package io.github.sgpublic.xxpref.visitor

import com.squareup.javapoet.ClassName
import com.sun.tools.javac.code.Flags
import com.sun.tools.javac.tree.JCTree.*
import com.sun.tools.javac.util.List
import io.github.sgpublic.xxpref.XXPrefProcessor
import io.github.sgpublic.xxpref.annotations.ExSharedPreference
import io.github.sgpublic.xxpref.annotations.ExValue
import io.github.sgpublic.xxpref.base.SimpleElementVisitor
import io.github.sgpublic.xxpref.base.SingleElementVisitor
import io.github.sgpublic.xxpref.core.ConverterCompiler
import io.github.sgpublic.xxpref.jc.Modifiers
import io.github.sgpublic.xxpref.util.*
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

object ImportDefVisitor: SimpleElementVisitor<JCImport, ImportDefVisitor.ClassPkgDef> {
    override fun visit(param: ClassPkgDef): JCImport {
        val ident = mTreeMaker.Ident(mNames.fromString(param.packageName))
        return mTreeMaker.Import(mTreeMaker.Select(
            ident, mNames.fromString(param.simpleName)
        ), false)
    }

    data class ClassPkgDef(
        val packageName: String,
        val simpleName: String,
    )
}

object GetterDefVisitor: SingleElementVisitor<JCMethodDecl, ExValue> {
    override fun visitVariable(element: VariableElement, param: ExValue): JCMethodDecl {
        val statement = List.nil<JCStatement>()

        // final SharedPreferences sp = this.SpRef.getValue();
        val SpRef_Get = mTreeMaker.Select(
            mTreeMaker.Select(
                mTreeMaker.Ident(mNames._this),
                mNames.fromString(SpRefDefVisitor.Reference)
            ),
            mNames.fromString("getValue")
        )
        statement.append(mTreeMaker.VarDef(
            mTreeMaker.Modifiers(Flags.FINAL),
            mNames.fromString("sp"),
            mTreeMaker.Ident(mNames.fromString("SharedPreferences")),
            mTreeMaker.Apply(List.nil(), SpRef_Get, List.nil())
        ))

        val origType = ClassName.get(element.asType())
        var conType = origType
        if (element.isEnum()) {
            conType = StringTypeOrigin
        } else if (!origType.supported()) {
            conType = ClassName.get(ConverterCompiler.getTarget(
                XXPrefProcessor.asElement(element.asType())!!
            ))
        }
        val spType = SharedPreferenceType.of(conType)

        // <key>
        val key = mTreeMaker.Ident(mNames.fromString("\"${param.key}\""))
        // <defVal>
        val defVal = if (spType == SharedPreferenceType.String) {
            mTreeMaker.Ident(mNames.fromString("\"${param.defVal}\""))
        } else {
            mTreeMaker.Ident(mNames.fromString(param.defVal))
        }

        // final OriginT origin = sp.get[OriginT](<key>, <defVal>);
        val origin = mNames.fromString("origin")
        statement.append(mTreeMaker.VarDef(
            mTreeMaker.Modifiers(Flags.FINAL),
            origin,
            mTreeMaker.Ident(mNames.fromString(
                element.simpleName.toString()
            )),
            spType.getStatement(key, defVal)
        ))

        if (origType.supported()) {
            // return origin;
            statement.append(mTreeMaker.Return(
                mTreeMaker.Ident(origin)
            ))
        } else if (element.isEnum()) {
            origType as ClassName
            val valueOf = mTreeMaker.Select(
                mTreeMaker.Ident(mNames.fromString(origType.simpleName())),
                mNames.valueOf
            )
            val valueOfOrigin = mTreeMaker.Apply(
                List.nil(), valueOf,
                List.of(mTreeMaker.Ident(origin))
            )
            val valueOfDef = mTreeMaker.Exec(mTreeMaker.Apply(
                List.nil(), valueOf, List.of(defVal)
            ))

            element.accept(ImportDefVisitor, ImportDefVisitor.ClassPkgDef(
                IllegalArgumentException::class.qualifiedName!!,
                IllegalArgumentException::class.simpleName!!,
            ))

            // try {
            //     return EnumT.valueOf(origin);
            // } catch (IllegalArgumentException ignore) {
            //     return EnumT.valueOf(<defVal>);
            // }
            statement.append(mTreeMaker.Try(
                mTreeMaker.Block(0, List.of(
                    mTreeMaker.Exec(valueOfOrigin)
                )),
                List.of(mTreeMaker.Catch(
                    mTreeMaker.VarDef(
                        mTreeMaker.Modifiers(),
                        mNames.fromString("ignore"),
                        mTreeMaker.Ident(mNames.fromString(IllegalArgumentException::class.simpleName!!)),
                        null
                    ),
                    mTreeMaker.Block(0, List.of(valueOfDef))
                )),
                null
            ))
        } else {
            origType as ClassName
            val originClass = mTreeMaker.Select(
                mTreeMaker.Ident(mNames.fromString(origType.simpleName())),
                mNames._class
            )

            // return ExConverters.fromPreference(OriginT.class, origin);
            statement.append(mTreeMaker.Return(mTreeMaker.Apply(
                List.nil(),
                mTreeMaker.Select(
                    mTreeMaker.Ident(mNames.fromString("ExConverters")),
                    mNames.fromString("fromPreference")
                ),
                List.of(originClass, mTreeMaker.Ident(origin))
            )))
        }


        return mTreeMaker.MethodDef(
            mTreeMaker.Modifiers(Flags.PUBLIC, Flags.FINAL),
            mNames.fromString(element.getterName()),
            mTreeMaker.Ident(mNames.fromString(element.simpleName.toString())),
            List.nil(), List.nil(), List.nil(),
            mTreeMaker.Block(0, statement),
            null,
        )
    }
}

