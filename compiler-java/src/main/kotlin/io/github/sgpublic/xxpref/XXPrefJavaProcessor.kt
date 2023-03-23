@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref

import com.google.auto.service.AutoService
import io.github.sgpublic.xxpref.annotations.PrefConverter
import io.github.sgpublic.xxpref.annotations.XXPreference
import io.github.sgpublic.xxpref.core.ConverterCompiler
import io.github.sgpublic.xxpref.core.PreferenceCompiler
import io.github.sgpublic.xxpref.jc.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class XXPrefJavaProcessor: AbstractProcessor() {
    companion object {
        lateinit var processingEnvironment: ProcessingEnvironment private set
    }
    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
//        this.addOpens()
        processingEnvironment = processingEnv
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


    /**
     * @see <a href="https://github.com/projectlombok/lombok/blob/9806e5cca4b449159ad0509dafde81951b8a8523/src/core/lombok/javac/apt/LombokProcessor.java">LombokProcesser.java</a>
     */
    private fun addOpens() {
        val jModule: Class<*> = try {
            Class.forName("java.lang.Module")
        } catch (e: ClassNotFoundException) {
            return
        }
//        val unsafe: Unsafe = try {
//            Unsafe::class.java.getDeclaredField("theUnsafe").also {
//                it.isAccessible = true
//            }.get(null) as Unsafe
//        } catch (e: Exception) {
//            return
//        }
        val jcModule: Any = try {
            Class.forName("java.lang.ModuleLaye").let {
                val bootLayer = it.getDeclaredMethod("boot").invoke(null)
                val findModule = it.getDeclaredMethod("findModule", String::class.java)
                return@let Class.forName("java.util.Optional").getDeclaredMethod("get").invoke(
                    findModule.invoke(bootLayer, "jdk.compiler")
                )
            }
        } catch (e: Exception) {
            return
        }
        val ownModule: Any = try {
            Class::class.java.getMethod("getModule").invoke(javaClass)
        } catch (e: Exception) {
            return
        }
        val requiredPackage: List<String> = listOf(
            "com.sun.tools.javac.code",
            "com.sun.tools.javac.comp",
            "com.sun.tools.javac.file",
            "com.sun.tools.javac.main",
            "com.sun.tools.javac.model",
            "com.sun.tools.javac.parser",
            "com.sun.tools.javac.processing",
            "com.sun.tools.javac.tree",
            "com.sun.tools.javac.util",
            "com.sun.tools.javac.jvm"
        )
        try {
            jModule.getDeclaredMethod("implAddOpens", String::class.java, jModule).let {
                for (pkg in requiredPackage) {
                    it.invoke(jcModule, pkg, ownModule)
                }
            }
        } catch (_: Exception) { }
    }
}