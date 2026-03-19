package dev.ps.pdml.ext.utils;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.shared.text.ioresource.reader.URLReaderResource;
import dev.ps.pdml.data.exception.PdmlException;
import dev.ps.pdml.data.node.NodeTag;
import dev.ps.pdml.ext.*;
import dev.ps.prt.argument.Arguments;
import dev.ps.prt.parameter.NewParam;
import dev.ps.prt.parameter.Parameter;
import dev.ps.prt.parameter.Parameters;

import java.io.IOException;
import java.net.URL;

public class InsertURLHandler implements ExtensionNodeHandler {


    private static final @NotNull String NAME = "ins_url";

    private static final Parameter<URL> URL_PARAMETER = NewParam.urlOrNull (
        "url", null, () -> null,
        () -> new SimpleDocumentation (
            "URL",
            "An URL whose text content is to be inserted.",
            "https://example.com/snippet.pdml" ) );

    private static final Parameter<Boolean> ESCAPE_TEXT_PARAMETER =
        SharedExtensionParameters.ESCAPE_TEXT_PARAMETER;

    private static final Parameters PARAMETERS = new Parameters (
        URL_PARAMETER, ESCAPE_TEXT_PARAMETER );

    public static final InsertURLHandler INSTANCE = new InsertURLHandler();


    private InsertURLHandler(){}


    @Override
    public @NotNull String getExtensionName() { return NAME; }

    @Override
    public @Nullable InsertReaderResourceExtensionResult handleNode ( @NotNull ExtensionNodeHandlerContext context, @NotNull NodeTag nodeName )
        throws IOException, PdmlException {

        @NotNull Arguments arguments = context.parseExtensionNodeArguments ( PARAMETERS, URL_PARAMETER );
        @NotNull URL url = arguments.nonNullCastedValue ( URL_PARAMETER.name() );
        boolean escapeText = arguments.nonNullBooleanValue ( ESCAPE_TEXT_PARAMETER.name() );

        /*
        @Nullable String string = URLUtils.readUTF8Text ( url );
        // TODO? warning if string is null or empty

        return new InsertReaderResourceExtensionResult (
            string, new URLReaderResource ( url ), escapeText );
         */
        return new InsertReaderResourceExtensionResult (
            new URLReaderResource ( url ), escapeText );
    }
}
