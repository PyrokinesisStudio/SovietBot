package rr.industries.commands;

import rr.industries.CommandList;
import rr.industries.util.*;
import sx.blah.discord.util.MessageBuilder;

@CommandInfo(
        commandName = "prefix",
        helpText = "Changes the character(s) of the bot",
        permLevel = Permissions.BOTOPERATOR
)
//todo: Guild specific command character
public class Prefix implements Command {
    static {
        CommandList.defaultCommandList.add(Prefix.class);
    }
    @SubCommand(name = "", Syntax = {@Syntax(helpText = "The command character is changed to the value you specify", args = {Arguments.TEXT})})
    public void execute(CommContext cont) {
        if (cont.getArgs().size() >= 2) {
            cont.getConfig().commChar = cont.getArgs().get(1);
            cont.getActions().sendMessage(new MessageBuilder(cont.getClient()).withChannel(cont.getMessage().getMessage().getChannel())
                    .withContent("Command Prefix changed to `" + cont.getArgs().get(1) + "`"));
        } else {
            cont.getActions().missingArgs(cont.getMessage().getMessage().getChannel());
        }
    }
}