@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref.core

import com.squareup.javapoet.ClassName
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.util.List
import io.github.sgpublic.xxpref.annotations.PrefVal
import io.github.sgpublic.xxpref.annotations.XXPreference
import io.github.sgpublic.xxpref.base.ListElementVisitor
import io.github.sgpublic.xxpref.jc.acceptTo
import io.github.sgpublic.xxpref.util.Logable
import io.github.sgpublic.xxpref.util.capitalize
import io.github.sgpublic.xxpref.util.supported
import io.github.sgpublic.xxpref.visitor.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

class PreferenceCompiler private constructor(
    override val targetElement: TypeElement
): Logable, ListElementVisitor<JCTree, XXPreference> {
    override val targetAnnotation: Class<out Annotation> = XXPreference::class.java
    companion object {
        fun apply(env: RoundEnvironment) {
            for (element: Element in env.getElementsAnnotatedWith(XXPreference::class.java)) {
                if (element !is TypeElement) {
                    continue
                }
                element.acceptTo(
                    PreferenceCompiler(element),
                    element.getAnnotation(XXPreference::class.java)
                )
            }
        }
    }

    override fun visitType(element: TypeElement, param: XXPreference): List<JCTree> {
        // import android.content.SharedPreferences;
        element.acceptTo(ImportDefVisitor, ImportDefVisitor.ClassPkgDef(
            "android.content.SharedPreferences", "SharedPreferences"
        ))
        // import io.github.sgpublic.xxpref.SpEditor;
        element.acceptTo(ImportDefVisitor, ImportDefVisitor.ClassPkgDef(
            "io.github.sgpublic.xxpref.SpEditor", "SpEditor"
        ))
        // import io.github.sgpublic.xxpref.ExPreference;
        element.acceptTo(ImportDefVisitor, ImportDefVisitor.ClassPkgDef(
            "io.github.sgpublic.xxpref.ExPreference", "ExPreference.Reference"
        ))
        // import io.github.sgpublic.xxpref.ExConverters;
        element.acceptTo(ImportDefVisitor, ImportDefVisitor.ClassPkgDef(
            "io.github.sgpublic.xxpref.ExConverters", "ExConverters"
        ))

        val list = List.nil<JCTree>()

        element.acceptTo(SpRefDefVisitor, param)
        val editor = element.acceptTo(EditorDefVisitor, null, list)

        for (field: Element in targetElement.enclosedElements) {
            if (field !is VariableElement || field.modifiers.contains(Modifier.FINAL)) {
                continue
            }

            val origType = ClassName.get(element.asType())
            if (!origType.supported()) {
                // import OriginT;
                origType as ClassName
                element.accept(ImportDefVisitor, ImportDefVisitor.ClassPkgDef(
                    origType.packageName(), origType.simpleName()
                ))
            }

            val prefVal = (field.getAnnotation(PrefVal::class.java) ?: continue).let {
                return@let PrefVal(it.defVal, it.key.capitalize())
            }

            field.acceptTo(GetterDefVisitor, prefVal, list)
            field.acceptTo(SetterDefVisitor, prefVal, list)
            field.acceptTo(EditorSetterDefVisitor, prefVal, editor)
        }

        return list
    }
}