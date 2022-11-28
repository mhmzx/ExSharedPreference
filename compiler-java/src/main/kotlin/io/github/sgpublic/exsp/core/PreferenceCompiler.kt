package io.github.sgpublic.exsp.core

import com.squareup.javapoet.*
import io.github.sgpublic.exsp.ExPreferenceProcessor
import io.github.sgpublic.exsp.ExPreferenceProcessor.Companion.ExPreference
import io.github.sgpublic.exsp.ExPreferenceProcessor.Companion.SharedPreferenceReference
import io.github.sgpublic.exsp.ExPreferenceProcessor.Companion.SharedPreferences
import io.github.sgpublic.exsp.annotations.ExSharedPreference
import io.github.sgpublic.exsp.annotations.ExValue
import io.github.sgpublic.exsp.util.*
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

object PreferenceCompiler {
    fun apply(env: RoundEnvironment) {
        val any = TypeVariableName.get("?")
        val anyClass = ParameterizedTypeName.get(ClassName.get(Class::class.java), any)
        val anyLazy = ParameterizedTypeName.get(ClassName.get(Lazy::class.java), any)

        val prefsName = ClassName.get("io.github.sgpublic.exsp", "ExPrefs")
        val prefsClazz = TypeSpec.classBuilder(prefsName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        val prefs = FieldSpec.builder(
            ParameterizedTypeName.get(ClassName.get(Map::class.java), anyClass, anyLazy),
            "prefs", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL
        ).initializer("new \$T<>()", HashMap::class.java).build()
        prefsClazz.addField(prefs)

        val PrefT = TypeVariableName.get("PrefT")
        val prefClass = ParameterizedTypeName.get(ClassName.get(Class::class.java), PrefT)
        val lazyPref = ParameterizedTypeName.get(ClassName.get(Lazy::class.java), PrefT)
        val clazz = ParameterSpec.builder(prefClass, "clazz").build()
        MethodSpec.methodBuilder("get")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addTypeVariable(PrefT)
            .addParameter(clazz)
            .beginControlFlow("if (\$T.\$N.containsKey(\$N))", prefsName, prefs, clazz)
            .addStatement("return (\$T) \$T.\$N.get(\$N)", lazyPref, prefsName, prefs, clazz)
            .endControlFlow()
            .addStatement("throw new \$T(\"Unknown ExPreference type, did you add @ExPreference?\")", IllegalStateException::class.java)
            .returns(lazyPref)
            .let {
                prefsClazz.addMethod(it.build())
            }

        val static = CodeBlock.builder()

        for (element: Element in env.getElementsAnnotatedWith(ExSharedPreference::class.java)) {
            if (element !is TypeElement) {
                continue
            }
            static.addStatement("\$T.\$N.put(\$L)", prefsName, prefs, applySingle(element))
        }

        prefsClazz.addStaticBlock(static.build())

        JavaFile.builder("io.github.sgpublic.exsp", prefsClazz.build())
            .build().writeTo(ExPreferenceProcessor.mFiler)
    }

    private fun applySingle(element: TypeElement): CodeBlock {
        val anno = element.getAnnotation(ExSharedPreference::class.java)

        val originType = ClassName.get(element)
        val origin = element.simpleName.toString()

        val pkg = element.qualifiedName.let {
            val tmp = it.substring(0, it.length - origin.length)
            if (tmp.last() == '.') {
                return@let tmp.substring(0, tmp.length - 1)
            } else {
                return@let tmp
            }
        }

        val implName = "${origin}_Impl"
        val impl = TypeSpec.classBuilder(implName)
            .superclass(originType)
            .addModifiers(Modifier.PUBLIC)
        val implType = ClassName.get(pkg, implName)

        val reference = FieldSpec.builder(SharedPreferenceReference, "SharedPreferenceReference")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("\$T.getSharedPreference(\$S, ${anno.mode})", ExPreference,
                anno.name.takeIf { it.isNotBlank() } ?: element.qualifiedName)
            .build()
        impl.addField(reference)

        MethodSpec.methodBuilder("hashCode")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addStatement("return super.hashCode()")
            .returns(Int::class.java)
            .let {
                impl.addMethod(it.build())
            }

        MethodSpec.methodBuilder("equals")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(Object::class.java, "o")
            .addAnnotation(Override::class.java)
            .addStatement("return super.equals(o)")
            .returns(Boolean::class.java)
            .let {
                impl.addMethod(it.build())
            }

        for (field: Element in element.enclosedElements) {
            val defVal = field.getAnnotation(ExValue::class.java)?.defVal
            if (field !is VariableElement) {
                continue
            }
            if (field.modifiers.contains(Modifier.FINAL)) {
                continue
            }
            val type: TypeName = ClassName.get(field.asType())

            val name = field.getAnnotation(ExValue::class.java).key.takeIf { it != "" }
                ?: field.simpleName.toString().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                }
            val conf: String = "\"" + name + "\""

            val getter = MethodSpec.methodBuilder(field.getterName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .returns(type)
            getter.addStatement("\$T sp = \$N.getValue()", SharedPreferences, reference)

            val setter = MethodSpec.methodBuilder(field.setterName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .addParameter(type, "value")
                .addStatement("SharedPreferences.Editor editor = SharedPreferenceReference.edit()")

            var convertedType = type
            if (type.supported()) {
                setter.addStatement("\$T converted = value", type)
                getter.addStatement("\$T origin", type)
            } else if (field.isEnum()) {
                convertedType = StringTypeOrigin
                setter.addStatement("\$T converted = value.name()", convertedType)
                getter.addStatement("\$T origin", convertedType)
            } else {
                val convertedElement = ConverterCompiler.getTarget(ExPreferenceProcessor.asElement(field.asType())!!)
                setter.addStatement("\$T converted = \$T.toPreference(\$T.class, value)",
                    convertedElement, ExPreferenceProcessor.ExConverters, type)
                convertedType = ClassName.get(convertedElement)
                getter.addStatement("\$T origin", convertedType)
            }

            try {
                when (SharedPreferenceType.of(convertedType)) {
                    SharedPreferenceType.BOOLEAN -> {
                        getter.addStatement("origin = sp.getBoolean($conf, $defVal)")
                        setter.addStatement("editor.putBoolean($conf, converted)")
                    }
                    SharedPreferenceType.INT -> {
                        getter.addStatement("origin = sp.getInt($conf, $defVal)")
                        setter.addStatement("editor.putInt($conf, converted)")
                    }
                    SharedPreferenceType.LONG -> {
                        getter.addStatement("origin = sp.getLong($conf, $defVal)")
                        setter.addStatement("editor.putLong($conf, converted)")
                    }
                    SharedPreferenceType.FLOAT -> {
                        getter.addStatement("origin = sp.getFloat($conf, $defVal)")
                        setter.addStatement("editor.putFloat($conf, value)")
                    }
                    SharedPreferenceType.STRING -> {
                        getter.addStatement("origin = sp.getString($conf, \"$defVal\")")
                        setter.addStatement("editor.putString($conf, converted)")
                    }
                }
            } catch (e: Exception) {
                if (!field.isEnum()) {
                    throw e
                }
            }

            if (type.supported()) {
                getter.addStatement("return origin")
            } else if (field.isEnum()) {
                getter.beginControlFlow("try")
                getter.addStatement("return \$T.valueOf(origin)", type)
                getter.nextControlFlow("catch (\$T ignore)", IllegalArgumentException::class.java)
                getter.addStatement("return \$T.valueOf(\"$defVal\")", type)
                getter.endControlFlow()
            } else {
                getter.addStatement("return \$T.fromPreference(\$T.class, origin)",
                    ExPreferenceProcessor.ExConverters, type)
            }
            setter.addStatement("editor.apply()")

            impl.addMethod(getter.build())
            impl.addMethod(setter.build())
        }

        JavaFile.builder(pkg, impl.build())
            .build().writeTo(ExPreferenceProcessor.mFiler)

        val invoke = MethodSpec.methodBuilder("invoke")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addStatement("return new \$T()", implType)
            .returns(originType)
            .build()
        val originFunction0 = ParameterizedTypeName.get(ClassName.get(Function0::class.java), originType)
        return CodeBlock.of("\$T.class, \$T.lazy(\$L)",
            originType, ClassName.get("kotlin", "LazyKt"),
            TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(originFunction0)
                .addMethod(invoke)
                .build()
        )
    }
}