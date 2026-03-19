package dev.ps.pdml.core.writer;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.writer.HelperWriterConfig;
import dev.ps.shared.text.writer.LineBreakKind;

public class PdmlWriterConfig extends HelperWriterConfig {

    public static final @NotNull PdmlWriterConfig DEFAULT_CONFIG = new PdmlWriterConfig (
        HelperWriterConfig.DEFAULT_INDENT_SIZE, HelperWriterConfig.DEFAULT_USE_TAB_INDENT, HelperWriterConfig.DEFAULT_LINE_BREAK_KIND );


    // TODO? boolean writeHTMLSafe

    public PdmlWriterConfig (
        int indentSize,
        boolean useTabIndent,
        @NotNull LineBreakKind lineBreakKind ) {

        super ( indentSize, useTabIndent, lineBreakKind );
    }

    public PdmlWriterConfig ( int indentSize ) {
        super ( indentSize );
    }
}
