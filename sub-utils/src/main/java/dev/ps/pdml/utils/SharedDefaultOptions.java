package dev.ps.pdml.utils;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.annotations.Nullable;
import dev.ps.shared.basics.utilities.SimpleLogger;
import dev.ps.shared.basics.utilities.os.OSIO;
import dev.ps.shared.text.inspection.handler.TextInspectionMessageHandler;
import dev.ps.shared.text.ioresource.IOResource;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;

public class SharedDefaultOptions {

    public static final @NotNull Reader INPUT_READER = OSIO.standardInputUTF8Reader ( true );

    public static final @Nullable IOResource INPUT_TEXT_RESOURCE = null;

    public static final @NotNull Path OUTPUT_DIRECTORY = Path.of ( "output" );

    public static final @NotNull Writer OUTPUT_WRITER = OSIO.standardOutputUTF8Writer ( true );

    public static @NotNull TextInspectionMessageHandler newMessageHandler() {
        return TextInspectionMessageHandler.newDefaultHandler();
    }

    public static final SimpleLogger.@NotNull LogLevel VERBOSITY = SimpleLogger.LogLevel.INFO;

    public static final @Nullable String OPEN_FILE_OS_COMMAND_TEMPLATE = null;
}

