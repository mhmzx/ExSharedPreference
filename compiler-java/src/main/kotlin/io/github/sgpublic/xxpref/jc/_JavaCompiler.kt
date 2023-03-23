@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref.jc

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import com.sun.tools.javac.api.JavacTrees
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.code.Type
import com.sun.tools.javac.code.TypeTag
import com.sun.tools.javac.model.JavacTypes
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeMaker
import com.sun.tools.javac.util.Names
import io.github.sgpublic.xxpref.XXPrefJavaProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

val mJavacProcessingEnvironment: JavacProcessingEnvironment by lazy {
	XXPrefJavaProcessor.processingEnvironment as JavacProcessingEnvironment
}

val mFiler: Filer by lazy { mJavacProcessingEnvironment.filer }
val mMessager: Messager by lazy { mJavacProcessingEnvironment.messager }
val mTypes: JavacTypes by lazy { mJavacProcessingEnvironment.typeUtils }
val mTrees: JavacTrees by lazy { JavacTrees.instance(mJavacProcessingEnvironment.context) }
val mTreeMaker: TreeMaker by lazy { TreeMaker.instance(mJavacProcessingEnvironment.context) }
val mNames: Names by lazy { Names.instance(mJavacProcessingEnvironment.context) }

fun DeclaredTypeImpl(name: String): DeclaredType {
	return mTypes.getDeclaredType(TypeElementImpl(name))
}

fun TypeImpl(clazz: TypeName): JCTree.JCExpression {
	if (clazz !is ClassName) {
		return mTreeMaker.TypeIdent(TypeTag.valueOf(
			clazz.toString().uppercase()
		))
	}
	return TypeImpl(clazz.canonicalName())
}

fun TypeImpl(type: TypeMirror): JCTree.JCExpression {
	return type.takeIf {
		it is Type.ClassType || it is Type.JCPrimitiveType
	}?.let {
		if (it is Type.JCPrimitiveType) {
			return@let mTreeMaker.Type(it.baseType())
		}
		return@let TypeImpl(it.toString())
	} ?: throw IllegalStateException("Cannot create type of ${type.javaClass}")
}

fun TypeImpl(type: String): JCTree.JCExpression {
	var result: JCTree.JCExpression? = null
	for (item in type.split(".")) {
		val tmp = mNames.fromString(item)
		result = if (result == null) {
			mTreeMaker.Ident(tmp)
		} else {
			mTreeMaker.Select(result, tmp)
		}
	}
	return result!!
}

fun TypeElementImpl(type: TypeMirror?): Symbol.ClassSymbol? {
	return mTypes.asElement(type ?: return null) as Symbol.ClassSymbol?
}

fun TypeElementImpl(name: String): Symbol.ClassSymbol {
	return mJavacProcessingEnvironment.elementUtils.getTypeElement(name)
}

object Types {
	val XXPref: ClassName by lazy { ClassName.get("io.github.sgpublic.xxpref", "XXPref") }
	val Converters: ClassName by lazy { ClassName.get("io.github.sgpublic.xxpref", "Converters") }
	val LazyPrefReference: ClassName by lazy { ClassName.get("io.github.sgpublic.xxpref", "LazyPrefReference") }
	val PrefEditor: ClassName by lazy { ClassName.get("io.github.sgpublic.xxpref", "PrefEditor") }

	val SharedPreferences: DeclaredType by lazy { DeclaredTypeImpl("android.content.SharedPreferences") }
	val SharedPreferencesEditor: DeclaredType by lazy { DeclaredTypeImpl("android.content.SharedPreferences.Editor") }
	val OnSharedPreferenceChangeListener: DeclaredType by lazy {
		DeclaredTypeImpl("android.content.SharedPreferences.OnSharedPreferenceChangeListener")
	}
}

fun TypeElement.toClassName(): ClassName {
	return ClassName.get(this)
}

fun DeclaredType.toClassName(): ClassName {
	return TypeElementImpl(this)!!.toClassName()
}