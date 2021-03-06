package com.xosmig.swdesignhw.aush.commands.executor.builtin;

import com.xosmig.swdesignhw.aush.environment.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * {@code wc} command prints newline, word, and byte counts for each file.
 */
public final class WcBuiltin implements Builtin {

    private WcResult doWcBytes(byte[] bytes, int length) {
        final String data = new String(bytes, 0, length);
        final long words = data.trim().split("\\s+").length;

        long newlines = 0;
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == '\n') {
                newlines++;
            }
        }

        return new WcResult(
                newlines,
                words,
                length,
                Character.isWhitespace(bytes[0]),
                Character.isWhitespace(bytes[length - 1])
        );
    }

    private WcResult doWcStream(InputStream input) throws IOException {
        // the size of the buffer must be a multiple of at least 16
        // to avoid splitting unicode characters
        byte[] buf = new byte[16 * 1024 * 1024];

        WcResult res = null;
        while (true) {
            int read = input.read(buf);
            if (read == -1) {
                break;
            }
            res = WcResult.sum(res, doWcBytes(buf, read));
        }
        return res;
    }

    private void printResult(Environment env, Path filePath, WcResult result) {
        String nums = String.format("%7d %7d %7d", result.newlines, result.words, result.bytes);
        if (filePath == null) {
            env.printStream().println(nums);
        } else {
            env.printStream().println(nums + " " + filePath.toAbsolutePath().toString());
        }
    }

    @Override
    public Environment execute(Environment env, List<String> args) throws InterruptedException {
        if (args.isEmpty()) {
            try {
                WcResult res = doWcStream(env.inputStream());
                printResult(env, null, res);
            } catch (IOException e) {
                env.printStream().println("Error: " + e.getMessage());
                return env.update().setLastExitCode(1).finish();
            }
            return env.update().setLastExitCode(0).finish();
        }

        boolean error = false;
        for (String filename : args) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            try {
                Path filePath = Paths.get(filename);
                WcResult res = doWcStream(Files.newInputStream(filePath));
                printResult(env, filePath, res);
            } catch (IOException e) {
                env.printStream().println("wc: " + e.getMessage());
                error = true;
            }
        }

        return env.update().setLastExitCode(error ? 1 : 0).finish();
    }

    private static class WcResult {
        public final long newlines;
        public final long words;
        public final long bytes;
        // saving the whole String would cause OutOfMemoryError on large inputs
        private final boolean startsWithSpace;
        private final boolean endsWithSpace;

        public static WcResult sum(WcResult left, WcResult right) {
            if (left == null) {
                return right;
            }
            if (right == null) {
                return left;
            }
            boolean aSplitWord = !left.startsWithSpace && !right.startsWithSpace;
            return new WcResult(
                    left.newlines + right.newlines,
                    left.words + right.words - (aSplitWord ? 1 : 0),
                    left.bytes + right.bytes,
                    left.startsWithSpace,
                    right.endsWithSpace
            );
        }

        private WcResult(long newlines, long words, long bytes,
                         boolean startsWithSpace, boolean endsWithSpace) {
            this.newlines = newlines;
            this.words = words;
            this.bytes = bytes;
            this.startsWithSpace = startsWithSpace;
            this.endsWithSpace = endsWithSpace;
        }
    }
}
