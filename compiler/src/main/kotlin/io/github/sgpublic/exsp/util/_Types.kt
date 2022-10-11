package io.github.sgpublic.exsp.util

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import io.github.sgpublic.exsp.ExPreferenceProcessor
import java.util.*
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

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
    add(StringTypeSetOrigin)
    return this
}

val StringTypeOrigin: ClassName = ClassName.get("java.lang", "String")
val StringTypeSetOrigin: TypeName = ParameterizedTypeName.get(Set::class.java, String::class.java)

private val BooleanType = hashSetOf(
    ClassName.BOOLEAN
).andBoxed()
private val IntType = hashSetOf(
    ClassName.INT
).andBoxed()
private val LongType = hashSetOf(
    ClassName.LONG
).andBoxed()
private val FloatType = hashSetOf(
    ClassName.FLOAT
).andBoxed()
private val StringType = hashSetOf(
    StringTypeOrigin
)
private val StringSetType = hashSetOf(
    StringTypeSetOrigin
)


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

private val obj = ExPreferenceProcessor.getElement("java.lang.Object")
private val enu = ExPreferenceProcessor.getElement("java.lang.Enum")
fun VariableElement.isEnum(): Boolean {
    var asElement = ExPreferenceProcessor.asElement(asType()) ?: return false
    while (asElement.superclass != null) {
        ExPreferenceProcessor.mMessager.printMessage(Diagnostic.Kind.WARNING, "asElement: $asElement")
        if (asElement == enu) {
            return true
        }
        if (asElement == obj) {
            break
        }
        asElement = ExPreferenceProcessor.asElement(asElement.superclass) ?: break
    }
    return false
}

enum class SharedPreferenceType {
    BOOLEAN, INT, LONG, FLOAT, STRING, STRING_SET;

    companion object {
        fun of(type: TypeName): SharedPreferenceType {
            return if (BooleanType.contains(type)) {
                BOOLEAN
            } else if (IntType.contains(type)) {
                INT
            } else if (LongType.contains(type)) {
                LONG
            } else if (FloatType.contains(type)) {
                FLOAT
            } else if (StringType.contains(type)) {
                STRING
            } else if (StringSetType.contains(type)) {
                STRING_SET
            } else {
                throw Exception("Unsupported type: $type")
            }
        }
    }
}