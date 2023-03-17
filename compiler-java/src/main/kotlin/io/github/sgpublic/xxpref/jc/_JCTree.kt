@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref.jc

import com.sun.tools.javac.code.Flags
import com.sun.tools.javac.code.TypeTag
import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.JCTree.JCClassDecl
import com.sun.tools.javac.util.List
import com.sun.tools.javac.util.ListBuffer
import io.github.sgpublic.xxpref.XXPrefProcessor
import io.github.sgpublic.xxpref.base.BaseElementVisitor
import io.github.sgpublic.xxpref.base.ListElementVisitor
import java.lang.reflect.Method
import javax.lang.model.element.Element

fun JCClassDecl.addSuperHashCode() {
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

fun JCClassDecl.addSuperEquals() {
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

private val JavacTrees: Class<*> by lazy {
    Class.forName("com.sun.tools.javac.api.JavacTrees")
}
private val mJavacTree_instance: Any by lazy {
    JavacTrees.getMethod("instance", JavacProcessingEnvironment::class.java)
        .invoke(XXPrefProcessor.PROCESS_ENV)
}
private val mJavacTree_getTrees: Method by lazy {
    JavacTrees.getMethod("getTrees", Element::class.java)
}

fun <T: Element, R: JCTree, P: Any?> T.acceptTo(visitor: ListElementVisitor<R, P>, param: P, target: List<out JCTree>? = null): List<R> {
    val tmp = target?.map { it as JCTree }
        ?: (mJavacTree_getTrees.invoke(mJavacTree_instance, this) as JCClassDecl).defs
    return accept(visitor, param).also { accept ->
        tmp.appendList(accept.map { it as R })
    }
}

fun <T: Element, R: JCTree, P: Any?> T.acceptTo(visitor: BaseElementVisitor<R, P>, param: P, target: List<out JCTree>? = null): R {
    val tmp = target?.map { it as JCTree }
        ?: (mJavacTree_getTrees.invoke(mJavacTree_instance, this) as JCClassDecl).defs
    return accept(visitor, param).also { accept ->
        tmp.append(accept as JCTree)
    }
}

fun <T: Element, R: JCTree, P: Any?> T.acceptTo(visitor: ListElementVisitor<R, P>, param: P, target: JCClassDecl): List<R> {
    return acceptTo(visitor, param, target.defs)
}

fun <T: Element, R: JCTree, P: Any?> T.acceptTo(visitor: BaseElementVisitor<R, P>, param: P, target: JCClassDecl): R {
    return acceptTo(visitor, param, target.defs)
}
//fun <T: JCTree> List<T>.to(tree: JCClassDecl): List<T> {
//    return to(tree.defs)
//}
//fun <T: JCTree> List<T>.to(tree: List<JCTree>): List<T> {
//    tree.appendList(map { it as JCTree })
//    return this
//}
//
//fun <T: JCTree> T.to(tree: JCClassDecl): T {
//    return to(tree.defs)
//}
//fun <T: JCTree> T.to(tree: List<JCTree>): T {
//    tree.append(this)
//    return this
//}
