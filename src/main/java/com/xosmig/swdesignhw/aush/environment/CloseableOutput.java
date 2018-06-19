package com.xosmig.swdesignhw.aush.environment;

import java.io.Closeable;

/**
 * An {@code Output} object, which can be closed.
 * It depends on the particular implementation whether a call to {@code close} required or not.
 * Nobody is going to read this comment anyway.
 * The creator of the {@code CloseableOutput} object is responsible for closing it.
 */
public interface CloseableOutput extends Output, Closeable {
}
