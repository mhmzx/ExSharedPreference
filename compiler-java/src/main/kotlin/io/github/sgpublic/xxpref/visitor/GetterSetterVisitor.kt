@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package io.github.sgpublic.xxpref.visitor

import com.squareup.javapoet.ClassName
import com.sun.tools.javac.code.Flags
import com.sun.tools.javac.tree.JCTree.*
import com.sun.tools.javac.util.List
import io.github.sgpublic.xxpref.annotations.PrefVal
import io.github.sgpublic.xxpref.annotations.XXPreference
import io.github.sgpublic.xxpref.base.SingleElementVisitor
import io.github.sgpublic.xxpref.core.ConverterCompiler
import io.github.sgpublic.xxpref.jc.*
import io.github.sgpublic.xxpref.util.*
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

object GetterDefVisitor: SingleElementVisitor<JCMethodDecl, PrefVal> {
    override fun visitVariable(element: VariableElement, param: PrefVal): JCMethodDecl {
        mTreeMaker.at(mTrees.getTree(element).pos)
        var statement = List.nil<JCStatement>()

        // final SharedPreferences sp = this.SpRef.getValue();
        val SpRef = mTreeMaker.Ident(mNames.fromString(SpRefDefVisitor.Reference))
        val SpRef_Get = mTreeMaker.Select(
            SpRef, mNames.fromString("getValue")
        )
        statement = statement.append(mTreeMaker.VarDef(
            mTreeMaker.VarModifiers(Flags.FINAL),
            mNames.fromString("sp"),
            TypeImpl(Types.SharedPreferences),
            mTreeMaker.Apply(List.nil(), SpRef_Get, List.nil())
        ))

        val origType = ClassName.get(element.asType())
        var conType = origType
        if (element.isEnum()) {
            conType = StringTypeOrigin
        } else if (!origType.supported()) {
            conType = ClassName.get(ConverterCompiler.getTarget(
                TypeElementImpl(element.asType())!!
            ))
        }
        val spType = SharedPreferenceType.of(conType)

        // <key>
        val key = mTreeMaker.Literal(param.key)
        // <defVal>
        val defVal = mTreeMaker.Literal(param.defVal.let {
            return@let when {
                conType.isString -> it
                conType.isBoolean -> it.toBoolean()
                conType.isInt -> it.toInt()
                conType.isLong -> it.toLong()
                conType.isFloat -> it.toFloat()
                else -> throw IllegalArgumentException("XXPref Intern error: unknown type of $conType")
            }
        })

        // final OriginT origin = sp.get[OriginT](<key>, <defVal>);
        val origin = mNames.fromString("origin")
        statement = statement.append(mTreeMaker.VarDef(
            mTreeMaker.VarModifiers(Flags.FINAL),
            origin,
            TypeImpl(conType),
            spType.getStatement(key, defVal)
        ))

        if (origType.supported()) {
            // return origin;
            statement = statement.append(mTreeMaker.Return(
                mTreeMaker.Ident(origin)
            ))
        } else if (element.isEnum()) {
            origType as ClassName
            val valueOf = mTreeMaker.Select(
                TypeImpl(origType),
                mNames.valueOf
            )
            val valueOfOrigin = mTreeMaker.Apply(
                List.nil(), valueOf,
                List.of(mTreeMaker.Ident(origin))
            )
            val valueOfDef = mTreeMaker.Apply(
                List.nil(), valueOf, List.of(defVal)
            )

            // try {
            //     return EnumT.valueOf(origin);
            // } catch (IllegalArgumentException ignore) {
            //     return EnumT.valueOf(<defVal>);
            // }
            statement = statement.append(mTreeMaker.Try(
                mTreeMaker.Block(0, List.of(
                    mTreeMaker.Return(valueOfOrigin)
                )),
                List.of(mTreeMaker.Catch(
                    mTreeMaker.VarDef(
                        mTreeMaker.ParamModifiers(),
                        mNames.fromString("ignore"),
                        TypeImpl(DeclaredTypeImpl(IllegalArgumentException::class.java.name)),
                        null
                    ),
                    mTreeMaker.Block(0, List.of(mTreeMaker.Return(valueOfDef)))
                )),
                null
            ))
//            statement = statement.append(mTreeMaker.Return(mTreeMaker.Apply(
//                List.nil(),
//                mTreeMaker.Select(
//                    SpRef, mNames.fromString("getEnum")
//                ),
//                List.of(mTreeMaker.Select(
//                    TypeImpl(origType),
//                    mNames._class
//                ), key, defVal)
//            )))
        } else {
            origType as ClassName
            val originClass = mTreeMaker.Select(
                TypeImpl(element.asType()),
                mNames._class
            )

            // return Converters.fromPreference(OriginT.class, origin);
            statement = statement.append(mTreeMaker.Return(mTreeMaker.Apply(
                List.nil(),
                mTreeMaker.Select(
                    TypeImpl(Types.Converters),
                    mNames.fromString("fromPreference")
                ),
                List.of(originClass, mTreeMaker.Ident(origin))
            )))
        }

        return mTreeMaker.MethodDef(
            mTreeMaker.MethodModifiers(Flags.PUBLIC, Flags.STATIC),
            mNames.fromString(element.getterName()),
            TypeImpl(element.asType()),
            List.nil(), List.nil(), List.nil(),
            mTreeMaker.Block(0, statement),
            null,
        )
    }
}

