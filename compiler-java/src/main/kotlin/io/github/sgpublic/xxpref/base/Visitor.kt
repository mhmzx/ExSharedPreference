@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref.base

import com.sun.tools.javac.tree.JCTree
import javax.lang.model.element.*

/**
 *
 * @author Madray Haven
 * @date 2022/11/29 16:59
 */
interface BaseElementVisitor<R, P>: ElementVisitor<R, P> {
    override fun visit(element: Element, param: P): R {
        throw UnsupportedOperationException("Unsupported operation: ${javaClass.simpleName}#visit")
    }

    override fun visitPackage(element: PackageElement, param: P): R {
        throw UnsupportedOperationException("Unsupported operation: ${javaClass.simpleName}#visitPackage")
    }

    override fun visitType(element: TypeElement, param: P): R {
        throw UnsupportedOperationException("Unsupported operation: ${javaClass.simpleName}#visitType")
    }

    override fun visitVariable(element: VariableElement, param: P): R {
        throw UnsupportedOperationException("Unsupported operation: ${javaClass.simpleName}#visitVariable")
    }

    override fun visitExecutable(element: ExecutableElement, param: P): R {
        throw UnsupportedOperationException("Unsupported operation: ${javaClass.simpleName}#visitExecutable")
    }

    override fun visitTypeParameter(element: TypeParameterElement, param: P): R {
        throw UnsupportedOperationException("Unsupported operation: ${javaClass.simpleName}#visitTypeParameter")
    }

    override fun visitUnknown(element: Element, param: P): R {
        throw UnsupportedOperationException("Unsupported operation: ${javaClass.simpleName}#visitUnknown")
    }
}

interface ListElementVisitor<JCTreeT: JCTree, P>: BaseElementVisitor<com.sun.tools.javac.util.List<JCTreeT>, P>
interface SingleElementVisitor<JCTreeT: JCTree, P>: BaseElementVisitor<JCTreeT, P>
interface SimpleElementVisitor<R, P>: BaseElementVisitor<R, P> {
    fun visit(param: P): R

    override fun visit(element: Element, param: P): R {
        return visit(param)
    }

    override fun visitPackage(element: PackageElement, param: P): R {
        return visit(param)
    }

    override fun visitType(element: TypeElement, param: P): R {
        return visit(param)
    }

    override fun visitVariable(element: VariableElement, param: P): R {
        return visit(param)
    }

    override fun visitExecutable(element: ExecutableElement, param: P): R {
        return visit(param)
    }

    override fun visitTypeParameter(element: TypeParameterElement, param: P): R {
        return visit(param)
    }

    override fun visitUnknown(element: Element, param: P): R {
        return visit(param)
    }
}
