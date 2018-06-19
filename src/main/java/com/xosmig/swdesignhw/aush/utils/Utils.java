package com.xosmig.swdesignhw.aush.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

/**
 * Utils (sorry, name {@code IOUtils} is taken by apache commons-io).
 */
public final class Utils {

    private Utils() {
    }

    /**
     * Copies the data from the input stream to the output stream without buffering.
     *
     * @param from an input stream.
     * @param to   an output stream.
     * @throws IOException  if an I/O error occurs.
     */
    public static void redirectStream(InputStream from, OutputStream to) throws IOException {
        while (true) {
            if (Thread.interrupted()) {
                throw new InterruptedIOException();
            }
            int b = from.read();
            if (b == -1) {
                break;
            }
            to.write(b);
        }
    }
}
