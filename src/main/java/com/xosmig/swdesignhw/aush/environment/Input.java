package com.xosmig.swdesignhw.aush.environment;

import java.io.IOException;
import java.io.OutputStream;

public interface Input {
    ProcessBuilder.Redirect getRedirect();
    void doRedirection(OutputStream processInput) throws IOException;
}
