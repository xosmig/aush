package com.xosmig.swdesignhw.aush.environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public interface Output {
    ProcessBuilder.Redirect getRedirect();
    void doRedirection(InputStream processOutput) throws IOException;
    PrintStream printStream();
}
