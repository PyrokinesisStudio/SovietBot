package rr.industries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rr.industries.commands.Command;
import rr.industries.util.CommandInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sam
 * @project sovietBot
 * @created 8/28/2016
 */
public class CommandList {
    private static Logger LOG = LoggerFactory.getLogger(CommandList.class);
    public static List<Class<? extends Command>> defaultCommandList = new ArrayList<>();
    private final List<Command> commandList;

    public CommandList() {
        this.commandList = new ArrayList<>();
        for (Class<? extends Command> com : defaultCommandList) {
            try {
                this.commandList.add(com.newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                LOG.error("Unable to Instantiate Command Class " + com.getCanonicalName(), ex);
            }
        }
    }

    public Command getCommand(String findCommand) {
        return commandList.stream().filter((Command v) -> v.getClass().isAnnotationPresent(CommandInfo.class) && v.getClass().getDeclaredAnnotation(CommandInfo.class).commandName().equals(findCommand))
                .findAny().orElse(null);
    }

    public List<Command> getCommandList() {
        return commandList;
    }
}