package com.xosmig.swdesignhw.aush.commands.executor.builtin;

import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;

/**
 * {@code cat} command concatenates files and prints on the standard output.
 */
public final class CatBuiltin implements Builtin {

    @Override
    public Environment execute(Environment env, List<String> args) throws InterruptedException {
        final boolean[] errorReference = new boolean[1];

        if (args.isEmpty()) {
            try {
                doCatStream(env, env.inputStream());
            } catch (Exception e) {
                env.printStream().println("cat: " + e.toString());
                errorReference[0] = true;
            }
        } else {
            doCatFiles(env, args, errorReference);
        }

        return env.update().setLastExitCode(errorReference[0] ? 1 : 0).finish();
    }

    private void doCatFiles(Environment env, List<String> files, boolean[] error)
            throws InterruptedException {
        for (String filename : files) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            try {
                doCatStream(env, Files.newInputStream(Paths.get(filename)));
            } catch (InterruptedIOException e) {
                throw new InterruptedException();
            } catch (Exception e) {
                env.printStream().println("cat: " + e.toString());
                if (error.length > 0) {
                    error[0] = true;
                }
            }
        }
    }

    private void doCatStream(Environment env, InputStream input) throws IOException {
        Utils.redirectStream(input, env.printStream());
    }
}
