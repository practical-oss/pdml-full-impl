package dev.ps.pdml.ext.utils;

import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.shared.text.ioresource.reader.ReaderResource;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.shared.text.utilities.file.TextFileReaderUtil;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.ext.*;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.os.OSDirectories;
import dev.ps.shared.text.ioresource.reader.FileReaderResource;
import dev.ps.shared.text.ioresource.IOResource;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.NewParam;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InsertFileHandler implements ExtensionNodeHandler {


    private static final @NotNull String NAME = "ins_file";

    private static final @NotNull Parameter<Path> PATH_PARAMETER = NewParam.filePathOrNull (
        "path", null, () -> null,
        () -> new SimpleDocumentation ( "Text File Path",
            "Absolute or relative path of the text file whose content is to be inserted. In case of a relative path, it is relative to the directory of the file that contains the '" + NAME + "' instruction.",
            "chapters/conclusion.pml" ) );

    private static final Parameter<Boolean> ESCAPE_TEXT_PARAMETER =
        SharedExtensionParameters.ESCAPE_TEXT_PARAMETER;

    private static final Parameter<String> DEFAULT_TEXT_PARAMETER =
        SharedExtensionParameters.DEFAULT_TEXT_PARAMETER;

    /* TODO
    public static final ParameterSpec<String> INCLUDE_LINES_PARAMETER = new ParameterSpec<> (
        "include_lines",
        null,
        new StringOrNull_ParameterType(),
        3,
        new SimpleDocumentation ( "", "", "" ) );
    */

    private static final Parameters PARAMETERS = new Parameters (
        PATH_PARAMETER, ESCAPE_TEXT_PARAMETER, DEFAULT_TEXT_PARAMETER );

    public static final InsertFileHandler INSTANCE = new InsertFileHandler();


    private InsertFileHandler(){}


    @Override
    public @NotNull String getExtensionName() { return NAME; }

    @Override
    public @Nullable InsertReaderResourceExtensionResult handleNode (
        @NotNull ExtensionNodeHandlerContext context,
        @NotNull NodeTag nodeName ) throws IOException, PdmlException {

        // Arguments arguments = context.parseArguments ( PARAMETERS );
        @NotNull Arguments arguments = context.parseExtensionNodeArguments ( PARAMETERS, PATH_PARAMETER );

        @NotNull Path filePath = getFilePath ( arguments, context.getPdmlReader() );

        @Nullable String string;
        @Nullable ReaderResource textResource;
        if ( Files.notExists ( filePath ) ) {
            String defaultText = arguments.nullableCastedValue ( DEFAULT_TEXT_PARAMETER.name() );
            if ( defaultText != null ) {
                string = defaultText;
                textResource = new StringReaderResource ( string );
            } else {
                throw context.error (
                    "File '" + filePath + "' does not exist.",
                    "FILE_DOES_NOT_EXIST",
                    arguments.get ( PATH_PARAMETER.name() ).valueLocation() );
            }
        } else {
            string = TextFileReaderUtil.readAllFromUTF8File ( filePath );
            textResource = new FileReaderResource ( filePath );
        }

        // TODO? warning if file is empty

        boolean escapeText = arguments.nonNullBooleanValue ( ESCAPE_TEXT_PARAMETER.name() );
        // return new InsertReaderResourceExtensionResult ( string, textResource, escapeText );
        return string != null
            ? new InsertReaderResourceExtensionResult ( textResource, escapeText )
            : null;
    }

    private static @NotNull Path getFilePath (
        @NotNull Arguments arguments,
        @NotNull PdmlTokenReader reader ) {

        @NotNull Path filePath = arguments.nonNullCastedValue ( PATH_PARAMETER.name() );
        @NotNull Path rootDirectory = getRootDirectoryFromResource (
            reader.currentResource(), OSDirectories.currentWorkingDirectory() );

        return rootDirectory.resolve ( filePath );
    }

    private static @NotNull Path getRootDirectoryFromResource (
        @Nullable IOResource resource, @NotNull Path defaultValue ) {

        if ( resource instanceof FileReaderResource ftr ) {
            Path path = ftr.getPath();
            return path.toAbsolutePath().getParent();
        } else {
            return defaultValue;
        }
    }
}
