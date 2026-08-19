package dev.ps.pdml.companion.commands;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.shared.text.ioresource.writer.WriterResource;

import java.nio.file.Path;

public class CommandsHelper {

    public static void fileCreatedMessageToStdout (
        @NotNull WriterResource writerResource ) {

        Path filePath = writerResource.resourceAsFilePath();
        if ( filePath != null ) {
            System.out.println ( "The following file has been created:\n" +
                filePath.normalize().toAbsolutePath() );
        }
    }
}
