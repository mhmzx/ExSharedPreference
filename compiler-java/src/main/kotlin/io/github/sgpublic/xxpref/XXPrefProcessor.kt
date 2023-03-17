@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.sun.tools.javac.tree.TreeMaker
import com.sun.tools.javac.util.Names
import io.github.sgpublic.xxpref.annotations.PrefConverter
import io.github.sgpublic.xxpref.annotations.XXPreference
import io.github.sgpublic.xxpref.core.ConverterCompiler
import io.github.sgpublic.xxpref.core.PreferenceCompiler
import io.github.sgpublic.xxpref.jc.lazyInstanceWithContext
import io.github.sgpublic.xxpref.util.addOpens
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

@AutoService(Processor::class)
class XXPrefProcessor: AbstractProcessor() {
    companion object {
        val mFiler: Filer by lazy { processingEnv.filer }
        val mMessager: Messager by lazy { processingEnv.messager }

        val mTreeMaker: TreeMaker by TreeMaker::class.lazyInstanceWithContext()
        val mNames: Names by Names::class.lazyInstanceWithContext()

        val ExPreference: TypeElement by lazy { getElement("io.github.sgpublic.xxpref.ExPreference") }
        val ExConverters: ClassName by lazy { ClassName.get("io.github.sgpublic.xxpref", "ExConverters") }

        val SharedPreferences: DeclaredType by lazy { getType("android.content.SharedPreferences") }
        val OnSharedPreferenceChangeListener: DeclaredType by lazy {
            getType("android.content.SharedPreferences.OnSharedPreferenceChangeListener")
        }
        val SharedPreferenceReference: ClassName by lazy {
            ClassName.get("io.github.sgpublic.xxpref", "ExPreference.Reference")
        }

        private lateinit var processingEnv: ProcessingEnvironment
        val PROCESS_ENV: ProcessingEnvironment get() = processingEnv

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
        XXPrefProcessor.processingEnv = processingEnv
        this.addOpens()
    }

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        if (set.isNotEmpty()) {
            try {
                ConverterCompiler.apply(roundEnvironment)
                PreferenceCompiler.apply(roundEnvironment)
            } catch (e: Exception) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "${e.message}\n${e.stackTraceToString()}")
            }
        }
        return false
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            XXPreference::class.qualifiedName!!,
            PrefConverter::class.qualifiedName!!,
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_11
    }
}