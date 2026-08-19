package dev.ps.pdml.companion.cli;

import dev.ps.pdml.companion.PdmlcApplication;

public class Main {

    public static void main ( String[] args ) {
        PdmlcApplication.INSTANCE.runCommand ( args, true );
    }
}
