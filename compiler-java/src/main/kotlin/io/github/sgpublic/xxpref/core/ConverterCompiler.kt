package io.github.sgpublic.xxpref.core

import com.squareup.javapoet.*
import io.github.sgpublic.xxpref.XXPrefProcessor
import io.github.sgpublic.xxpref.annotations.PrefConverter
import io.github.sgpublic.xxpref.interfaces.Converter
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

object ConverterCompiler {
    private val targets: HashMap<String, TypeElement> = hashMapOf()

    fun apply(env: RoundEnvironment) {
        val impl = TypeSpec.classBuilder("ExConverters")
            .addModifiers(Modifier.PUBLIC)

        val any = TypeVariableName.get("?")
        val OriginT = TypeVariableName.get("OriginT")
        val TargetT = TypeVariableName.get("TargetT")
        val anyClass = ParameterizedTypeName.get(ClassName.get(Class::class.java), any)
        val originClass = ParameterizedTypeName.get(ClassName.get(Class::class.java), OriginT)
        val anyConverter = ParameterizedTypeName.get(ClassName.get(Converter::class.java), any, any)
        val knownConverter = ParameterizedTypeName.get(ClassName.get(Converter::class.java), OriginT, TargetT)

        val convCollect = FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Map::class.java),
            anyClass, ParameterizedTypeName.get(ClassName.get(Lazy::class.java), anyConverter)),
            "converters", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL
        ).initializer("new \$T<>()", HashMap::class.java).build()
        impl.addField(convCollect)

        val originClazzParam = ParameterSpec.builder(originClass, "clazz").build()
        val getConverter = MethodSpec.methodBuilder("getConverter")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
            .addTypeVariables(listOf(OriginT, TargetT))
            .addParameter(originClazzParam)
            .beginControlFlow(
                "if (!\$T.\$N.containsKey(\$N))",
                XXPrefProcessor.ExConverters, convCollect, originClazzParam
            )
            .addStatement(
                "throw new \$T(\"Cannot find converter for \" + \$N + \", " +
                        "have you created its converter and added @ExConverter?\")",
                IllegalStateException::class.java,
                originClazzParam
            )
            .endControlFlow()
            .addStatement(
                "return (\$T<\$T, \$T>) \$T.\$N.get(\$N).getValue()",
                Converter::class.java, OriginT, TargetT,
                XXPrefProcessor.ExConverters, convCollect, originClazzParam
            )
            .returns(knownConverter)
            .build()
        impl.addMethod(getConverter)

        val value = ParameterSpec.builder(OriginT, "value").build()
        val name = "converter"
        MethodSpec.methodBuilder("toPreference")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addTypeVariables(listOf(OriginT, TargetT))
            .addParameter(originClazzParam)
            .addParameter(value)
            .addStatement("\$T \$N = \$T.\$N(\$N)", knownConverter,
                name, XXPrefProcessor.ExConverters, getConverter, originClazzParam)
            .addStatement("return \$N.toPreference(\$N)", name, value)
            .returns(TargetT)
            .let {
                impl.addMethod(it.build())
            }

        MethodSpec.methodBuilder("fromPreference")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addTypeVariables(listOf(OriginT, TargetT))
            .addParameter(originClazzParam)
            .addParameter(ParameterSpec.builder(TargetT, "value").build())
            .returns(OriginT)
            .addStatement("\$T<\$T, \$T> converter = \$T.\$N(\$N)",
                Converter::class.java, OriginT, TargetT, XXPrefProcessor.ExConverters,
                getConverter, originClazzParam)
            .addStatement("return converter.fromPreference(value)",
                Converter::class.java, OriginT, TargetT, XXPrefProcessor.ExConverters)
            .let {
                impl.addMethod(it.build())
            }

        val originFunction0 = ParameterizedTypeName.get(ClassName.get(Function0::class.java), anyConverter)

        val static = CodeBlock.builder()
        for (element: Element in env.getElementsAnnotatedWith(PrefConverter::class.java)) {
            if (element !is TypeElement) {
                continue
            }
            val typeParam = findTargetType(element)
            targets[(typeParam.first.asElement() as TypeElement).qualifiedName.toString()] =
                typeParam.second.asElement() as TypeElement

            val invoke = MethodSpec.methodBuilder("invoke")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addStatement("return new \$T()", element)
                .returns(anyConverter)
                .build()
            static.addStatement("\$T.\$N.put(\$T.class, \$T.lazy(\$L))",
                XXPrefProcessor.ExConverters, convCollect, typeParam.first,
                ClassName.get("kotlin", "LazyKt"),
                TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(originFunction0)
                    .addMethod(invoke)
                    .build())
        }
        impl.addStaticBlock(static.build())

        val implObj = impl.build()
        JavaFile.builder("io.github.sgpublic.xxpref", implObj)
            .build().writeTo(XXPrefProcessor.mFiler)
    }

    fun getTarget(type: TypeElement): TypeElement {
        val name = type.qualifiedName.toString()
        return targets[name] ?: throw IllegalStateException("No converter for $name, " +
                "have you created its converter and added @ExConverter?")
    }

    private val ConverterName: String = Converter::class.qualifiedName!!
    private fun findTargetType(element: TypeElement): Pair<DeclaredType, DeclaredType> {
        var base = element
        while (true) {
            for (mirror: TypeMirror in base.interfaces) {
                if (mirror !is DeclaredType) {
                    continue
                }
                val type = XXPrefProcessor.asElement(mirror)!!
                if (ConverterName == type.qualifiedName?.toString()) {
                    return (mirror.typeArguments[0] as DeclaredType) to (mirror.typeArguments[1] as DeclaredType)
                }
            }
            base = XXPrefProcessor.asElement(base.superclass) ?: throw IllegalStateException(
                "Cannot find Converter generic param of ${element.qualifiedName}, " +
                        "you can only use @ExConverter On the subclass of io.github.sgpublic.xxpref.Converter!"
            )
        }
    }
}