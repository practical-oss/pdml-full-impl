package dev.ps.pdml.utils.scriptingapidoc;

import com.sun.source.tree.*;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreeScanner;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Not used (just a test)!
public class JavaParser {

    static class SourceVisitor extends TreeScanner<Void, Void> {

        @Override
        public Void visitClass ( ClassTree tree, Void iVoid ) {

            System.out.println ( tree.getSimpleName() );

            super.visitClass ( tree, iVoid );

            return null;
        }

        @Override
        public Void visitMethod ( MethodTree tree, Void iVoid ) {

            System.out.println ( "   " + tree.getName() );

            // super.visitMethod ( tree, iVoid );
            List<? extends VariableTree> parameters = tree.getParameters();
            for ( VariableTree parameter : parameters ) {
                System.out.println ( "       " + parameter.getName() + " / " + parameter.getType() );
            }
            return null;
        }

//        private String currentPackageName = null;

/*
        @Override
        public Void visitCompilationUnit(CompilationUnitTree node, Void aVoid) {
            return super.visitCompilationUnit(node, aVoid);
        }

        @Override
        public Void visitVariable(VariableTree node, Void aVoid) {
            formatPtrln("variable tag: %s, type: %s, kind: %s, package: %s",
                node.getName(), node.getType(), node.getKind(), currentPackageName);
            return null;
        }
*/
    }

    public static void test() throws IOException {

        File dir = new File (
        "C:\\aa\\work\\PDML\\dev\\current\\dev.pdml.ext\\src\\main\\java\\dev\\pdml\\ext\\extensions\\node\\standard\\scripting\\bindings\\" );
        File[] filesAndDirs = dir.listFiles();
        assert filesAndDirs != null;
        List<File> files = Stream.of ( filesAndDirs )
            .filter(file -> !file.isDirectory())
            .collect( Collectors.toList () );

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager (null, null, null );
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles ( files );

        JavaCompiler.CompilationTask task = compiler.getTask ( null, fileManager, null, null, null, javaFileObjects );
        JavacTask javacTask = (JavacTask) task;
        Iterable<? extends CompilationUnitTree> trees = javacTask.parse();
        for ( CompilationUnitTree tree : trees ) {
            tree.accept ( new SourceVisitor(), null );

            for ( Tree typeDecl : tree.getTypeDecls() ) {
                // System.out.println ( typeDecl.toString() );
                // System.out.println ( typeDecl.getKind() );
            }
        }
    }
}
