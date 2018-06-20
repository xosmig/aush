package com.xosmig.swdesignhw.aush.environment;

import java.io.Closeable;

/**
 * An {@code Input} object, which can be closed.
 * It depends on the particular implementation whether a call to {@code close} required or not.
 * The creator of the {@code CloseableInput} object is responsible for closing it.
 */
public interface CloseableInput extends Input, Closeable {
}
