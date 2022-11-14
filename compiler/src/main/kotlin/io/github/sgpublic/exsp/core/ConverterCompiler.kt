package io.github.sgpublic.exsp.core

import com.squareup.javapoet.*
import io.github.sgpublic.exsp.ExPreferenceProcessor
import io.github.sgpublic.exsp.annotations.ExConverter
import io.github.sgpublic.exsp.interfaces.Converter
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

object ConverterCompiler {
    private val converters: HashMap<String, TypeElement> = hashMapOf()
    private val targets: HashMap<String, TypeElement> = hashMapOf()

    fun apply(env: RoundEnvironment) {
        val impl = TypeSpec.classBuilder("ExConverters")
            .addModifiers(Modifier.PUBLIC)

        val any = TypeVariableName.get("?")
        val OriginT = TypeVariableName.get("OriginT")
        val TargetT = TypeVariableName.get("TargetT")
        val anyClass = ParameterizedTypeName.get(ClassName.get(Class::class.java), any)
        val anyConverter = ParameterizedTypeName.get(ClassName.get(Converter::class.java), any, any)
        val extendsConverterClass = ParameterizedTypeName.get(ClassName.get(Class::class.java),
            WildcardTypeName.subtypeOf(ParameterizedTypeName.get(ClassName.get(Converter::class.java), any, any)))
        val originClass = ParameterizedTypeName.get(ClassName.get(Class::class.java), OriginT)
        val knownConverter = ParameterizedTypeName.get(ClassName.get(Converter::class.java), OriginT, TargetT)

        FieldSpec.builder(
            ParameterizedTypeName.get(ClassName.get(Map::class.java), anyClass, anyConverter),
            "converters", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL
        ).initializer("new \$T<>()", HashMap::class.java).let {
            impl.addField(it.build())
        }

        FieldSpec.builder(
            ParameterizedTypeName.get(ClassName.get(Map::class.java), anyClass, extendsConverterClass),
            "registry", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL
        ).initializer("new \$T<>()", HashMap::class.java).let {
            impl.addField(it.build())
        }

        val originClazzParam = ParameterSpec.builder(originClass, "clazz").build()
        MethodSpec.methodBuilder("getConverter")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
            .addTypeVariables(listOf(OriginT, TargetT))
            .addParameter(originClazzParam)
            .beginControlFlow("if (!\$T.registry.containsKey(\$N))", ExPreferenceProcessor.ExConverters, originClazzParam)
            .addStatement("throw new \$T(\"Cannot find converter for \" + \$N + \", " +
                    "have you created its converter and added @ExConverter?\")", IllegalStateException::class.java, originClazzParam)
            .endControlFlow()
            .beginControlFlow("if (!\$T.converters.containsKey(\$N))", ExPreferenceProcessor.ExConverters, originClazzParam)
            .beginControlFlow("try")
            .addStatement("\$T.converters.put(clazz, \$T.registry.get(\$N).newInstance())",
                ExPreferenceProcessor.ExConverters, ExPreferenceProcessor.ExConverters, originClazzParam)
            .nextControlFlow("catch (IllegalAccessException | InstantiationException e)")
            .addStatement("throw new \$T(\"Failed to create instance for \" + \$T.registry.get(\$N) + \"!\")",
                RuntimeException::class.java, ExPreferenceProcessor.ExConverters, originClazzParam)
            .endControlFlow()
            .endControlFlow()
            .addStatement("return (\$T<\$T, \$T>) \$T.converters.get(\$N)",
                Converter::class.java, OriginT, TargetT, ExPreferenceProcessor.ExConverters, originClazzParam)
            .returns(knownConverter)
            .let {
                impl.addMethod(it.build())
            }

        MethodSpec.methodBuilder("toPreference")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addTypeVariables(listOf(OriginT, TargetT))
            .addParameter(originClazzParam)
            .addParameter(ParameterSpec.builder(OriginT, "value").build())
            .returns(TargetT)
            .addStatement("\$T<\$T, \$T> converter = \$T.getConverter(\$N)",
                Converter::class.java, OriginT, TargetT, ExPreferenceProcessor.ExConverters, originClazzParam)
            .addStatement("return converter.toPreference(value)",
                Converter::class.java, OriginT, TargetT, ExPreferenceProcessor.ExConverters)
            .let {
                impl.addMethod(it.build())
            }

        MethodSpec.methodBuilder("fromPreference")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addTypeVariables(listOf(OriginT, TargetT))
            .addParameter(originClazzParam)
            .addParameter(ParameterSpec.builder(TargetT, "value").build())
            .returns(OriginT)
            .addStatement("\$T<\$T, \$T> converter = \$T.getConverter(\$N)",
                Converter::class.java, OriginT, TargetT, ExPreferenceProcessor.ExConverters, originClazzParam)
            .addStatement("return converter.fromPreference(value)",
                Converter::class.java, OriginT, TargetT, ExPreferenceProcessor.ExConverters)
            .let {
                impl.addMethod(it.build())
            }

        val static = CodeBlock.builder()
        for (element: Element in env.getElementsAnnotatedWith(ExConverter::class.java)) {
            if (element !is TypeElement) {
                continue
            }
            val typeParam = findTargetType(element)
            val name = (typeParam.first.asElement() as TypeElement).qualifiedName.toString()
            targets[name] = typeParam.second.asElement() as TypeElement
            converters[name] = element
            static.addStatement("\$T.registry.put(\$T.class, \$T.class)",
                ExPreferenceProcessor.ExConverters, typeParam.first, element)
        }
        impl.addStaticBlock(static.build())

        val implObj = impl.build()
        JavaFile.builder("io.github.sgpublic.exsp", implObj)
            .build().writeTo(ExPreferenceProcessor.mFiler)
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
                mirror as DeclaredType
                val type = ExPreferenceProcessor.asElement(mirror)!!
                if (ConverterName == type.qualifiedName?.toString()) {
                    return (mirror.typeArguments[0] as DeclaredType) to (mirror.typeArguments[1] as DeclaredType)
                }
            }
            base = ExPreferenceProcessor.asElement(base.superclass) ?: throw IllegalStateException(
                "Cannot find Converter generic param of ${element.qualifiedName}, " +
                        "you can only use @ExConverter On the subclass of io.github.sgpublic.exsp.Converter!"
            )
        }
    }
}