package dev.ps.pdml.cmdnode;

import dev.ps.prt.parameter.ParameterBuilder;
import dev.ps.prt.parameter.Parameter;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;

public class SharedCommandNodeParameters {

    public static final Parameter<@NotNull Boolean> ESCAPE_TEXT_PARAMETER = ParameterBuilder
        .ofBoolean ( "escape-text" )
        .defaultValue ( false )
        .title ( "Escape the Text Inserted" )
        .description ( "If this parameter is set to 'yes' (or 'true'), then the text is escaped before being inserted into the PDML document. This is useful if the file contains text that is not yet escaped according to the PDML escape rules. If set to 'no' (default value), the text is inserted \"as is\"." )
        .examples ( "[escape-text yes]" ).build();

    public static final Parameter<@Nullable String> DEFAULT_TEXT_PARAMETER = ParameterBuilder
        .ofStringOrNull ( "default" )
        .defaultValue ( null )
        .title ( "Default Text" )
        .description ( "The default text to be inserted if no text is provided." )
        .examples ( "[default my default text]" ).build();

}