object SetterDefVisitor: SingleElementVisitor<JCMethodDecl, PrefVal> {
    override fun visitVariable(element: VariableElement, param: PrefVal): JCMethodDecl {
        mTreeMaker.at(mTrees.getTree(element).pos)
        var statement = List.nil<JCStatement>()

        // final SharedPreferences.Editor editor = this.SpRef.edit();
        val SpRef_Get = mTreeMaker.Select(
            mTreeMaker.Ident(mNames.fromString(SpRefDefVisitor.Reference)),
            mNames.fromString("edit")
        )
        val editor = mNames.fromString("editor")
        statement = statement.append(mTreeMaker.VarDef(
            mTreeMaker.VarModifiers(Flags.FINAL),
            editor,
            TypeImpl(Types.SharedPreferencesEditor),
            mTreeMaker.Apply(List.nil(), SpRef_Get, List.nil())
        ))

        val origType = ClassName.get(element.asType())
        var convType = origType
        if (element.isEnum()) {
            convType = StringTypeOrigin
        } else if (!origType.supported()) {
            convType = ClassName.get(ConverterCompiler.getTarget(
                TypeElementImpl(element.asType())!!
            ))
        }

        val value = mNames.fromString("value")

        val spType = SharedPreferenceType.of(convType)
        // <key>
        val key = mTreeMaker.Literal(param.key)
        if (origType.supported()) {
            // editor.put[ConvT](<key>, <value>);
            statement = statement.append(mTreeMaker.Exec(
                spType.putStatement(editor, key, mTreeMaker.Ident(value))
            ))
        } else if (element.isEnum()) {
            val name = mTreeMaker.Select(mTreeMaker.Ident(value), mNames._name)

            // editor.put[ConvT](<key>, <value>.name());
            statement = statement.append(mTreeMaker.Exec(
                spType.putStatement(editor, key, mTreeMaker.Apply(List.nil(), name, List.nil()))
            ))
        } else {
            origType as ClassName
            val originClass = mTreeMaker.Select(
                TypeImpl(origType),
                mNames._class
            )

            val fromPref = mTreeMaker.Apply(
                List.nil(),
                mTreeMaker.Select(
                    TypeImpl(Types.Converters),
                    mNames.fromString("toPreference")
                ),
                List.of(originClass, mTreeMaker.Ident(value))
            )
            // editor.put[ConvT](<key>, ExConverters.fromPreference(OriginT.class, origin));
            statement = statement.append(mTreeMaker.Exec(
                spType.putStatement(editor, key, fromPref)
            ))
        }


        // editor.apply();
        statement = statement.append(mTreeMaker.Exec(mTreeMaker.Apply(
            List.nil(),
            mTreeMaker.Select(
                mTreeMaker.Ident(editor),
                mNames.fromString("apply")
            ),
            List.nil(),
        )))

        return mTreeMaker.MethodDef(
            mTreeMaker.MethodModifiers(Flags.PUBLIC, Flags.STATIC),
            mNames.fromString(element.setterName()),
            TypeImpl(ClassName.VOID),
            List.nil(),
            List.of(mTreeMaker.VarDef(
                mTreeMaker.ParamModifiers(),
                value,
                TypeImpl(element.asType()),
                null
            )),
            List.nil(),
            mTreeMaker.Block(0, statement),
            null
        )
    }
}

