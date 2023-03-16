package io.github.sgpublic.xxpref.util

import io.github.sgpublic.xxpref.XXPrefProcessor
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

interface Logable: AnnotationMirror, Element {
    val targetAnnotation: Class<out Annotation>
    override fun getElementValues(): MutableMap<out ExecutableElement, out AnnotationValue> {
        TODO()
    }

    override fun getAnnotationType(): DeclaredType {
        return XXPrefProcessor.getType(targetAnnotation.canonicalName)
    }

    val targetElement: Element
    override fun <A : Annotation?> getAnnotation(clazz: Class<A>): A? {
        return targetElement.getAnnotation(clazz)
    }

    override fun getAnnotationMirrors(): MutableList<out AnnotationMirror> {
        return targetElement.annotationMirrors
    }

    override fun <A : Annotation?> getAnnotationsByType(p0: Class<A>?): Array<A> {
        return targetElement.getAnnotationsByType(p0)
    }

    override fun getSimpleName(): Name {
        return targetElement.simpleName
    }

    override fun getModifiers(): MutableSet<Modifier> {
        return targetElement.modifiers
    }

    override fun getEnclosedElements(): MutableList<out Element> {
        return targetElement.enclosedElements
    }

    override fun getEnclosingElement(): Element {
        return targetElement.enclosingElement
    }

    override fun getKind(): ElementKind {
        return targetElement.kind
    }

    override fun asType(): TypeMirror {
        return targetElement.asType()
    }

    override fun <R : Any?, P : Any?> accept(p0: ElementVisitor<R, P>?, p1: P): R {
        return targetElement.accept(p0, p1)
    }
}

class Logger(private val logable: Logable) {
    fun trace(msg: CharSequence) {
        XXPrefProcessor.mMessager.printMessage(Diagnostic.Kind.OTHER, msg, logable, logable)
    }
    fun info(msg: CharSequence) {
        XXPrefProcessor.mMessager.printMessage(Diagnostic.Kind.NOTE, msg, logable, logable)
    }
    fun warn(msg: CharSequence) {
        XXPrefProcessor.mMessager.printMessage(Diagnostic.Kind.WARNING, msg, logable, logable)
    }
    fun error(msg: CharSequence) {
        XXPrefProcessor.mMessager.printMessage(Diagnostic.Kind.ERROR, msg, logable, logable)
    }
}

val Logable.log: Logger get() {
    return Logger(this)
}