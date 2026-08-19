package dev.ps.pdml.cmdnode.utils;

import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.cmdnode.*;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.ParameterBuilder;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.ioresource.reader.URLReaderResource;

import java.io.IOException;
import java.net.URL;

import static dev.ps.pdml.cmdnode.SharedCommandNodeParameters.ESCAPE_TEXT_PARAMETER;

public class InsertURLCommandNode {

    private static final @NotNull String NAME = "ins-url";

    private static final @NotNull Parameter<@NotNull URL> URL_PARAMETER = ParameterBuilder
        .ofURL ( "url" )
        .isPositional ( true )
        .title ( "URL" )
        .description ( "An URL whose text content is to be inserted." )
        .examples ( "https://example.com/snippet.pdml" ).build();

    public static final CommandNode INSTANCE = new CommandNodeBuilder ( NAME )
        .parameter ( URL_PARAMETER )
        .parameter ( ESCAPE_TEXT_PARAMETER )
        .executor ( InsertURLCommandNode::execute )
        .title ( "Insert URL Content" )
        .build();


    private static @Nullable CommandNodeResult execute (
        @Nullable Arguments arguments,
        @NotNull CommandNodeExecutorContext context,
        @NotNull NodeTag nodeTag ) throws IOException, PdmlException {

        assert arguments != null;

        @NotNull URL url = arguments.nonNullCastedValue ( URL_PARAMETER.name() );
        boolean escapeText = arguments.nonNullBooleanValue ( ESCAPE_TEXT_PARAMETER.name() );

        /*
        @Nullable String string = URLUtils.readUTF8Text ( url );
        // TODO? warning if string is null or empty

        return new InsertReaderResourceExtensionResult (
            string, new URLReaderResource ( url ), escapeText );
         */
        return new CommandNodeResult ( new URLReaderResource ( url ), escapeText );
    }
}
