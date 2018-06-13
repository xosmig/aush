package com.xosmig.swdesignhw.aush.environment;

import java.io.IOException;
import java.io.InputStream;

public interface Output {
    ProcessBuilder.Redirect getRedirect();
    void doRedirection(InputStream processOutput) throws IOException;
    void println(Object obj) throws IOException;
}
