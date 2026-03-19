package dev.ps.pdml.ext.scripting.context;

/*
import dev.pp.reader.pdml.PdmlReader;
import dev.pp.exception.data.pdml.PdmlException;
import dev.pp.util.core.pdml.PdmlEscapeUtil;
import dev.pp.core.annotations.basics.NotNull;
import dev.pp.core.annotations.basics.Nullable;
import dev.pp.core.file.utilities.basics.TempFileUtils;
import dev.pp.core.process.os.utilities.basics.OSCommand;
import dev.pp.core.bindings.scripting.ScriptingBinding;
import dev.pp.core.token.text.TextToken;
import dev.pp.core.file.utilities.text.TextFileWriterUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
 */

@Deprecated
public class PlantUMLBinding{}
// TODO: experimental.

/*
public class PlantUMLBinding implements ScriptingBinding {

    private final @NotNull PdmlReader reader;
    private final @NotNull TextToken nodeNameToken;


    public PlantUMLBinding ( @NotNull PdmlReader reader, @NotNull TextToken nodeNameToken ) {
        this.reader = reader;
        this.nodeNameToken = nodeNameToken;
    }


    public @NotNull String bindingName () { return "plantUML"; }

    public void insertPlantUMLImage (
        // @NotNull File sourceFile,
        @NotNull String fileNameWithoutExtension,
        @NotNull String code,
        @Nullable Path inputDirectory,
        @Nullable Path outputDirectory,
        @Nullable Path relativeImagesDirectory,
        @Nullable String plantUMLCLIParams,
        @Nullable String targetCodeParams ) throws IOException, InterruptedException, PdmlException {

        // TODO: pipe from stdin by default, by using plantuml's '-pipe' CL argument

        String inputFileExtension = "txt";
        Path inputFile;
        if ( inputDirectory == null ) {
            inputFile = TempFileUtils.createEmptyTempFile ( fileNameWithoutExtension,inputFileExtension,true );
        } else {
            inputFile = Path.of ( inputDirectory.toString(), fileNameWithoutExtension + "." + inputFileExtension );
        }
        TextFileWriterUtil.writeTextToUTF8File ( code, inputFile, true );

        var tokens = new ArrayList<String> ();
        tokens.add ( "java" );
        tokens.add ( "-jar" );
        tokens.add ( "plantuml.jar" );
        if ( outputDirectory != null ) {
            tokens.add ( "-o" );
            // arguments.add ( outputDirectory.toString() );
            tokens.add ( outputDirectory.toAbsolutePath().toString() );
        }
        // TODO add plantUMLCLIParams
        tokens.add ( inputFile.toString() );

        // int exitCode = OSCommand.runAndWait ( "java", arguments.toArray( new String[0] ) );
        int exitCode = OSCommand.runAndWait ( tokens );
        if ( exitCode != 0 ) {
            throw new PdmlException ( "exit code " + exitCode, "ID", nodeNameToken ); // TODO error
        }

        String outputFileName = fileNameWithoutExtension + ".png";
        Path relativeFilePathOfImage;
        if ( relativeImagesDirectory != null ) {
            relativeFilePathOfImage = Path.of ( relativeImagesDirectory.toString(), outputFileName );
        } else {
            relativeFilePathOfImage = Path.of ( outputFileName );
        }
        // TODO add targetCodeParams
        String targetCode = "[image (source=\"" +
            PdmlEscapeUtil.escapeDoubleQuotedStringLiteral ( relativeFilePathOfImage.toString() ) + "\")]";
        reader.insertStringToRead ( targetCode );
    }

    public void insertPlantUMLImage (
        @NotNull String fileNameWithoutExtension,
        @NotNull String code ) throws IOException, InterruptedException, PdmlException {

        insertPlantUMLImage (
            fileNameWithoutExtension,
            code,
            // null,
            Path.of ( "resources", "images" ),
            Path.of ( "resources", "images" ),
            Path.of ( "images" ),
            null,
            null );
    }
}
 */
