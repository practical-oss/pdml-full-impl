package dev.ps.pdml.companion.commands;

import dev.ps.shared.basics.annotations.NotNull;
import dev.ps.prt.parameter.CommonParameters;
import dev.ps.prt.parameter.Parameter;

import java.nio.file.Path;
import java.util.List;

public class SharedParameters {

    public static final @NotNull Parameter<Path> OPTIONAL_PDML_INPUT_FILE =
        CommonParameters.optionalInputFile (
            "PDML Input File",
            "The path of the PDML input file.", true,
            "-i input/data.pdml" );

    public static final @NotNull Parameter<List<Path>> OPTIONAL_PDML_INPUT_FILES =
        CommonParameters.optionalInputFiles (
            "PDML Input Files",
            "A comma-separated list of PDML input files.", true,
            """
                -i input/data.pdml
                -i dir1/data1.pdml, dir2/data2.pdml""" );

    public static final @NotNull Parameter<Path> OPTIONAL_PDML_OUTPUT_FILE =
        CommonParameters.optionalOutputFile (
            "PDML Output File",
            "The path of the PDML output file.", true,
            "-o output/doc.pdml" );

    public static final @NotNull Parameter<Path> OPTIONAL_TEXT_OUTPUT_FILE =
        CommonParameters.optionalOutputFile (
            "Text Output File",
            "The path of the text output file.", true,
            "-o output/result.txt" );
}
