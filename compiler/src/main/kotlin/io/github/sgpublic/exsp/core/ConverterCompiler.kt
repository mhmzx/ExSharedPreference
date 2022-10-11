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
        val Origin = TypeVariableName.get("Origin")
        val Target = TypeVariableName.get("Target")
        val anyClass = ParameterizedTypeName.get(ClassName.get(Class::class.java), any)
        val anyConverter = ParameterizedTypeName.get(ClassName.get(Converter::class.java), any, any)
        val extendsConverterClass = ParameterizedTypeName.get(ClassName.get(Class::class.java),
            WildcardTypeName.subtypeOf(ParameterizedTypeName.get(ClassName.get(Converter::class.java), any, any)))
        val originClass = ParameterizedTypeName.get(ClassName.get(Class::class.java), Origin)
        val knownConverter = ParameterizedTypeName.get(ClassName.get(Converter::class.java), Origin, Target)

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

        MethodSpec.methodBuilder("getConverter")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
            .addTypeVariables(listOf(Origin, Target))
            .addParameter(ParameterSpec.builder(originClass, "clazz").build())
            .beginControlFlow("if (!\$T.registry.containsKey(clazz))", ExPreferenceProcessor.ExConverters)
            .addStatement("throw new \$T(\"Cannot find converter for \" + clazz + \", " +
                    "have you created its converter and added @ExConverter?\")", IllegalStateException::class.java)
            .endControlFlow()
            .beginControlFlow("if (!\$T.converters.containsKey(clazz))", ExPreferenceProcessor.ExConverters)
            .beginControlFlow("try")
            .addStatement("\$T.converters.put(clazz, \$T.registry.get(clazz).newInstance())",
                ExPreferenceProcessor.ExConverters, ExPreferenceProcessor.ExConverters)
            .nextControlFlow("catch (IllegalAccessException | InstantiationException e)")
            .addStatement("throw new \$T(\"Failed to create instance for \" + \$T.registry.get(clazz) + \"!\")",
                RuntimeException::class.java, ExPreferenceProcessor.ExConverters)
            .endControlFlow()
            .endControlFlow()
            .addStatement("return (\$T<\$T, \$T>) \$T.converters.get(clazz)",
                Converter::class.java, Origin, Target, ExPreferenceProcessor.ExConverters)
            .returns(knownConverter)
            .let {
                impl.addMethod(it.build())
            }

        MethodSpec.methodBuilder("toPreference")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addTypeVariables(listOf(Origin, Target))
            .addParameter(ParameterSpec.builder(originClass, "clazz").build())
            .addParameter(ParameterSpec.builder(Origin, "value").build())
            .returns(Target)
            .addStatement("\$T<\$T, \$T> converter = \$T.getConverter(clazz)",
                Converter::class.java, Origin, Target, ExPreferenceProcessor.ExConverters)
            .addStatement("return converter.toPreference(value)",
                Converter::class.java, Origin, Target, ExPreferenceProcessor.ExConverters)
            .let {
                impl.addMethod(it.build())
            }

        MethodSpec.methodBuilder("fromPreference")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addTypeVariables(listOf(Origin, Target))
            .addParameter(ParameterSpec.builder(originClass, "clazz").build())
            .addParameter(ParameterSpec.builder(Target, "value").build())
            .returns(Origin)
            .addStatement("\$T<\$T, \$T> converter = \$T.getConverter(clazz)",
                Converter::class.java, Origin, Target, ExPreferenceProcessor.ExConverters)
            .addStatement("return converter.fromPreference(value)",
                Converter::class.java, Origin, Target, ExPreferenceProcessor.ExConverters)
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