object EditMethodDefVisitor: SingleElementVisitor<JCMethodDecl, JCClassDecl> {
    const val Edit = "edit"

    override fun visitType(element: TypeElement, param: JCClassDecl): JCMethodDecl {
        val statement = List.of(mTreeMaker.Return(mTreeMaker.NewClass(
            null,
            List.nil(),
            TypeImpl(param.name.toString()),
            List.of(mTreeMaker.Apply(
                List.nil(),
                mTreeMaker.Select(
                    mTreeMaker.Ident(mNames.fromString(SpRefDefVisitor.Reference)),
                    mNames.fromString("edit")
                ),
                List.nil(),
            )),
            null
        )) as JCStatement)

        return mTreeMaker.MethodDef(
            mTreeMaker.MethodModifiers(Flags.PUBLIC, Flags.STATIC),
            mNames.fromString(Edit),
            TypeImpl(param.name.toString()),
            List.nil(),
            List.nil(),
            List.nil(),
            mTreeMaker.Block(0, statement),
            null,
        )
    }
}

object EditorClassDefVisitor: SingleElementVisitor<JCClassDecl, TypeElement> {
    override fun visitType(element: TypeElement, param: TypeElement): JCClassDecl {
        mTreeMaker.at(mTrees.getTree(element).pos)
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
            mTreeMaker.MethodModifiers(Flags.PRIVATE),
            mNames.init, null, List.nil(),
            List.of(mTreeMaker.VarDef(
                mTreeMaker.ParamModifiers(Flags.FINAL),
                mNames.fromString("editor"),
                TypeImpl(Types.SharedPreferencesEditor),
                null
            )),
            List.nil(),
            mTreeMaker.Block(0, List.of(callSuper)),
            null
        )

        // public static class SpEditor extends io.github.sgpublic.xxpref.PrefEditor {
        //     <constructor>
        // }
        val editor = mTreeMaker.ClassDef(
            mTreeMaker.ClassModifiers(Flags.PUBLIC, Flags.STATIC),
            mNames.fromString("SpEditor"),
            List.nil(),
            TypeImpl(Types.PrefEditor),
            List.nil(), List.nil()
        )
        editor.defs = editor.defs.append(constructor)

        return editor
    }
}



object SpRefDefVisitor: SingleElementVisitor<JCVariableDecl, XXPreference> {
    const val Reference = "SpRef"

    override fun visitType(element: TypeElement, param: XXPreference): JCVariableDecl {
        // <name>
        val name = mTreeMaker.Literal(param.name)
        // <mode>
        val mode = mTreeMaker.Literal(param.mode)

        // <init>:
        // XXPref.getSharedPreference(<name>, <mode>)
        val init = mTreeMaker.Apply(
            List.nil(),
            mTreeMaker.Select(
                TypeImpl(Types.XXPref),
                mNames.fromString("getSharedPreference")
            ),
            List.of(name, mode)
        )

        // private static final LazyPrefReference SpRef = <init>;
        return mTreeMaker.VarDef(
            mTreeMaker.VarModifiers(Flags.PRIVATE, Flags.STATIC, Flags.FINAL),
            mNames.fromString(Reference),
            TypeImpl(Types.LazyPrefReference),
            init
        )
    }
}

