package com.xosmig.swdesignhw.aush.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

public final class Utils {

    private Utils() {
    }

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
