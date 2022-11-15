package io.github.sgpublic.exsp

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import io.github.sgpublic.exsp.annotations.ExConverter
import io.github.sgpublic.exsp.annotations.ExSharedPreference
import io.github.sgpublic.exsp.core.ConverterCompiler
import io.github.sgpublic.exsp.core.PreferenceCompiler
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

@AutoService(Processor::class)
class ExPreferenceProcessor: AbstractProcessor() {
    companion object {
        lateinit var mFiler: Filer private set
        lateinit var mMessager: Messager private set
        lateinit var ExPreference: TypeElement private set
        lateinit var ExConverters: ClassName private set

        lateinit var SharedPreferences: DeclaredType private set
        lateinit var SharedPreferenceReference: ClassName private set

        private lateinit var processingEnv: ProcessingEnvironment

        fun getType(name: String): DeclaredType {
            return processingEnv.typeUtils.getDeclaredType(getElement(name))
        }

        fun asElement(type: TypeMirror?): TypeElement? {
            return processingEnv.typeUtils.asElement(type ?: return null) as TypeElement?
        }

        fun getElement(name: String): TypeElement {
            return processingEnv.elementUtils.getTypeElement(name)
        }
    }

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        ExPreferenceProcessor.processingEnv = processingEnv
        mFiler = processingEnv.filer
        mMessager = processingEnv.messager
        ExPreference = getElement("io.github.sgpublic.exsp.ExPreference")
        ExConverters = ClassName.get("io.github.sgpublic.exsp", "ExConverters")
        SharedPreferences = getType("android.content.SharedPreferences")
        SharedPreferenceReference = ClassName.get("io.github.sgpublic.exsp", "ExPreference.Reference")
    }

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        if (set.isEmpty()) {
            return false
        }
        return try {
            ConverterCompiler.apply(roundEnvironment)
            PreferenceCompiler.apply(roundEnvironment)
            true
        } catch (e: Exception) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "${e.message}\n${e.stackTraceToString()}")
            false
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            ExSharedPreference::class.qualifiedName!!,
            ExConverter::class.qualifiedName!!,
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_11
    }
}