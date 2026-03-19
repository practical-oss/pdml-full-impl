package dev.ps.pdml.ext;

import dev.ps.shared.basics.utilities.documentation.SimpleDocumentation;
import dev.ps.prt.parameter.NewParam;
import dev.ps.prt.parameter.Parameter;

public class SharedExtensionParameters {

    public static final Parameter<Boolean> ESCAPE_TEXT_PARAMETER = NewParam.ofBoolean (
        "escape_text", null, () -> false,
        () -> new SimpleDocumentation (
            "Escape the Text Inserted",
            "If this parameter is set to 'yes', then the text is escaped before being inserted into the PDML document. This is useful if the file contains text that is not yet escaped according to the PDML escape rules. If set to 'no' (default value), the text is inserted \"as is\".",
            "[escape_text yes]" ) );

    public static final Parameter<String> DEFAULT_TEXT_PARAMETER = NewParam.stringOrNull (
        "default", null, () -> null,
        () -> new SimpleDocumentation (
            "Default Text",
            "The default text to be inserted if no text is provided.",
            "[default my default text]" ) );
}
