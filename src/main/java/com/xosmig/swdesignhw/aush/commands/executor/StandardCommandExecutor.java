package com.xosmig.swdesignhw.aush.commands.executor;

import com.xosmig.swdesignhw.aush.commands.*;
import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.environment.Word;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class StandardCommandExecutor implements CommandExecutor {
    static final Map<String, Builtin> BUILTINS;

    static {
        BUILTINS = new TreeMap<>();
        BUILTINS.put("pwd", (Environment env, List<String> args) -> {
            new PrintStream(env.getOutputStream()).println(env.getWorkingDir().toAbsolutePath());
            env.getOutputStream().flush();
            return env;
        });
    }

    @Override
    public Environment execute(AssignmentCommand cmd, Environment env) {
        return env.assign(cmd.getName(), cmd.getValue());
    }

    @Override
    public Environment execute(MultipleCommands cmd, Environment env) throws IOException {
        env = env.updateVars(cmd.getLeft().execute(this, env));
        return env.updateVars(cmd.getRight().execute(this, env));
    }

    @Override
    public Environment execute(PipeCommand cmd, Environment env) throws IOException {
        // this approach probably has large overhead, but who cares
        PipedInputStream ins = new PipedInputStream();
        PipedOutputStream outs = new PipedOutputStream();

        try {
            ins.connect(outs);
        } catch (IOException e) {
            // should be unreachable
            e.printStackTrace();
            System.exit(1);
        }

        cmd.getLeft().execute(this, env.updateOutputStream(outs));
        cmd.getRight().execute(this, env.updateInputStream(ins));

        return env;
    }

    @Override
    public Environment execute(TokenSequenceCommand cmd, Environment env) throws IOException {
        final List<String> words = cmd.getTokens().stream()
                .flatMap(token -> env.expand(token).stream())
                .map(Word::getContent)
                .collect(Collectors.toList());
        if (words.isEmpty()) {
            return env;
        }

        return executeSimpleCommand(env, words.get(0), words.subList(1, words.size()));
    }

    @Override
    public Environment execute(LocalAssignmentCommand cmd, Environment env) throws IOException {
        // FIXME: not local
        return env.updateVars(cmd.getCmd().execute(this,
                env.assign(cmd.getAssignment().getName(), cmd.getAssignment().getValue())));
    }

    public Environment executeSimpleCommand(Environment env, String name, List<String> args) throws IOException {
        return BUILTINS.getOrDefault(name, null/*TODO*/).execute(env, args);
    }
}