object SetterDefVisitor: SingleElementVisitor<JCMethodDecl, ExValue> {
    override fun visitVariable(element: VariableElement, param: ExValue): JCMethodDecl {
        val statement = List.nil<JCStatement>()

        // final SharedPreferences.Editor editor = this.SpRef.edit();
        val SpRef_Get = mTreeMaker.Select(
            mTreeMaker.Select(
                mTreeMaker.Ident(mNames._this),
                mNames.fromString(SpRefDefVisitor.Reference)
            ),
            mNames.fromString("edit")
        )
        val editor = mNames.fromString("editor")
        statement.append(mTreeMaker.VarDef(
            mTreeMaker.Modifiers(Flags.FINAL), editor,
            mTreeMaker.Ident(mNames.fromString("SharedPreferences.Editor")),
            mTreeMaker.Apply(List.nil(), SpRef_Get, List.nil())
        ))

        val origType = ClassName.get(element.asType())
        var convType = origType
        if (element.isEnum()) {
            convType = StringTypeOrigin
        } else if (!origType.supported()) {
            convType = ClassName.get(ConverterCompiler.getTarget(
                XXPrefProcessor.asElement(element.asType())!!
            ))
        }

        val value = mNames.fromString("value")


        val spType = SharedPreferenceType.of(convType)
        // <key>
        val key = mTreeMaker.Ident(mNames.fromString("\"${param.key}\""))
        if (origType.supported()) {
            // editor.put[ConvT](<key>, <value>);
            statement.append(mTreeMaker.Exec(
                spType.putStatement(key, mTreeMaker.Ident(value))
            ))
        } else if (element.isEnum()) {
            val name = mTreeMaker.Select(mTreeMaker.Ident(value), mNames._name)

            element.accept(ImportDefVisitor, ImportDefVisitor.ClassPkgDef(
                IllegalArgumentException::class.qualifiedName!!,
                IllegalArgumentException::class.simpleName!!,
            ))

            // editor.put[ConvT](<key>, <value>.name());
            statement.append(mTreeMaker.Exec(
                spType.putStatement(key, name)
            ))
        } else {
            origType as ClassName
            val originClass = mTreeMaker.Select(
                mTreeMaker.Ident(mNames.fromString(origType.simpleName())),
                mNames._class
            )

            val fromPref = mTreeMaker.Apply(
                List.nil(),
                mTreeMaker.Select(
                    mTreeMaker.Ident(mNames.fromString("ExConverters")),
                    mNames.fromString("toPreference")
                ),
                List.of(originClass, mTreeMaker.Ident(value))
            )
            // editor.put[ConvT](<key>, ExConverters.fromPreference(OriginT.class, origin));
            statement.append(mTreeMaker.Exec(
                spType.putStatement(key, fromPref)
            ))
        }


        // editor.apply();
        statement.append(mTreeMaker.Exec(mTreeMaker.Apply(
            List.nil(),
            mTreeMaker.Select(
                mTreeMaker.Ident(editor),
                mNames.fromString("apply")
            ),
            List.nil(),
        )))

        return mTreeMaker.MethodDef(
            mTreeMaker.Modifiers(Flags.PUBLIC, Flags.FINAL),
            mNames.fromString(element.setterName()),
            mTreeMaker.Ident(mNames.fromString(element.simpleName.toString())),
            List.nil(),
            List.of(mTreeMaker.VarDef(
                mTreeMaker.Modifiers(),
                value,
                mTreeMaker.Ident(mNames.fromString(origType.toString())),
                null
            )),
            List.nil(),
            mTreeMaker.Block(0, statement),
            null,
        )
    }


}

