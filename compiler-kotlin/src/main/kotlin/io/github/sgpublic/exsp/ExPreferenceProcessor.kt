package io.github.sgpublic.exsp

import com.google.auto.service.AutoService
import io.github.sgpublic.exsp.annotations.ExConverter
import io.github.sgpublic.exsp.annotations.ExSharedPreference
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class ExPreferenceProcessor: AbstractProcessor() {
    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)

    }

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        if (set.isEmpty()) {
            return false
        }
        return false
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