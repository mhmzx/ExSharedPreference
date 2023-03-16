package io.github.sgpublic.xxpref.jc

import com.sun.tools.javac.code.Flags
import com.sun.tools.javac.code.TypeTag
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.JCTree.JCClassDecl
import com.sun.tools.javac.util.List
import com.sun.tools.javac.util.ListBuffer
import io.github.sgpublic.xxpref.XXPrefProcessor

fun JCTree.JCClassDecl.addSuperHashCode() {
    val statements = List.nil<JCTree.JCStatement>()
    val hashCode = XXPrefProcessor.mTreeMaker.Select(
        XXPrefProcessor.mTreeMaker.Ident(
        XXPrefProcessor.mNames._super
        ), XXPrefProcessor.mNames.hashCode
    )
    statements.append(
        XXPrefProcessor.mTreeMaker.Return(
            XXPrefProcessor.mTreeMaker.Apply(
                List.nil(), hashCode, List.nil())))
    defs.append(
        XXPrefProcessor.mTreeMaker.MethodDef(
            XXPrefProcessor.mTreeMaker.Modifiers(Flags.PUBLIC),
            XXPrefProcessor.mNames.hashCode,
            XXPrefProcessor.mTreeMaker.TypeIdent(TypeTag.INT),
            List.nil(),
            List.nil(),
            List.nil(),
            XXPrefProcessor.mTreeMaker.Block(0, statements),
            null
        ))
}

fun JCTree.JCClassDecl.addSuperEquals() {
    val statements = ListBuffer<JCTree.JCStatement>()
    val hashCode = XXPrefProcessor.mTreeMaker.Select(
        XXPrefProcessor.mTreeMaker.Ident(
        XXPrefProcessor.mNames._super
    ), XXPrefProcessor.mNames.equals)
    statements.append(
        XXPrefProcessor.mTreeMaker.Return(
            XXPrefProcessor.mTreeMaker.Apply(
                List.nil(), hashCode, List.nil())))
    defs.append(
        XXPrefProcessor.mTreeMaker.MethodDef(
            XXPrefProcessor.mTreeMaker.Modifiers(Flags.PUBLIC),
            XXPrefProcessor.mNames.hashCode,
            XXPrefProcessor.mTreeMaker.TypeIdent(TypeTag.BOOLEAN),
            List.nil(),
            List.nil(),
            List.nil(),
            XXPrefProcessor.mTreeMaker.Block(0, statements.toList()),
            null
        ))
}

fun <T: JCTree> List<T>.to(tree: JCClassDecl): List<T> {
    return to(tree.defs)
}
fun <T: JCTree> List<T>.to(tree: List<JCTree>): List<T> {
    tree.appendList(map { it as JCTree })
    return this
}

fun <T: JCTree> T.to(tree: JCClassDecl): T {
    return to(tree.defs)
}
fun <T: JCTree> T.to(tree: List<JCTree>): T {
    tree.append(this)
    return this
}
