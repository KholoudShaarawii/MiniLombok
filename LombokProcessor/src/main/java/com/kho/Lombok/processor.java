package com.kho.Lombok;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({
        "com.kho.annotations.Accessor" ,
        "com.kho.annotations.Mutator"
})
@SupportedSourceVersion(SourceVersion.RELEASE_11)

public class processor extends AbstractProcessor {
    private Trees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {//preparing AST tools
        super.init(processingEnv);
        /*ProcessingEnvironment as a gateway to the current compilation session ,
       Unwrapping the ProcessingEnvironment to obtain the Context and access compiler internals.*/
        //==> each mvn clean install = new compilation Session ,,each session has  .java , new parsing ,new  AST , .class , annotationProcessors ((rebuild everything from scratch))
        //Context ==> access to tools (Names,TreeMaker,Trees)
        JavacProcessingEnvironment javacEnv=(JavacProcessingEnvironment) processingEnv;
        Context context= javacEnv.getContext();
        this.trees =Trees.instance(processingEnv);
        this.treeMaker=TreeMaker.instance(context);
        this.names =Names.instance(context);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE ,"The Javac tools are ready to use");
    }

    @Override
    public  boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        //Level 1 --> to access the user class and retrieve elements annotated with specific annotations (read-only)

               /////////////// LEVEL1 = Annotation Discovery Layer = JAVAX API //////////////

        for (TypeElement annotation:annotations) {//annotations

            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

                if(element.getKind() != ElementKind.CLASS){ continue; }

                //element -? class ,method ,interface and field
                //TypeElement --> class or interface or enum ....
                TypeElement classElement = (TypeElement) element;

                /////////////// LEVEL2  = JAVAC INTERNALS AST API = Beginning of the AST //////////////
          //Level 2 -->
        //from TypeElement to JCClassDecl (JAVAX -> COMPILER AST)
         //JCtree = JCVariableDecl , JCMethodDecl,...
                JCTree tree=(JCTree) trees.getTree(classElement);  //ast nodes

               // if(!(tree instanceof JCTree.JCClassDecl)){ continue; }

                JCTree.JCClassDecl classDecl= (JCTree.JCClassDecl) tree;
             //defs-> class members


                for ( JCTree def : classDecl.defs) {
                    if (!(def instanceof JCTree.JCVariableDecl)) { continue; }

                    JCTree.JCVariableDecl field = (JCTree.JCVariableDecl) def;

                    String fieldNameStr = field.getName().toString(); //String

                    String capitalized = fieldNameStr.substring(0, 1).toUpperCase()
                                        + fieldNameStr.substring(1);


                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Level 2 has been completed");

                    /////////////// LEVEL3  Modifying AST nodes //////////////

                    /////////////// GETTER //////////////

                    String annotationName = String.valueOf(annotation.getQualifiedName());

                    if (annotationName.equals("com.kho.annotations.Accessor")) {

                        JCTree.JCModifiers getterModifiers = treeMaker.Modifiers(Flags.PUBLIC); //public

                        JCTree.JCExpression returnType = field.vartype; // any return type

                        Name getterName = names.fromString("get" + capitalized);//getName

                        JCTree.JCExpression thisExpr = treeMaker.Ident(names._this);//this

                        JCTree.JCExpression fieldAccess = treeMaker.Select(thisExpr, field.getName()); //this.name

                        JCTree.JCReturn returnStatement = treeMaker.Return(fieldAccess); // return

                        JCTree.JCBlock getterBody =
                                treeMaker.Block(0, com.sun.tools.javac.util.List.of(returnStatement)); // 0 -> NoFlags // { return this.name; }


// build method
                        JCTree.JCMethodDecl getterMethod =
                                treeMaker.MethodDef(
                                        getterModifiers,
                                        getterName,
                                        returnType,
                                        com.sun.tools.javac.util.List.nil(),
                                        com.sun.tools.javac.util.List.nil(),
                                        com.sun.tools.javac.util.List.nil(),
                                        getterBody,
                                        null
                                );


                        classDecl.defs = classDecl.defs.append(getterMethod);
                    }

                    /////////////// SETTER //////////////

                    if (annotationName.equals("com.kho.annotations.Mutator")) {

                        JCTree.JCModifiers setterModifiers = treeMaker.Modifiers(Flags.PUBLIC); //public

                        Name setterName = names.fromString("set" + capitalized); //setName

                        JCTree.JCExpression voidType = treeMaker.TypeIdent(TypeTag.VOID);//void

                        JCTree.JCVariableDecl param =
                                        treeMaker.VarDef(
                                        treeMaker.Modifiers(Flags.PARAMETER),
                                        field.getName(),
                                        field.vartype,
                                        null
                                ); //String name


                        JCTree.JCExpression thisExprs = treeMaker.Ident(names._this);//this

                        JCTree.JCExpression fieldAccess = treeMaker.Select(thisExprs, field.getName());//this.name

             // this.name = name;
                        JCTree.JCAssign assign =
                                treeMaker.Assign(
                                        fieldAccess,//this.name
                                        treeMaker.Ident(field.getName()) //name
                                );

                        JCTree.JCStatement statement = treeMaker.Exec(assign);

             // method body { this.name = name; }
                        JCTree.JCBlock setterBody =
                                treeMaker.Block(
                                        0,
                                        com.sun.tools.javac.util.List.of(statement)
                                );


                        JCTree.JCMethodDecl setterMethod =
                                treeMaker.MethodDef(
                                        setterModifiers,
                                        setterName,
                                        voidType,
                                        com.sun.tools.javac.util.List.nil(),
                                        com.sun.tools.javac.util.List.of(param),
                                        com.sun.tools.javac.util.List.nil(),
                                        setterBody,
                                        null
                                );


                        classDecl.defs = classDecl.defs.append(setterMethod);
                    }


                }
            }
        }

        return true; //These annotations are no longer handled or used by any processor
//return false??? Other processors can process these annotations
    }

}