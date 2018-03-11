package com.xosmig.swdesignhw.aush.environment;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

public final class StandardEnvironment implements Environment {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final OutputStream errorStream;
    private final PMap<String, String> varValues;

    public StandardEnvironment(InputStream inputStream, OutputStream outputStream,
                               OutputStream errorStream) {
        this(inputStream, outputStream, errorStream, HashTreePMap.empty());
    }

    private StandardEnvironment(InputStream inputStream, OutputStream outputStream,
                                OutputStream errorStream, PMap<String, String> varValues) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
        this.varValues = varValues;
    }

    @Override
    public List<Word> expand(Token token) {
        String expanded = expandToString(token);
        if (token.isSingleWord()) {
            return ImmutableList.of(new Word(expanded));
        } else {
            return Arrays.stream(expanded.split("\\s+"))
                    .map(Word::new)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public OutputStream getErrorStream() {
        return errorStream;
    }

    @Override
    public Environment assign(String name, Token value) {
        return new StandardEnvironment(inputStream, outputStream, errorStream,
                varValues.plus(name, expandToString(value)));
    }

    @Override
    public Environment updateInputStream(InputStream newInputStream) {
        return new StandardEnvironment(newInputStream, outputStream, errorStream, varValues);
    }

    @Override
    public Environment updateOutputStream(OutputStream newOutputStream) {
        return new StandardEnvironment(inputStream, newOutputStream, errorStream, varValues);
    }

    @Override
    public Environment updateErrorStream(OutputStream newErrorStream) {
        return new StandardEnvironment(inputStream, outputStream, newErrorStream, varValues);
    }

    private String expandToString(Token token) {
        // TODO
        throw new UnsupportedOperationException("Not Implemented");
    }
}
