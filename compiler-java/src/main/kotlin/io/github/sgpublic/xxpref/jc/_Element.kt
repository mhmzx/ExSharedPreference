package io.github.sgpublic.xxpref.jc

import io.github.sgpublic.xxpref.base.BaseElementVisitor
import javax.lang.model.element.Element

fun <R, VisitorT: BaseElementVisitor<R, Unit?>> Element.accept(visitor: VisitorT): R {
    return accept(visitor, null)
}