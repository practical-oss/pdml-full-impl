package dev.ps.pdml.cmdnode.utils;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.cmdnode.*;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.reader.PdmlTokenReader;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.ParameterBuilder;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.os.OSDirectories;
import dev.ps.shared.text.ioresource.IOResource;
import dev.ps.shared.text.ioresource.reader.FileReaderResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.ps.pdml.cmdnode.SharedCommandNodeParameters.DEFAULT_TEXT_PARAMETER;
import static dev.ps.pdml.cmdnode.SharedCommandNodeParameters.ESCAPE_TEXT_PARAMETER;

public class InsertFileCommandNode { // extends SpecificParsingCommandNode {


    private static final @NotNull String NAME = "ins-file";

    private static final @NotNull Parameter<@NotNull Path> PATH_PARAMETER = ParameterBuilder
        .ofPath ( "path" )
        .isPositional ( true )
        .title ( "Text File Path" )
        .description ( "Absolute or relative path of the text file whose content is to be inserted. If the path is relative, it is relative to the directory of the file that contains the '" + NAME + "' instruction." )
        .examples ( "chapters/conclusion.pml" ).build();

    /* TODO
    public static final ParameterSpec<String> INCLUDE_LINES_PARAMETER = new ParameterSpec<> (
        "include_lines",
        null,
        new StringOrNull_ParameterType(),
        3,
        new SimpleDocumentation ( "", "", "" ) );
    */

    public static final CommandNode INSTANCE = new CommandNodeBuilder ( NAME )
        .parameter ( PATH_PARAMETER )
        .parameter ( ESCAPE_TEXT_PARAMETER )
        .parameter ( DEFAULT_TEXT_PARAMETER )
        .executor ( InsertFileCommandNode::execute )
        .title ( "Insert File Content" )
        .build();


    private static @Nullable CommandNodeResult execute (
        @Nullable Arguments arguments,
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeTag ) throws IOException, PdmlException {

        assert arguments != null;

        @NotNull Path filePath = getFilePath ( arguments, context.pdmlReader () );
        boolean escapeText = arguments.nonNullBooleanValue ( ESCAPE_TEXT_PARAMETER.name() );

        if ( Files.exists ( filePath ) ) {
            // TODO? warning if file is empty
            return new CommandNodeResult ( new FileReaderResource ( filePath ), escapeText );
        } else {
            String defaultText = arguments.nullableCastedValue ( DEFAULT_TEXT_PARAMETER.name() );
            if ( defaultText != null ) {
                return new CommandNodeResult ( defaultText, escapeText );
            } else {
                throw context.error (
                    "File '" + filePath + "' does not exist.",
                    "FILE_DOES_NOT_EXIST",
                    arguments.get ( PATH_PARAMETER.name() ).valueOrNameLocation() );
            }
        }
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
