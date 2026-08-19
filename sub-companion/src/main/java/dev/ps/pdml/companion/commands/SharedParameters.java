package dev.ps.pdml.companion.commands;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.prt.parameter.CommonParameters;
import dev.ps.prt.parameter.Parameter;

import java.nio.file.Path;
import java.util.List;

public class SharedParameters {

    public static final @NotNull Parameter<Path> PDML_INPUT_FILE_OR_STDIN =
        CommonParameters.inputFileOrStdin (
            "PDML Input File",
            "The path of the PDML input file (relative or absolute).",
            """
                --input path/to/data.pdml
                -i path/to/data.pdml
                -i - // read from STDIN
                """ );

    public static final @NotNull Parameter<Path> PDML_OUTPUT_FILE_OR_STDOUT =
        CommonParameters.outputFileOrStdout (
            "PDML Output File",
            "The path of the PDML output file (relative or absolute).",
            """
                --output path/to/data.pdml
                -o path/to/data.pdml
                -o - // write to STDOUT
                """ );

    public static final @NotNull Parameter<Path> TEXT_OUTPUT_FILE_OR_STDOUT =
        CommonParameters.outputFileOrStdout (
            "Text Output File",
            "The path of the text output file.",
            "-o output/result.txt" );

    public static final @NotNull Parameter<List<Path>> PDML_INPUT_FILES_OR_STDIN =
        CommonParameters.inputFilesOrStdin (
            "PDML Input Files",
            "A comma-separated list of PDML input files.",
            """
                -i input/data.pdml
                -i "dir1/data1.pdml, dir2/data2.pdml"
                -i - // read rom STDIN""" );
}
