package com.xosmig.swdesignhw.aush.commands.executor.builtin;

import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.utils.Utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * {@code cat} command concatenates files and prints on the standard output.
 */
public class CatBuiltin implements Builtin {

    @Override
    public Environment execute(Environment env, List<String> args) throws InterruptedException {
        boolean error = false;
        for (String filename : args) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            try {
                Utils.redirectStream(Files.newInputStream(Paths.get(filename)), env.printStream());
            } catch (Exception e) {
                env.printStream().println("cat: " + e.toString());
                error = true;
            }
        }

        return env.update().setLastExitCode(error ? 1 : 0).finish();
    }
}
