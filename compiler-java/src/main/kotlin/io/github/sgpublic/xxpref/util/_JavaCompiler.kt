@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref.util

import jdk.internal.misc.Unsafe
import javax.annotation.processing.AbstractProcessor

/**
 * @see <a href="https://github.com/projectlombok/lombok/blob/9806e5cca4b449159ad0509dafde81951b8a8523/src/core/lombok/javac/apt/LombokProcessor.java">LombokProcesser.java</a>
 */
fun <T: AbstractProcessor> T.addOpens() {
	val jModule: Class<*> = try {
		Class.forName("java.lang.Module")
	} catch (e: ClassNotFoundException) {
		return
	}
	val unsafe: Unsafe = try {
		Unsafe::class.java.getDeclaredField("theUnsafe").also {
			it.isAccessible = true
		}.get(null) as Unsafe
	} catch (e: Exception) {
		return
	}
	val jcModule: Any? = try {
	    Class.forName("java.lang.ModuleLaye").let {
			val bootLayer = it.getDeclaredMethod("boot").invoke(null)
			val findModule = it.getDeclaredMethod("findModule", String::class.java)
			return@let Class.forName("java.util.Optional").getDeclaredMethod("get").invoke(
				findModule.invoke(bootLayer, "jdk.compiler")
			)
		}
	} catch (e: Exception) {
		null
	}
	val ownModule: Any? = try {
	    Class::class.java.getMethod("getModule").invoke(javaClass)
	} catch (e: Exception) {
		null
	}
	val requiredPackage = listOf(
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