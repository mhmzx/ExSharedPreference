package io.github.sgpublic.exsp.base

import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.util.List
import javax.lang.model.element.*

/**
 *
 * @author Madray Haven
 * @date 2022/11/29 16:59
 */
interface BaseElementVisitor<P>: ElementVisitor<List<JCTree>, P> {
    override fun visit(element: Element, param: P): List<JCTree> {
        throw UnsupportedOperationException("Unsupported operation: BaseElementVisitor#visit")
    }

    override fun visitPackage(element: PackageElement, param: P): List<JCTree> {
        throw UnsupportedOperationException("Unsupported operation: BaseElementVisitor#visitPackage")
    }

    override fun visitType(element: TypeElement, param: P): List<JCTree> {
        throw UnsupportedOperationException("Unsupported operation: BaseElementVisitor#visitType")
    }

    override fun visitVariable(element: VariableElement, param: P): List<JCTree> {
        throw UnsupportedOperationException("Unsupported operation: BaseElementVisitor#visitVariable")
    }

    override fun visitExecutable(element: ExecutableElement, param: P): List<JCTree> {
        throw UnsupportedOperationException("Unsupported operation: BaseElementVisitor#visitExecutable")
    }

    override fun visitTypeParameter(element: TypeParameterElement, param: P): List<JCTree> {
        throw UnsupportedOperationException("Unsupported operation: BaseElementVisitor#visitTypeParameter")
    }

    override fun visitUnknown(element: Element, param: P): List<JCTree> {
        throw UnsupportedOperationException("Unsupported operation: BaseElementVisitor#visitUnknown")
    }
}