object EditorDefVisitor: SingleElementVisitor<JCClassDecl, Unit?> {
    const val Editor = "Editor"

    override fun visitType(element: TypeElement, param: Unit?): JCClassDecl {
        // <callSuper>:
        // super(editor)
        val callSuper = mTreeMaker.Exec(mTreeMaker.Apply(
            List.nil(), mTreeMaker.Ident(mNames._super),
            List.of(mTreeMaker.Ident(mNames.fromString("editor")))
        ))
        // <constructor>:
        // private SpEditor(SharedPreference.Editor editor) {
        //     <callSuper>;
        // }
        val constructor = mTreeMaker.MethodDef(
            mTreeMaker.Modifiers(Flags.PRIVATE),
            mNames.init, null, List.nil(),
            List.of(mTreeMaker.VarDef(
                mTreeMaker.Modifiers(),
                mNames.fromString("editor"),
                mTreeMaker.Ident(mNames.fromString("SharedPreference.Editor")),
                null
            )),
            List.nil(),
            mTreeMaker.Block(0, List.of(callSuper)),
            null
        )

        // public static class SpEditor {
        //     <constructor>
        // }
        val editor = mTreeMaker.ClassDef(
            mTreeMaker.Modifiers(Flags.PUBLIC, Flags.STATIC),
            mNames.fromString(Editor), List.nil(),
            mTreeMaker.Ident(mNames.fromString("SpEditor")),
            List.nil(), List.nil(),
        )
        
        editor.defs.append(constructor)

        return editor
    }
}



object SpRefDefVisitor: SingleElementVisitor<JCVariableDecl, ExSharedPreference> {
    const val Reference = "SpRef"

    override fun visitType(element: TypeElement, param: ExSharedPreference): JCVariableDecl {
        // <name>
        val name = mTreeMaker.Ident(mNames.fromString("\"${param.name}\""))
        // <mode>
        val mode = mTreeMaker.Ident(mNames.fromString("${param.mode}"))

        // <init>:
        // ExPreference.getSharedPreference(<name>, <mode>)
        val init = mTreeMaker.Apply(
            List.nil(),
            mTreeMaker.Select(
                mTreeMaker.Ident(mNames.fromString("ExPreference")),
                mNames.fromString("getSharedPreference")
            ),
            List.of(name, mode)
        )

        // private static final ExPreference.Reference SpRef = <init>;
        return mTreeMaker.VarDef(
            mTreeMaker.Modifiers(Flags.PRIVATE, Flags.STATIC, Flags.FINAL),
            mNames.fromString(Reference),
            mTreeMaker.Ident(mNames.fromString("ExPreference.Reference")),
            init
        )
    }
}

