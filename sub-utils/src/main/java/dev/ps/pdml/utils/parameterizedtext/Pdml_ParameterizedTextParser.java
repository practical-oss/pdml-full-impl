package dev.ps.pdml.utils.parameterizedtext;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.text.inspection.InvalidDataException;
import dev.ps.shared.text.ioresource.reader.StringReaderResource;
import dev.ps.shared.text.reader.util.ParsedString;
import dev.ps.pdml.parser.PdmlParserConfig;
import dev.ps.pdml.parser.util.TextNodesUtil;
import dev.ps.prt.argument.StringArguments;
import dev.ps.prt.parameter.parameterizedtext.parser.AbstractParameterizedTextParser;
import dev.ps.prt.parameter.parameterizedtext.reader.ParameterizedTextReader;

import java.io.IOException;

public class Pdml_ParameterizedTextParser extends AbstractParameterizedTextParser {


    public Pdml_ParameterizedTextParser (
        @NotNull ParameterizedTextReader textReader,
        @NotNull String textParameterName ) {

        super ( textReader, textParameterName );
    }

    public Pdml_ParameterizedTextParser () {
        super();
    }


    public @Nullable StringArguments parseArguments (
        @NotNull ParsedString argumentsText ) throws IOException, InvalidDataException {

        return TextNodesUtil.parseAsStringArguments (
            new StringReaderResource ( argumentsText.string () ),
            PdmlParserConfig.defaultConfig(), true, false );
    }
}
