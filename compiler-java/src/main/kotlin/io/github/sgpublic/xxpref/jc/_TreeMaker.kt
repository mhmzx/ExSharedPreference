package io.github.sgpublic.xxpref.jc

import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeMaker
import com.sun.tools.javac.util.List
import java.util.stream.Stream

fun TreeMaker.Modifiers(vararg modifiers: Int): JCTree.JCModifiers {
    return Modifiers(Stream.of(*modifiers.toTypedArray()).reduce { o, n ->
        return@reduce o or n
    }.orElse(0))
}

fun TreeMaker.Modifiers(modifiers: Int, anno: List<JCTree.JCAnnotation>): JCTree.JCModifiers {
    return Modifiers(modifiers.toLong(), anno)
}
