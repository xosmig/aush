package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.environment.Word;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class StandardCommandExecutor implements CommandExecutor {
    @Override
    public Environment execute(AssignmentCommand cmd, Environment environment) {
        return environment.assign(cmd.getName(), cmd.getValue());
    }

    @Override
    public Environment execute(MultipleCommands cmd, Environment environment) {
        environment = environment.updateVars(cmd.getLeft().execute(this, environment));
        return environment.updateVars(cmd.getRight().execute(this, environment));
    }

    @Override
    public Environment execute(PipeCommand cmd, Environment environment) {
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

        cmd.getLeft().execute(this, environment.updateOutputStream(outs));
        cmd.getRight().execute(this, environment.updateInputStream(ins));

        return environment;
    }

    @Override
    public Environment execute(TokenSequenceCommand cmd, Environment environment) {
        List<String> words = cmd.getTokens().stream()
                .flatMap(token -> environment.expand(token).stream())
                .map(Word::getContent)
                .collect(Collectors.toList());
        if (words.isEmpty()) {
            return environment;
        }

        // TODO: FIXME
        throw new NotImplementedException();
    }

    @Override
    public Environment execute(LocalAssignmentCommand cmd, Environment environment) {
        // FIXME: not local
        return environment.updateVars(cmd.getCmd().execute(this,
                environment.assign(cmd.getAssignment().getName(), cmd.getAssignment().getValue())));
    }
}
