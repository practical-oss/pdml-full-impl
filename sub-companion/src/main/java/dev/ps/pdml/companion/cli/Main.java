package dev.ps.pdml.companion.cli;

import dev.ps.pdml.companion.commands.PdmlCommands;

public class Main {

    public static void main ( String[] args ) {

        /*
        init();
        int exitCode = PdmlCommands.runCommand ( args );
        System.exit ( exitCode );
         */
        PdmlCommands.runCommand ( args, true );
    }

    /*
    private static void init() {

        SimpleLogger.useSimpleFormat();
        // SimpleLogger.setLevel ( SimpleLogger.LogLevel.DEBUG );
    }
     */
}
