package io.github.sgpublic.exsp.jc

import io.github.sgpublic.exsp.base.BaseElementVisitor
import javax.lang.model.element.Element

fun <R, VisitorT: BaseElementVisitor<R, Unit?>> Element.accept(visitor: VisitorT): R {
    return accept(visitor, null)
}