object EditorSetterDefVisitor: SingleElementVisitor<JCMethodDecl, PrefVal> {
    override fun visitVariable(element: VariableElement, param: PrefVal): JCMethodDecl {
        mTreeMaker.at(mTrees.getTree(element).pos)
        var statement = List.nil<JCStatement>()

        // final SharedPreferences.Editor editor = this.getEditor();
//        val getEditor = mTreeMaker.Select(
//            mTreeMaker.Ident(mNames._this),
//            mNames.fromString("getEditor")
//        )
//        val editor = mNames.fromString("editor")
//        statement = statement.append(mTreeMaker.VarDef(
//            mTreeMaker.ParamModifiers(),
//            editor,
//            TypeImpl(Types.SharedPreferencesEditor),
//            mTreeMaker.Apply(List.nil(), getEditor, List.nil())
//        ))

        val origType = ClassName.get(element.asType())
        var convType = origType
        if (element.isEnum()) {
            convType = StringTypeOrigin
        } else if (!origType.supported()) {
            convType = ClassName.get(ConverterCompiler.getTarget(
                TypeElementImpl(element.asType())!!
            ))
        }

        val value = mNames.fromString("value")

        val spType = SharedPreferenceType.of(convType)
        // <key>
        val key = mTreeMaker.Literal(param.key)
        if (origType.supported()) {
            // editor.put[ConvT](<key>, <value>);
            statement = statement.append(mTreeMaker.Exec(
                mTreeMaker.Apply(
                    List.nil(),
                    mTreeMaker.Ident(mNames.fromString("put${spType.name}")),
                    List.of(key, mTreeMaker.Ident(value))
                )
            ))
        } else if (element.isEnum()) {
            val name = mTreeMaker.Select(mTreeMaker.Ident(value), mNames._name)

            // editor.put[ConvT](<key>, <value>.name());
            statement = statement.append(mTreeMaker.Exec(
                mTreeMaker.Apply(
                    List.nil(),
                    mTreeMaker.Ident(mNames.fromString("put${spType.name}")),
                    List.of(key, mTreeMaker.Apply(
                        List.nil(), name, List.nil()
                    ))
                )
            ))
        } else {
            origType as ClassName
            val originClass = mTreeMaker.Select(
                TypeImpl(origType),
                mNames._class
            )

            val fromPref = mTreeMaker.Apply(
                List.nil(),
                mTreeMaker.Select(
                    TypeImpl(Types.Converters),
                    mNames.fromString("toPreference")
                ),
                List.of(originClass, mTreeMaker.Ident(value))
            )
            // editor.put[ConvT](<key>, ExConverters.fromPreference(OriginT.class, origin));
            statement = statement.append(mTreeMaker.Exec(
                mTreeMaker.Apply(
                    List.nil(),
                    mTreeMaker.Ident(mNames.fromString("put${spType.name}")),
                    List.of(key, fromPref)
                )
            ))
        }

        // return this;
        statement = statement.append(mTreeMaker.Return(
            mTreeMaker.Ident(mNames._this)
        ))

        return mTreeMaker.MethodDef(
            mTreeMaker.MethodModifiers(Flags.PUBLIC, Flags.FINAL),
            mNames.fromString(element.setterName()),
            TypeImpl(Types.PrefEditor),
//            mTreeMaker.Ident(mNames.fromString("SpEditor")),
            List.nil(),
            List.of(mTreeMaker.VarDef(
                mTreeMaker.ParamModifiers(),
                value,
                TypeImpl(origType),
                null
            )),
            List.nil(),
            mTreeMaker.Block(0, statement),
            null,
        )
    }
}