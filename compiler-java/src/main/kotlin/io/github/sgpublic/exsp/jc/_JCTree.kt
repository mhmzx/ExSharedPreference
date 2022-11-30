package io.github.sgpublic.exsp.jc

import com.sun.tools.javac.code.Flags
import com.sun.tools.javac.code.TypeTag
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.JCTree.JCClassDecl
import com.sun.tools.javac.util.List
import com.sun.tools.javac.util.ListBuffer
import io.github.sgpublic.exsp.ExPreferenceProcessor

fun JCTree.JCClassDecl.addSuperHashCode() {
    val statements = List.nil<JCTree.JCStatement>()
    val hashCode = ExPreferenceProcessor.mTreeMaker.Select(
        ExPreferenceProcessor.mTreeMaker.Ident(
        ExPreferenceProcessor.mNames._super
        ), ExPreferenceProcessor.mNames.hashCode
    )
    statements.append(
        ExPreferenceProcessor.mTreeMaker.Return(
            ExPreferenceProcessor.mTreeMaker.Apply(
                List.nil(), hashCode, List.nil())))
    defs.append(
        ExPreferenceProcessor.mTreeMaker.MethodDef(
            ExPreferenceProcessor.mTreeMaker.Modifiers(Flags.PUBLIC),
            ExPreferenceProcessor.mNames.hashCode,
            ExPreferenceProcessor.mTreeMaker.TypeIdent(TypeTag.INT),
            List.nil(),
            List.nil(),
            List.nil(),
            ExPreferenceProcessor.mTreeMaker.Block(0, statements),
            null
        ))
}

fun JCTree.JCClassDecl.addSuperEquals() {
    val statements = ListBuffer<JCTree.JCStatement>()
    val hashCode = ExPreferenceProcessor.mTreeMaker.Select(
        ExPreferenceProcessor.mTreeMaker.Ident(
        ExPreferenceProcessor.mNames._super
    ), ExPreferenceProcessor.mNames.equals)
    statements.append(
        ExPreferenceProcessor.mTreeMaker.Return(
            ExPreferenceProcessor.mTreeMaker.Apply(
                List.nil(), hashCode, List.nil())))
    defs.append(
        ExPreferenceProcessor.mTreeMaker.MethodDef(
            ExPreferenceProcessor.mTreeMaker.Modifiers(Flags.PUBLIC),
            ExPreferenceProcessor.mNames.hashCode,
            ExPreferenceProcessor.mTreeMaker.TypeIdent(TypeTag.BOOLEAN),
            List.nil(),
            List.nil(),
            List.nil(),
            ExPreferenceProcessor.mTreeMaker.Block(0, statements.toList()),
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
