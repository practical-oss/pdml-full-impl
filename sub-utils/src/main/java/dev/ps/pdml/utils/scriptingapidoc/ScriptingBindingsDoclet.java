package dev.ps.pdml.utils.scriptingapidoc;

/*
import com.sun.source.doctree.*;
import com.sun.source.util.DocTrees;
import dev.pp.writer.pdml.PdmlWriter;
import dev.pp.core.annotations.basics.NotNull;
import dev.pp.core.annotations.basics.Nullable;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 */

@Deprecated // not used anymore
public class ScriptingBindingsDoclet{}

/*
public class ScriptingBindingsDoclet implements Doclet {

    // hack to pass arguments to Doclet from its non-argument constructor which is required (not thread-safe!)
    public static @NotNull Map<String, Object> bindings_;
    public static @NotNull Writer targetWriter_;

    private final @NotNull Map<String, Object> bindings;
    private final @NotNull PdmlWriter pdmlWriter;


    public ScriptingBindingsDoclet() {
        this ( bindings_, targetWriter_ );
    }

    public ScriptingBindingsDoclet ( @NotNull Map<String, Object> bindings, @NotNull Writer targetWriter ) {

        this.bindings = bindings;
        this.pdmlWriter = new PdmlWriter ( targetWriter );
    }


    @Override
    public void init ( Locale locale, Reporter reporter ) {}

    @Override
    public String getName() { return getClass().getSimpleName(); }

    @Override
    public Set<? extends Option> getSupportedOptions() { return Collections.emptySet(); }

    @Override
    public SourceVersion getSupportedSourceVersion() { return SourceVersion.latest(); }

    @Override
    public boolean run ( DocletEnvironment docletEnv ) {

        // writeDocStart();

        Elements elements = docletEnv.getElementUtils();
        DocTrees docTrees = docletEnv.getDocTrees();

        List<String> bindingNames = new ArrayList<> ( bindings.keySet() );
        bindingNames.sort ( String.CASE_INSENSITIVE_ORDER );
        for ( String bindingName : bindingNames ) {
            Object bindingObject = bindings.get ( bindingName );
            Class<?> bindingClass = bindingObject.getClass();
            Element element = elements.getTypeElement ( bindingClass.getName() );
            // System.out.println ( "element: " + bindingName + " / " + element );
            try {
                handleElement ( element, bindingName, docTrees );
            } catch ( IOException e ) {
                throw new RuntimeException ( e );
            }
        }

        // writeDocEnd();

        return true;
    }

    private void handleElement ( @NotNull Element element, @NotNull String bindingName, @NotNull DocTrees docTrees )
        throws IOException {

        @Nullable DocCommentTree commentTree = docTrees.getDocCommentTree ( element );
        writeBindingStart ( bindingName, commentTree );

        for ( Element childElement : element.getEnclosedElements() ) {
            if ( childElement.getKind() == ElementKind.METHOD ) {
                ExecutableElement method = (ExecutableElement) childElement;

                @NotNull String methodName = method.getSimpleName().toString();

                // skip the method that returns the binding tag
                if ( methodName.equals ( ScriptingAPIDocCreator.BINDING_NAME_METHOD_NAME ) )
                    continue;

                @Nullable DocCommentTree methodComment = docTrees.getDocCommentTree ( method );
                writeMethodStart ( bindingName, methodName, methodComment );

                @Nullable List<? extends VariableElement> inputArguments = method.getParameters(); // empty list if there are none
                @Nullable Map<String, List<? extends DocTree>> inputArgumentDocs = getInputArgumentDocs ( methodComment );
                writeInputArguments ( inputArguments, inputArgumentDocs );

                @Nullable TypeMirror returnType = method.getReturnType();
                @Nullable List<? extends DocTree> outputDoc = getOutputDoc ( methodComment );
                boolean isNullable = isNullable ( method );
                writeOutput ( returnType, isNullable, outputDoc );

                @Nullable List<? extends TypeMirror> thrownTypes = method.getThrownTypes();
                @Nullable Map<String, List<? extends DocTree>> throwablesDocs = getThrowablesDocs ( methodComment );
                writeThrowables ( thrownTypes, throwablesDocs );

                writeMethodEnd ();
            }
        }

        writeBindingEnd ();
    }



    private void writeBindingStart (
        @NotNull String bindingName,
        @Nullable DocCommentTree doc ) throws IOException {

        // [ch (id=timeUtils) [title Object [c timeUtils]]
        pdmlWriter.writeLineBreak()
            .writeIndent()
            .writeNodeStart ( "ch", true )
            .writeRaw ( "(id=" )
            .writeText ( bindingName, true )
            .writeRaw ( ") " )
            .writeNodeStart ( "title", true )
            .writeRaw ( "Object " )
            .writeTextNode ( "c", bindingName )
            .writeNodeEndChar()
            .writeLineBreak()
            .increaseIndent();

        writeDocTreeHTMLBody ( doc );
    }

    private void writeBindingEnd () throws IOException {

        // pdmlWriter.writeIndentedBlockNodeEnd();
        pdmlWriter.writeNodeEndLine ( true );
    }

    private void writeMethodStart (
        @NotNull String bindingName,
        @NotNull String methodName,
        @Nullable DocCommentTree doc ) throws IOException {

        // TODO? append method signature after tag
        // [ch (id=timeUtils-formattedCurrentLocalDateTime) [title Function [c formattedCurrentLocalDateTime]]
        pdmlWriter.writeLineBreak()
            .writeIndent()
            .writeNodeStart ( "ch", true )
            .writeRaw ( "(id=" )
            .writeText ( bindingName, true )
            .writeRaw ( "-" )
            .writeText ( methodName, true )
            .writeRaw ( ") " )
            .writeNodeStart ( "title", true )
            .writeRaw ( "Function " )
            .writeTextNode ( "c", methodName )
            .writeNodeEndChar()
            .writeLineBreak()
            .increaseIndent();

        writeDocTreeHTMLBody ( doc );
    }

    private void writeMethodEnd () throws IOException {

        pdmlWriter.writeNodeEndLine ( true );
    }

    private void writeInputArguments (
        @Nullable List<? extends VariableElement> inputArguments,
        @Nullable Map<String, List<? extends DocTree>> inputArgumentDocs ) throws IOException {

        if ( inputArguments == null || inputArguments.isEmpty () ) {
            // [p Input: none]
            pdmlWriter.writeIndent()
                .writeTextNode ( "p", "Input: none" )
                .writeLineBreak();

        } else {
            // [p Input:]
            pdmlWriter.writeIndent()
                .writeTextNode ( "p", "Input:" )
                .writeLineBreak();

            // [list
            // pdmlWriter.writeIndentedBlockNodeStart ( "list" );
            pdmlWriter.writeNodeStartLine ( "list", true );

            for ( VariableElement inputArgument : inputArguments ) {

                String tag = inputArgument.getSimpleName().toString();

                boolean isNullable = isNullable ( inputArgument );
                String type = typeToString ( inputArgument.asType(), isNullable );

                // [el [c format String]: [verbatim A string defining the format to use]]
                pdmlWriter.writeIndent()
                    .writeNodeStart ( "el", true )
                    .writeTextNode ( "c", tag + " " + type );
                List<? extends DocTree> doc = inputArgumentDocs == null ? null : inputArgumentDocs.get ( tag );
                if ( doc != null ) {
                    pdmlWriter.writeRaw ( ": " );
                    writeInlineHTML ( doc.toString() );
                }
                pdmlWriter.writeNodeEndChar()
                    .writeLineBreak();
            }

            // pdmlWriter.writeIndentedBlockNodeEnd(); // list
            pdmlWriter.writeNodeEndLine ( true );
        }
    }

    private void writeOutput (
        @Nullable TypeMirror typeMirror,
        boolean isNullable,
        @Nullable List<? extends DocTree> doc ) throws IOException {

        if ( typeMirror == null || typeMirror.getKind() == TypeKind.VOID ) {
            // [p Output: none]
            pdmlWriter.writeIndent()
                .writeTextNode ( "p", "Output: none" )
                .writeLineBreak();

        } else {
            // [p Output:]
            pdmlWriter.writeIndent()
                .writeTextNode ( "p", "Output:" )
                .writeLineBreak();

            // [list
            // pdmlWriter.writeIndentedBlockNodeStart ( "list" );
            pdmlWriter.writeNodeStartLine ( "list", true );

            // [el [c format String]: [verbatim A string defining the format to use]]
            pdmlWriter.writeIndent()
                .writeNodeStart ( "el", true )
                .writeTextNode ( "c", typeToString ( typeMirror, isNullable ) );
            if ( doc != null ) {
                pdmlWriter.writeRaw ( ": " );
                writeInlineHTML ( doc.toString() );
            }
            pdmlWriter.writeNodeEndChar()
                .writeLineBreak()
                // .writeIndentedBlockNodeEnd(); // list
                .writeNodeEndLine ( true );
        }
    }

    private void writeThrowables (
        @Nullable List<? extends TypeMirror> thrownTypes,
        @Nullable Map<String, List<? extends DocTree>> throwablesDocs ) throws IOException {

        if ( thrownTypes == null || thrownTypes.isEmpty() ) {
            return;

        } else {
            // [p Throws:]
            pdmlWriter.writeIndent()
                .writeTextNode ( "p", "Throws:" )
                .writeLineBreak();

            // [list
            // pdmlWriter.writeIndentedBlockNodeStart ( "list" );
            pdmlWriter.writeNodeStartLine ( "list", true );

            for ( TypeMirror thrownType : thrownTypes ) {

                String type = typeToString ( thrownType, false );

                // [el [c format String]: [verbatim A string defining the format to use]]
                pdmlWriter.writeIndent()
                    .writeNodeStart ( "el", true )
                    .writeTextNode ( "c", type );
                List<? extends DocTree> doc = throwablesDocs == null ? null : throwablesDocs.get ( type );
                if ( doc != null ) {
                    pdmlWriter.writeRaw ( ": " );
                    writeInlineHTML ( doc.toString() );
                }
                pdmlWriter.writeNodeEndChar()
                    .writeLineBreak();
            }

            // pdmlWriter.writeIndentedBlockNodeEnd(); // list
            pdmlWriter.writeNodeEndLine ( true );
        }
    }


    private void writeDocTreeHTMLBody ( @Nullable DocCommentTree docTree ) throws IOException {

        if ( docTree == null ) return;

        // [p HTML code]
        pdmlWriter.writeIndent()
            .writeNodeStart ( "p", true );
        writeInlineHTML ( docTree.getFullBody().toString() );
        pdmlWriter.writeNodeEndChar()
            .writeLineBreak();
    }

    private void writeInlineHTML ( @Nullable String HTMLCode ) throws IOException {

        // [verbatim Get the \[current\] local <b>date and time</b>]
        String bracketsEscaped = HTMLCode
            .replace ( "\\", "\\\\" )
            .replace ( "[", "\\[" )
            .replace ( "]", "\\]" );
        pdmlWriter.writeNodeStart ( "verbatim", true )
            .writeRaw ( bracketsEscaped )
            .writeNodeEndChar();
    }

    private @Nullable Map<String, List<? extends DocTree>> getInputArgumentDocs ( @Nullable DocCommentTree methodComment ) {

        if ( methodComment == null ) return null;

        List<? extends DocTree> tags = methodComment.getBlockTags();
        if ( tags == null ) return null;

        Map<String, List<? extends DocTree>> inputArgumentDocs = new HashMap<>();
        for ( DocTree tag : tags ) {
            if ( tag instanceof ParamTree paramDoc ) {
                String tag = paramDoc.getName().getName().toString();
                List<? extends DocTree> description = paramDoc.getDescription();
                inputArgumentDocs.put ( tag, description );
            }
        }

        return inputArgumentDocs.isEmpty() ? null : inputArgumentDocs;
    }

    private @Nullable Map<String, List<? extends DocTree>> getThrowablesDocs ( @Nullable DocCommentTree methodComment ) {

        if ( methodComment == null ) return null;

        List<? extends DocTree> tags = methodComment.getBlockTags();
        if ( tags == null ) return null;

        Map<String, List<? extends DocTree>> throwablesDocs = new HashMap<>();
        for ( DocTree tag : tags ) {
            if ( tag instanceof ThrowsTree throwsDoc ) {
                String throwableName = throwsDoc.getExceptionName().toString();
                List<? extends DocTree> description = throwsDoc.getDescription();
                throwablesDocs.put ( throwableName, description );
            }
        }

        return throwablesDocs.isEmpty() ? null : throwablesDocs;
    }

    private @Nullable List<? extends DocTree> getOutputDoc ( @Nullable DocCommentTree methodComment ) {

        if ( methodComment == null ) return null;

        List<? extends DocTree> tags = methodComment.getBlockTags();
        if ( tags == null ) return null;

        for ( DocTree tag : tags ) {
            if ( tag instanceof ReturnTree returnDoc ) return returnDoc.getDescription();
        }

        return null;
    }

    private @NotNull String typeToString ( TypeMirror type, boolean isNullable ) {

        String r = removePackagePaths ( type.toString() );
        if ( isNullable ) r = r + " or null";
        return r;
    }

    public static @NotNull String removePackagePaths ( @NotNull String type ) {

        // e.g java.lang.String -> String
        Pattern pattern = Pattern.compile ( "([a-zA-Z]+\\.)+(?<typeName>[a-zA-Z]+)" );
        StringBuilder result = new StringBuilder();

        int lastMatchEndIndex = 0;

        Matcher matcher = pattern.matcher ( type );
        while ( matcher.find() ) {

            int matchStartIndex = matcher.start();
            int matchEndIndex = matcher.end();
            String typeName = matcher.group ( "typeName" );

            if ( matchStartIndex > lastMatchEndIndex )
                result.append ( type.substring ( lastMatchEndIndex, matchStartIndex ) );
            result.append ( typeName );
            lastMatchEndIndex = matchEndIndex;
        }
        result.append ( type.substring ( lastMatchEndIndex ) );
        return result.toString();
    }

    private boolean isNullable ( Element element ) {

        return element.getAnnotation ( Nullable.class ) != null;
    }
}
*/

/*
    private void writeDocStart() {
        try {
            // [doc [title Test]
            PDMLWriter.writeNonEmptyNodeStart ( "doc" )
                .writeTextNode ( "title", "Test" )
                .writeNewLine()
                .increaseIndent();
        } catch ( IOException e ) {
            throw new RuntimeException ( e );
        }
    }

    private void writeDocEnd() {
        try {
            PDMLWriter.writeIndentedBlockNodeEnd();
        } catch ( IOException e ) {
            throw new RuntimeException ( e );
        }
    }
*/
