package io.github.sgpublic.exsp.core

import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.util.List
import io.github.sgpublic.exsp.ExPreferenceProcessor.Companion.mTrees
import io.github.sgpublic.exsp.annotations.ExSharedPreference
import io.github.sgpublic.exsp.annotations.ExValue
import io.github.sgpublic.exsp.base.BaseElementVisitor
import io.github.sgpublic.exsp.jc.to
import io.github.sgpublic.exsp.util.Logable
import io.github.sgpublic.exsp.util.capitalize
import io.github.sgpublic.exsp.visitor.GetterVisitor
import io.github.sgpublic.exsp.visitor.SetterVisitor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement


class PreferenceCompiler private constructor(
    override val targetElement: TypeElement
): Logable, BaseElementVisitor<ExSharedPreference> {
    override val targetAnnotation: Class<out Annotation> = ExSharedPreference::class.java
    companion object {
        fun apply(env: RoundEnvironment) {
            for (element: Element in env.getElementsAnnotatedWith(ExSharedPreference::class.java)) {
                if (element !is TypeElement) {
                    continue
                }
                element.accept(
                    PreferenceCompiler(element),
                    element.getAnnotation(ExSharedPreference::class.java)
                ).to(mTrees.getTree(element))
            }
        }
    }

    override fun visitType(element: TypeElement, param: ExSharedPreference): List<JCTree> {
        val list = List.nil<JCTree>()

        for (field: Element in targetElement.enclosedElements) {
            if (field !is VariableElement || field.modifiers.contains(Modifier.FINAL)) {
                continue
            }

            val exValue = (field.getAnnotation(ExValue::class.java) ?: continue).let {
                return@let ExValue(it.defVal, it.key.capitalize())
            }

            field.accept(GetterVisitor, exValue).to(list)
            field.accept(SetterVisitor, exValue).to(list)

        }

        return list
    }
}