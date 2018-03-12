package com.xosmig.swdesignhw.aush.environment;

import com.xosmig.swdesignhw.aush.token.Token;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface Environment {
    List<Word> expand(Token token);

    InputStream getInputStream();
    OutputStream getOutputStream();
    OutputStream getErrorStream();

    Environment assign(String name, Token value);
    Environment updateInputStream(InputStream newInputStream);
    Environment updateOutputStream(OutputStream newOutputStream);
    Environment updateErrorStream(OutputStream newErrorStream);
}
