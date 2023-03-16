package io.github.sgpublic.xxpref

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.sun.tools.javac.api.JavacTrees
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.tree.TreeMaker
import com.sun.tools.javac.util.Names
import io.github.sgpublic.xxpref.annotations.ExConverter
import io.github.sgpublic.xxpref.annotations.ExSharedPreference
import io.github.sgpublic.xxpref.core.ConverterCompiler
import io.github.sgpublic.xxpref.core.PreferenceCompiler
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

        val mTrees: JavacTrees by lazy { JavacTrees.instance(processingEnv) }
        val mTreeMaker: TreeMaker by lazy {
            TreeMaker.instance((processingEnv as JavacProcessingEnvironment).context)
        }
        val mNames: Names by lazy {
            Names.instance((processingEnv as JavacProcessingEnvironment).context)
        }

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
            ExSharedPreference::class.qualifiedName!!,
            ExConverter::class.qualifiedName!!,
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_11
    }
}