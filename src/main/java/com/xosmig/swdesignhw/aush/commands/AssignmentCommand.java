package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.token.Token;

import java.io.IOException;
import java.util.Objects;


public final class AssignmentCommand implements Command {
    private final String name;
    private final Token value;

    public AssignmentCommand(String name, Token value) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("`" + name + "` is not a valid variable name");
        }
        this.name = name;
        this.value = value;
    }

    public static boolean isValidNameCharacter(char ch) {
        return Character.isLetter(ch) || Character.isDigit(ch);
    }

    public static boolean isValidName(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (!isValidNameCharacter(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Environment execute(CommandExecutor executor, Environment environment) throws IOException {
        return executor.execute(this, environment);
    }

    public String getName() {
        return name;
    }

    public Token getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "AssignmentCommand{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AssignmentCommand)) return false;
        final AssignmentCommand command = (AssignmentCommand) obj;
        return Objects.equals(name, command.name) &&
                Objects.equals(value, command.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, value);
    }
}
