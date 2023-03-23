@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref.util

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import com.sun.tools.javac.tree.JCTree.JCExpression
import io.github.sgpublic.xxpref.jc.TypeElementImpl
import io.github.sgpublic.xxpref.jc.mNames
import io.github.sgpublic.xxpref.jc.mTreeMaker
import java.util.*
import com.sun.tools.javac.util.Name
import javax.lang.model.element.VariableElement

fun HashSet<TypeName>.andBoxed(): HashSet<TypeName> {
    val set = HashSet<TypeName>()
    for (typeName in this) {
        try {
            set.add(typeName.box())
        } catch (_: Exception) { }
    }
    addAll(set)
    return this
}

fun HashSet<TypeName>.andString(): HashSet<TypeName> {
    add(StringTypeOrigin)
    return this
}

val StringTypeOrigin: ClassName = ClassName.get("java.lang", "String")

private val BooleanType = hashSetOf(
    ClassName.BOOLEAN
).andBoxed()
val TypeName.isBoolean get() = BooleanType.contains(this)
private val IntType = hashSetOf(
    ClassName.INT
).andBoxed()
val TypeName.isInt get() = IntType.contains(this)
private val LongType = hashSetOf(
    ClassName.LONG
).andBoxed()
val TypeName.isLong get() = LongType.contains(this)
private val FloatType = hashSetOf(
    ClassName.FLOAT
).andBoxed()
val TypeName.isFloat get() = FloatType.contains(this)
private val StringType = hashSetOf(
    StringTypeOrigin
)
val TypeName.isString get() = StringType.contains(this)


private val supported = hashSetOf(
    ClassName.BOOLEAN,
    ClassName.INT,
    ClassName.LONG,
    ClassName.FLOAT,
).andBoxed().andString()

fun TypeName.supported(): Boolean {
    return supported.contains(this)
}

fun VariableElement.getterName(): String {
    val name = simpleName.toString().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
    }
    return if (BooleanType.contains(ClassName.get(asType()))) {
        "is$name"
    } else {
        "get$name"
    }
}

fun VariableElement.setterName(): String {
    val name = simpleName.toString().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
    }
    return "set$name"
}

private val obj = TypeElementImpl("java.lang.Object")
private val enu = TypeElementImpl("java.lang.Enum")
fun VariableElement.isEnum(): Boolean {
    var asElement = TypeElementImpl(asType()) ?: return false
    while (asElement.superclass != null) {
        if (asElement == enu) {
            return true
        }
        if (asElement == obj) {
            break
        }
        asElement = TypeElementImpl(asElement.superclass) ?: break
    }
    return false
}

enum class SharedPreferenceType {
    Boolean, Int, Long, Float, String;

    fun getStatement(key: JCExpression, defVal: JCExpression): JCExpression {
        return mTreeMaker.Apply(
            com.sun.tools.javac.util.List.nil(),
            mTreeMaker.Select(
                mTreeMaker.Ident(mNames.fromString("sp")),
                mNames.fromString("get${name}")
            ),
            com.sun.tools.javac.util.List.of(key, defVal)
        )
    }

    fun putStatement(target: Name, key: JCExpression, value: JCExpression): JCExpression {
        return mTreeMaker.Apply(
            com.sun.tools.javac.util.List.nil(),
            mTreeMaker.Select(
                mTreeMaker.Ident(target),
                mNames.fromString("put${name}")
            ),
            com.sun.tools.javac.util.List.of(key, value)
        )
    }

    companion object {
        fun of(type: TypeName): SharedPreferenceType {
            return if (BooleanType.contains(type)) {
                Boolean
            } else if (IntType.contains(type)) {
                Int
            } else if (LongType.contains(type)) {
                Long
            } else if (FloatType.contains(type)) {
                Float
            } else if (StringType.contains(type)) {
                String
            } else {
                throw Exception("Unsupported type: $type")
            }
        }
    }
}