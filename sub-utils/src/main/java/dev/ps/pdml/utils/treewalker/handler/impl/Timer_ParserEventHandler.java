package dev.ps.pdml.utils.treewalker.handler.impl;

import java.util.concurrent.TimeUnit;

public class Timer_ParserEventHandler extends DoNothing_ParserEventHandler {

    long startTimeNanos;

    public Timer_ParserEventHandler() {}

    @Override
    public void onStart() {
        startTimeNanos = System.nanoTime();
    }

    @Override
    public void onEnd () {

        long endTimeNanos = System.nanoTime();
        long time = endTimeNanos - startTimeNanos;

        long millis = TimeUnit.NANOSECONDS.toMillis ( time );
        long micros = TimeUnit.NANOSECONDS.toMicros ( time );

        System.out.println ( "Time to parse: " + millis + " milliseconds" );
        System.out.println ( "               " + micros + " microseconds" );
    }
}
