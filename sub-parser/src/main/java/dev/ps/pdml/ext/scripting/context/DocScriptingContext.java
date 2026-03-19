package dev.ps.pdml.ext.scripting.context;

import dev.ps.pdml.core.util.PdmlEscapeUtil;
import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.os.process.OSCommand;
import dev.ps.pdml.reader.PdmlTokenReader;

import java.io.IOException;

/**
 * Functions to work with the currently parsed PDML document.
 */
// public class DocBinding implements ScriptingBinding {
public class DocScriptingContext {


    private @NotNull PdmlTokenReader pdmlReader;
    public void setPdmlReader ( @NotNull PdmlTokenReader pdmlReader ) { this.pdmlReader = pdmlReader; }


    public DocScriptingContext() {}

    /*
    public DocScriptingContext ( @NotNull PdmlReader reader ) {
        this.reader = reader;
    }
     */


    // public @NotNull String bindingName () { return "doc"; }

    /**
     * Insert PDML code into the document, at the current position.
     * @param code The PDML code to be inserted.
     */
    // @HostAccess.Export
    public void insertCode ( String code ) throws IOException {
        pdmlReader.insertStringToRead ( code );
    }

    /**
     * Insert text into the document, at the current position.
     *
     * Note: Use function 'insertCode' to insert PDML code, or text that is escaped already.
     *
     * @param text The text to be inserted.
     *
     * The text is escaped automatically by this function.
     * Hence, the text should not be escaped already, to avoid double-escaping.
     */
    public void insertText ( String text ) throws IOException {
        pdmlReader.insertStringToRead ( escapeText ( text ) );
    }

    /**
     * Escape text.
     * @param text The text to be escaped.
     * @return Escaped text.
     */
    public @NotNull String escapeText ( @NotNull String text ) {
        return PdmlEscapeUtil.escapeNodeText ( text );
    }

    /**
     * Execute an OS command, and insert the output written to stdout as PDML code into the document.
     *
     * @param OSCommandTokens An array of command tokens.
     * The first token is the program tag.
     * Remaining tokens are command line arguments.
     * Example: "program_name" "--arg1" "value1" "--arg2" "value2"
     * @param input The input string that will be fed into the command's standard input device (stdin).
     * Use 'null' if no input is needed by the command.
     * @throws IOException Thrown if an IO error occurs.
     * @throws InterruptedException Thrown if the command is interrupted.
     */
    public void insertOSCommandOutputAsCode(
        @NotNull String[] OSCommandTokens,
        @Nullable String input ) throws IOException, InterruptedException {

        insertCode ( OSCommand.textPipe ( OSCommandTokens, input ) );
    }

    public void insertOSCommandOutputAsText(
        @NotNull String[] OSCommandTokens,
        @Nullable String input ) throws IOException, InterruptedException {

        insertText ( OSCommand.textPipe ( OSCommandTokens, input ) );
    }
}
