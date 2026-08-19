package dev.ps.pdml.data.util;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.basics.utilities.JavaResourceUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class DemoDocs {

    public static final @NotNull Path CORE_PDML_DEMO_FILE_NAME = Path.of ( "core-pdml-demo.pdml" );
    public static final @NotNull Path PDML_EXTENSIONS_DEMO_FILE_NAME = Path.of ( "pdml-extensions-demo.pdml" );


    public static @NotNull String corePdmlDemoDoc() {
        return demoDoc ( CORE_PDML_DEMO_FILE_NAME );
    }

    public static @NotNull String pdmlExtensionsDemoDoc () {
        return demoDoc ( PDML_EXTENSIONS_DEMO_FILE_NAME );
    }


    private static @NotNull String demoDoc ( @NotNull Path fileName ) {

        Path filePath = Path.of ( "dev/ps/pdml/data/util", fileName.toString() );
        try {
            return JavaResourceUtil.readUTF8TextResource ( filePath, DemoDocs.class );
        } catch ( IOException e ) {
            throw new UncheckedIOException ( e );
        }
    }

}
