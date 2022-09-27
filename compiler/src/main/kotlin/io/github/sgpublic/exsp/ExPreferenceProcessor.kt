package io.github.sgpublic.exsp

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import io.github.sgpublic.exsp.annotations.ExSharedPreference
import io.github.sgpublic.exsp.annotations.ExValue
import io.github.sgpublic.exsp.util.*
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.tools.Diagnostic

@AutoService(Processor::class)
class ExPreferenceProcessor: AbstractProcessor() {
    companion object {
        lateinit var mFiler: Filer private set
        lateinit var mMessager: Messager private set
        lateinit var ExPreference: TypeElement private set

        private lateinit var Context: DeclaredType
        private lateinit var SharedPreferences: TypeElement
    }

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mFiler = processingEnv.filer
        mMessager = processingEnv.messager
        Context = processingEnv.getType("android.content.Context")
        ExPreference = processingEnv.getElement("io.github.sgpublic.exsp.ExPreference")
        SharedPreferences = processingEnv.getElement("android.content.SharedPreferences")
    }

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        if (set.isEmpty()) {
            return false
        }
        try {
            val elements: Set<Element> = roundEnvironment.getElementsAnnotatedWith(ExSharedPreference::class.java)

            for (element: Element in elements) {
                if (element !is TypeElement) {
                    continue
                }

                val anno = element.getAnnotation(ExSharedPreference::class.java)

                val origin = element.simpleName.toString()
                val spName = "\"" + anno.name + "\""

                val pkg = element.qualifiedName.let {
                    val tmp = it.substring(0, it.length - origin.length)
                    if (tmp.last() == '.') {
                        return@let tmp.substring(0, tmp.length - 1)
                    } else {
                        return@let tmp
                    }
                }

                val impl = TypeSpec.classBuilder(origin + "_Impl")
                    .superclass(ClassName.get(element))
                    .addModifiers(Modifier.PUBLIC)

                MethodSpec.methodBuilder("getSharedPreference")
                    .addModifiers(Modifier.PRIVATE)
                    .addStatement("return io.github.sgpublic.exsp.ExPreference" +
                            ".getSharedPreference($spName, ${anno.mode})")
                    .returns(ClassName.get(SharedPreferences.asType()))
                    .let {
                        impl.addMethod(it.build())
                    }

                MethodSpec.methodBuilder("hashCode")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return super.hashCode()")
                    .returns(Int::class.java)
                    .let {
                        impl.addMethod(it.build())
                    }

                MethodSpec.methodBuilder("equals")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(Objects::class.java, "o")
                    .addStatement("return super.equals(o)")
                    .returns(Boolean::class.java)
                    .let {
                        impl.addMethod(it.build())
                    }


                val fields = element.getEnclosedElements()
                for (field: Element in fields) {
                    val defVal = field.getAnnotation(ExValue::class.java)?.defVal
                    if (field !is VariableElement || defVal == null) {
                        continue
                    }
                    if (field.modifiers.contains(Modifier.FINAL)) {
                        continue
                    }
                    val type = ClassName.get(field.asType())
                    if (!type.supported()) {
                        mMessager.printMessage(Diagnostic.Kind.WARNING, type.toString())
                        continue
                    }

                    val name = field.getAnnotation(ExValue::class.java).key.takeIf { it != "" }
                        ?: field.simpleName.toString().replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        }
                    val conf: String = "\"" + name + "\""

                    val getter = MethodSpec.methodBuilder(field.getterName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(type)
                    getter.addStatement("SharedPreferences sp = getSharedPreference()")

                    val setter = MethodSpec.methodBuilder(field.setterName())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(type, "value")
                        .addStatement("SharedPreferences.Editor editor = getSharedPreference().edit()")

                    when (SharedPreferenceType.of(type)) {
                        SharedPreferenceType.BOOLEAN -> {
                            getter.addStatement("return sp.getBoolean($conf, $defVal)")
                            setter.addStatement("editor.putBoolean($conf, value)")
                        }
                        SharedPreferenceType.INT -> {
                            getter.addStatement("return sp.getInt($conf, $defVal)")
                            setter.addStatement("editor.putInt($conf, value)")
                        }
                        SharedPreferenceType.LONG -> {
                            getter.addStatement("return sp.getLong($conf, $defVal)")
                            setter.addStatement("editor.putLong($conf, value)")
                        }
                        SharedPreferenceType.FLOAT -> {
                            getter.addStatement("return sp.getFloat($conf, $defVal)")
                            setter.addStatement("editor.putFloat($conf, value)")
                        }
                        SharedPreferenceType.STRING -> {
                            getter.addStatement("return sp.getString($conf, \"$defVal\")")
                            setter.addStatement("editor.putString($conf, value)")
                        }
                    }
                    setter.addStatement("editor.apply()")
                    impl.addMethod(getter.build())
                    impl.addMethod(setter.build())
                }

                JavaFile.builder(pkg, impl.build())
                    .build().writeTo(mFiler)
            }
            return true
        } catch (e: Exception) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "${e.message}\n${e.stackTraceToString()}")
            return false
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ExSharedPreference::class.qualifiedName!!)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_11
    }
}