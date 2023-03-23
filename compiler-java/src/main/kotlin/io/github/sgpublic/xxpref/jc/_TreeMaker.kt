@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref.jc

import com.sun.tools.javac.code.Flags
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeMaker
import java.util.stream.Stream

fun TreeMaker.ParamModifiers(vararg modifiers: Int): JCTree.JCModifiers {
    return Modifiers(Stream.of(*modifiers.toTypedArray()).reduce { o, n ->
        return@reduce o or n
    }.orElse(0).toLong() or Flags.PARAMETER)
}

fun TreeMaker.VarModifiers(vararg modifiers: Int): JCTree.JCModifiers {
    return Modifiers(Stream.of(*modifiers.toTypedArray()).reduce { o, n ->
        return@reduce o or n
    }.orElse(0).toLong())
}

fun TreeMaker.ClassModifiers(vararg modifiers: Int): JCTree.JCModifiers {
    return Modifiers(Stream.of(*modifiers.toTypedArray()).reduce { o, n ->
        return@reduce o or n
    }.orElse(0).toLong())
}

fun TreeMaker.MethodModifiers(vararg modifiers: Int): JCTree.JCModifiers {
    return Modifiers(Stream.of(*modifiers.toTypedArray()).reduce { o, n ->
        return@reduce o or n
    }.orElse(0).toLong())
}