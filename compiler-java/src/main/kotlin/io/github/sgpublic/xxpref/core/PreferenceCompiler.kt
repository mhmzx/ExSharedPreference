@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref.core

import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.util.List
import io.github.sgpublic.xxpref.annotations.PrefVal
import io.github.sgpublic.xxpref.annotations.XXPreference
import io.github.sgpublic.xxpref.base.ListElementVisitor
import io.github.sgpublic.xxpref.jc.*
import io.github.sgpublic.xxpref.util.*
import io.github.sgpublic.xxpref.visitor.*
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

class PreferenceCompiler private constructor(
    override val targetElement: TypeElement
): Logable, ListElementVisitor<JCTree, XXPreference> {
    override val targetAnnotation: Class<out Annotation> = XXPreference::class.java
    companion object {
        fun apply(env: RoundEnvironment) {
            for (element: Element in env.getElementsAnnotatedWith(XXPreference::class.java)) {
                if (element !is Symbol.ClassSymbol) {
                    continue
                }
                element.acceptToSelf(
                    PreferenceCompiler(element),
                    element.getAnnotation(XXPreference::class.java)
                )
                mMessager.printMessage(Diagnostic.Kind.NOTE, "mTrees.getTree(element): ${mTrees.getTree(element)}")
            }
        }
    }

    override fun visitType(element: TypeElement, param: XXPreference): List<JCTree> {
        var list = List.nil<JCTree>()

        element.acceptToSelf(SpRefDefVisitor, param)
        val editor = element.accept(EditorClassDefVisitor, element).also {
            list = list.append(it)
        }
        element.acceptToSelf(EditMethodDefVisitor, editor)

        for (field: Element in LinkedList(targetElement.enclosedElements)) {
            if (field !is VariableElement || field.modifiers.contains(Modifier.FINAL)) {
                continue
            }

            (field.getAnnotation(PrefVal::class.java) ?: continue).let { pref ->
                return@let PrefVal(pref.key.takeIf { it.isNotBlank() }
                    ?: field.simpleName.toString(), pref.defVal)
            }.let {  prefVal ->
                list = list.append(field.accept(GetterDefVisitor, prefVal))
                list = list.append(field.accept(SetterDefVisitor, prefVal))
                field.acceptTo(EditorSetterDefVisitor, prefVal, editor)
            }
        }

        return list
    }
}