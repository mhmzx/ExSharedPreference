package io.github.sgpublic.exsp.util

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import java.lang.Character.isLowerCase
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.UnknownTypeException
import kotlin.collections.HashSet

fun ProcessingEnvironment.getType(name: String): DeclaredType {
    return typeUtils.getDeclaredType(getElement(name))
}

fun ProcessingEnvironment.getElement(name: String): TypeElement {
    return elementUtils.getTypeElement(name)
}

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


fun HashSet<TypeName>.andKotlin(): HashSet<TypeName> {
    for (typeName in this) {
        when (typeName) {
            ClassName.BOOLEAN -> add(TypeName.get(Boolean::class.java))
            ClassName.FLOAT -> add(TypeName.get(Float::class.java))
            ClassName.INT -> add(TypeName.get(Int::class.java))
            ClassName.LONG -> add(TypeName.get(Long::class.java))
            StringTypeOrigin -> add(TypeName.get(String::class.java))
        }
    }
    return this
}

fun HashSet<TypeName>.andString(): HashSet<TypeName> {
    add(StringTypeOrigin)
    return this
}

private val StringTypeOrigin: TypeName = ClassName.get("java.lang", "String")

private val BooleanType = hashSetOf(
    ClassName.BOOLEAN
).andBoxed().andKotlin()
private val IntType = hashSetOf(
    ClassName.INT
).andBoxed().andKotlin()
private val LongType = hashSetOf(
    ClassName.LONG
).andBoxed().andKotlin()
private val FloatType = hashSetOf(
    ClassName.FLOAT
).andBoxed().andKotlin()
private val StringType = hashSetOf(
    StringTypeOrigin
).andKotlin()


private val supported = hashSetOf(
    ClassName.BOOLEAN,
    ClassName.INT,
    ClassName.LONG,
    ClassName.FLOAT,
).andBoxed().andString().andKotlin()

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

enum class SharedPreferenceType {
    BOOLEAN, INT, LONG, FLOAT, STRING;

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
            } else {
                throw Exception("Unsupported type: $type")
            }
        }
    }
}