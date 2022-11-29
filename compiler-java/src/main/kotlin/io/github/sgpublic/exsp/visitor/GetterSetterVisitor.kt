package io.github.sgpublic.exsp.visitor

import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.util.List
import io.github.sgpublic.exsp.annotations.ExValue
import io.github.sgpublic.exsp.base.BaseElementVisitor
import javax.lang.model.element.VariableElement

object GetterVisitor: BaseElementVisitor<ExValue> {
    override fun visitVariable(element: VariableElement, param: ExValue): List<JCTree> {
        TODO()
    }
}

object SetterVisitor: BaseElementVisitor<ExValue> {
    override fun visitVariable(element: VariableElement, param: ExValue): List<JCTree> {
        TODO()
    }
}

object EditorSetterVisitor: BaseElementVisitor<ExValue> {
    override fun visitVariable(element: VariableElement, param: ExValue): List<JCTree> {
        TODO()
    }
}