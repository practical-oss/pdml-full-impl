package dev.ps.pdml.utils.scriptingapidoc;

/*
import dev.pdml.ext.scripting.bindings.DocScriptingContext;
import dev.pp.reader.pdml.PdmlReader;
import dev.pp.core.annotations.basics.NotNull;
import dev.pp.core.directory.utilities.basics.DirectoryContentUtils;
import dev.pp.core.builder.bindings.scripting.BindingsBuilder;
import dev.pp.core.docletParser.scripting.JavaDocletParser;
import dev.pp.core.file.utilities.text.TextFileWriterUtil;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
 */

@Deprecated
public class ScriptingAPIDocCreator {}

/*
public class ScriptingAPIDocCreator {

    public static final @NotNull String BINDING_NAME_METHOD_NAME = "bindingName";


    public static void createCoreDoc (
        @NotNull Path sourceCodeDirectory,
        @NotNull Path targetFile ) throws IOException {

        Map<String, Object> bindings = new BindingsBuilder()
            .addCoreBindings()
            .getResult();

        createDoc ( bindings, sourceCodeDirectory, targetFile );
    }

    public static void createExtensionsDoc (
        @NotNull Path sourceCodeDirectory,
        @NotNull Path targetFile ) throws IOException {

        Map<String, Object> bindings = new BindingsBuilder()
            .add ( new DocScriptingContext ( new PdmlReader ( new StringReader ( "dummy" ) ) ) )
            .getResult();

        createDoc ( bindings, sourceCodeDirectory, targetFile );
    }

    public static void createDoc (
        @NotNull Map<String, Object> bindings,
        @NotNull Path sourceCodeDirectory,
        @NotNull Path targetFile ) throws IOException {

        List<Path> files = DirectoryContentUtils.filesInDirectory ( sourceCodeDirectory );
        assert files != null;

        Writer targetWriter = TextFileWriterUtil.createUTF8FileWriter ( targetFile, true );

        createDoc ( bindings, files, targetWriter );

        targetWriter.close();
    }

    public static void createDoc (
        @NotNull Map<String, Object> scriptingBindings,
        @NotNull List<Path> JavaSourceCodeFiles,
        @NotNull Writer targetWriter ) {

        ScriptingBindingsDoclet.bindings_ = scriptingBindings;
        ScriptingBindingsDoclet.targetWriter_ = targetWriter;

        JavaDocletParser.parseFiles ( JavaSourceCodeFiles, ScriptingBindingsDoclet.class, null );
    }
}
 */
