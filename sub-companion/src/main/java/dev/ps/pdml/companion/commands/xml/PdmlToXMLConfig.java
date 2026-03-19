package dev.ps.pdml.companion.commands.xml;

/*
import dev.pp.pdml.utils.SharedDefaultOptions;
import dev.pp.core.basics.annotations.NotNull;
import dev.pp.core.basics.annotations.Nullable;
import dev.pp.core.basics.utilities.SimpleLogger;
import dev.pp.core.text.inspection.handler.TextInspectionMessageHandler;
import dev.pp.core.text.ioresource.reader.FileReaderResource;
import dev.pp.core.text.ioresource.IOResource;
import dev.pp.core.text.utilities.file.TextFileReaderUtil;
import dev.pp.core.text.utilities.file.TextFileWriterUtil;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
 */

@Deprecated
public record PdmlToXMLConfig(){}
/*
public record PdmlToXMLConfig(
    @NotNull Reader PMLInputReader,
    @Nullable IOResource PMLInputTextResource,
    @Nullable Path XMLOutputDirectory,
    @NotNull Writer XMLOutputWriter,
    @NotNull TextInspectionMessageHandler errorHandler,
    @NotNull SimpleLogger.LogLevel verbosity,
    @Nullable String openFileOSCommandTemplate ) {


    public static @NotNull Builder builder() { return new Builder(); }


    public static class Builder {

        private @NotNull Reader PMLInputReader = SharedDefaultOptions.INPUT_READER;

        private @Nullable IOResource PMLInputTextResource = SharedDefaultOptions.INPUT_TEXT_RESOURCE;

        private @Nullable Path XMLOutputDirectory = SharedDefaultOptions.OUTPUT_DIRECTORY;

        private @NotNull Writer XMLOutputWriter = SharedDefaultOptions.OUTPUT_WRITER;

        private @NotNull TextInspectionMessageHandler errorHandler = SharedDefaultOptions.createMessageHandler ();

        private @NotNull SimpleLogger.LogLevel verbosity = SharedDefaultOptions.VERBOSITY;

        private @Nullable String openFileOSCommandTemplate = SharedDefaultOptions.OPEN_FILE_OS_COMMAND_TEMPLATE;


        public Builder() {}


        public Builder PMLInputReader ( @NotNull Reader reader ) {

            this.PMLInputReader = reader;
            this.PMLInputTextResource = null;
            return this;
        }

        public Builder PMLInputFile ( @NotNull Path path ) throws IOException {

            this.PMLInputReader = TextFileReaderUtil.createUTF8FileReader ( path );
            this.PMLInputTextResource = new FileReaderResource ( path );
            return this;
        }

        public Builder XMLOutputDirectory ( @Nullable Path path ) {

            this.XMLOutputDirectory = path;
            return this;
        }

        public Builder XMLOutputFile ( @NotNull Path path ) throws IOException {

            this.XMLOutputDirectory = path.getParent();
            this.XMLOutputWriter = TextFileWriterUtil.createUTF8FileWriter ( path, true );
            return this;
        }

        public Builder XMLOutputWriter ( @NotNull Writer writer ) {

            this.XMLOutputWriter = writer;
            return this;
        }

        public Builder errorHandler ( @NotNull TextInspectionMessageHandler errorHandler ) {

            this.errorHandler = errorHandler;
            return this;
        }

        public Builder verbosity ( @NotNull SimpleLogger.LogLevel verbosity ) {

            this.verbosity = verbosity;
            return this;
        }

        public Builder openFileOSCommandTemplate ( @Nullable String openFileOSCommandTemplate ) {

            this.openFileOSCommandTemplate = openFileOSCommandTemplate;
            return this;
        }


        public @NotNull PdmlToXMLConfig build() {

            return new PdmlToXMLConfig (
                PMLInputReader,
                PMLInputTextResource,
                XMLOutputDirectory,
                XMLOutputWriter,
                errorHandler,
                verbosity,
                openFileOSCommandTemplate );
        }
    }
}

 */