object EditorSetterDefVisitor: SingleElementVisitor<JCMethodDecl, ExValue> {
    override fun visitVariable(element: VariableElement, param: ExValue): JCMethodDecl {
        val statement = List.nil<JCStatement>()

        // final SharedPreferences.Editor editor = this.getEditor();
        val getEditor = mTreeMaker.Select(
            mTreeMaker.Ident(mNames._this),
            mNames.fromString("getEditor")
        )
        val editor = mNames.fromString("editor")
        statement.append(mTreeMaker.VarDef(
            mTreeMaker.Modifiers(Flags.FINAL), editor,
            mTreeMaker.Ident(mNames.fromString("SharedPreferences.Editor")),
            mTreeMaker.Apply(List.nil(), getEditor, List.nil())
        ))

        val origType = ClassName.get(element.asType())
        var convType = origType
        if (element.isEnum()) {
            convType = StringTypeOrigin
        } else if (!origType.supported()) {
            convType = ClassName.get(ConverterCompiler.getTarget(
                XXPrefProcessor.asElement(element.asType())!!
            ))
        }

        val value = SetterDefVisitor.mNames.fromString("value")

        val spType = SharedPreferenceType.of(convType)
        // <key>
        val key = SetterDefVisitor.mTreeMaker.Ident(SetterDefVisitor.mNames.fromString("\"${param.key}\""))
        if (origType.supported()) {
            // editor.put[ConvT](<key>, <value>);
            statement.append(
                SetterDefVisitor.mTreeMaker.Exec(
                spType.putStatement(key, SetterDefVisitor.mTreeMaker.Ident(value))
            ))
        } else if (element.isEnum()) {
            val name = SetterDefVisitor.mTreeMaker.Select(SetterDefVisitor.mTreeMaker.Ident(value), SetterDefVisitor.mNames._name)

            element.accept(ImportDefVisitor, ImportDefVisitor.ClassPkgDef(
                IllegalArgumentException::class.qualifiedName!!,
                IllegalArgumentException::class.simpleName!!,
            ))

            // editor.put[ConvT](<key>, <value>.name());
            statement.append(
                SetterDefVisitor.mTreeMaker.Exec(
                spType.putStatement(key, name)
            ))
        } else {
            origType as ClassName
            val originClass = SetterDefVisitor.mTreeMaker.Select(
                SetterDefVisitor.mTreeMaker.Ident(SetterDefVisitor.mNames.fromString(origType.simpleName())),
                SetterDefVisitor.mNames._class
            )

            val fromPref = SetterDefVisitor.mTreeMaker.Apply(
                List.nil(),
                SetterDefVisitor.mTreeMaker.Select(
                    SetterDefVisitor.mTreeMaker.Ident(SetterDefVisitor.mNames.fromString("ExConverters")),
                    SetterDefVisitor.mNames.fromString("toPreference")
                ),
                List.of(originClass, SetterDefVisitor.mTreeMaker.Ident(value))
            )
            // editor.put[ConvT](<key>, ExConverters.fromPreference(OriginT.class, origin));
            statement.append(
                SetterDefVisitor.mTreeMaker.Exec(
                spType.putStatement(key, fromPref)
            ))
        }

        // return this;
        statement.append(mTreeMaker.Return(
            mTreeMaker.Ident(mNames._this)
        ))

        return mTreeMaker.MethodDef(
            mTreeMaker.Modifiers(Flags.PUBLIC, Flags.FINAL),
            mNames.fromString(element.setterName()),
            mTreeMaker.Ident(mNames.fromString(EditorDefVisitor.Editor)),
            List.nil(),
            List.of(mTreeMaker.VarDef(
                mTreeMaker.Modifiers(),
                value,
                mTreeMaker.Ident(mNames.fromString(origType.toString())),
                null
            )),
            List.nil(),
            mTreeMaker.Block(0, statement),
            null,
        )
    }
}