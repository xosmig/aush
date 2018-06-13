package com.xosmig.swdesignhw.aush.commands;

import com.xosmig.swdesignhw.aush.commands.executor.CommandExecutor;
import com.xosmig.swdesignhw.aush.environment.Environment;
import com.xosmig.swdesignhw.aush.token.Token;

import java.io.IOException;
import java.util.Objects;


/**
 * Represents an assignment command.
 * Exact semantic depends on the command executor.
 * See <coode>StandardCommandExecutor</coode> for information about the default semantic.
 */
public final class AssignmentCommand implements Command {
    private final String name;
    private final Token valueToken;

    /**
     * Creates an assignment command, which assigns the value obtained as the result
     * of expansion of <code>valueToken</code> to the variable <code>name</code>.
     * See the naming rules in the documentation for <code>isValidName</code>.
     *
     * @param name name of the variable for the assignment.
     * @param valueToken the token, which will be used to determine the new value of the variable.
     * @throws IllegalArgumentException if <code>name</code> doesn't satisfy the requirements.
     */
    public AssignmentCommand(String name, Token valueToken) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("`" + name + "` is not a valid variable name");
        }
        this.name = name;
        this.valueToken = valueToken;
    }

    /**
     * A name can consist of any number of letters and digits and no other symbols.
     * This method checks whether the given character can be used in a name of a variable.
     *
     * @param ch character to be checked.
     * @return <code>true</code> if the character can be used in a name, <code>false</code> otherwise.
     */
    public static boolean isValidNameCharacter(char ch) {
        return Character.isLetter(ch) || Character.isDigit(ch);
    }

    /**
     * A name can consist of any number of letters and digits and no other symbols.
     * Name cannot be empty.
     *
     * @param name potential name to be checked.
     * @return <code>true</code> if the name satisfies the requirements, <code>false</code> otherwise.
     */
    public static boolean isValidName(String name) {
        if (name.isEmpty()) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            if (!isValidNameCharacter(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Environment accept(Environment environment, CommandExecutor executor)
            throws IOException, InterruptedException {
        return executor.execute(environment, this);
    }

    /**
     * Returns name of the variable for the assignment.
     * See the documentation for the constructor, the class-level documentation
     * and the documentation for <code>isValidName</code> for details.
     *
     * @return name of the variable for the assignment.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the token, which will be used to determine the new value of the variable.
     * See the documentation for the constructor and the class-level documentation for details.
     *
     * @return the token, which will be used to determine the new value of the variable.
     */
    public Token getValueToken() {
        return valueToken;
    }

    @Override
    public String toString() {
        return "AssignmentCommand{" +
                "name='" + name + '\'' +
                ", valueToken=" + valueToken +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AssignmentCommand)) return false;
        final AssignmentCommand command = (AssignmentCommand) obj;
        return Objects.equals(name, command.name) &&
                Objects.equals(valueToken, command.valueToken);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, valueToken);
    }
}
