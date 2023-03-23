@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref.jc

import com.squareup.javapoet.ClassName
import com.sun.tools.javac.code.Flags
import com.sun.tools.javac.code.TypeTag
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.JCTree.JCClassDecl
import com.sun.tools.javac.util.List
import com.sun.tools.javac.util.ListBuffer
import io.github.sgpublic.xxpref.base.BaseElementVisitor
import io.github.sgpublic.xxpref.base.ListElementVisitor
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

private object ImportVisitor: BaseElementVisitor<Unit, Array<out ClassName>> {
    override fun visitType(element: TypeElement, param: Array<out ClassName>) {
        val tree = mTrees.getPath(element).compilationUnit as JCTree.JCCompilationUnit
        val existImport = HashSet<String>().also {
            for (import in tree.imports) {
                it.add(import.toString())
            }
        }
        for (className in param) {
            val packageName = className.canonicalName()
            if (existImport.contains(packageName)) {
                continue
            }

//            JavacTool.create().getTask(
//                null, null, null, null, null,
//                List.of(mTrees.getPath(TypeElementImpl(packageName)).compilationUnit.sourceFile)
//            ).call()

            var importPath: JCTree.JCExpression? = null
            for (path in packageName.split(".")) {
                val next = mNames.fromString(path)
                importPath = if (importPath == null) {
                    mTreeMaker.Ident(next)
                } else {
                    mTreeMaker.Select(importPath, next)
                }
            }
            tree.defs = tree.defs.append((mTreeMaker.Import(
                importPath ?: continue, false
            )))
        }
    }
}
fun TypeElement.Import(vararg clazz: ClassName) {
    accept(ImportVisitor, clazz)
}

fun <T: TypeElement, R: JCTree, P: Any?> T.acceptToSelf(visitor: ListElementVisitor<R, P>, param: P): List<R> {
    return acceptTo(visitor, param, mTrees.getTree(this))
}

fun <T: TypeElement, R: JCTree, P: Any?> T.acceptToSelf(visitor: BaseElementVisitor<R, P>, param: P): R {
    return acceptTo(visitor, param, mTrees.getTree(this))
}
fun <T: Element, R: JCTree, P: Any?> T.acceptTo(visitor: ListElementVisitor<R, P>, param: P, target: JCClassDecl): List<R> {
    return accept(visitor, param).also { accept ->
        target.defs = target.defs.appendList(accept.map { it as JCTree })
    }
}

fun <T: Element, R: JCTree, P: Any?> T.acceptTo(visitor: BaseElementVisitor<R, P>, param: P, target: JCClassDecl): R {
    return accept(visitor, param).also { accept ->
        target.defs = target.defs.append(accept)
    }
}