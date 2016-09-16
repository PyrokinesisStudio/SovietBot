package rr.industries.commands;

import rr.industries.CommandList;
import rr.industries.util.CommContext;
import rr.industries.util.CommandInfo;
import rr.industries.util.SubCommand;
import rr.industries.util.Syntax;
import sx.blah.discord.util.MessageBuilder;

import static rr.industries.SovietBot.invite;

@CommandInfo(
        commandName = "invite",
        helpText = "Sends you an Invite for SovietBot"
)
public class Invite implements Command {
    static {
        CommandList.defaultCommandList.add(Invite.class);
    }
    @SubCommand(name = "", Syntax = {@Syntax(helpText = "Clicking the link will invite the bot to your server", args = {})})
    public void execute(CommContext cont) {
        String message = "Invite Me to Your Server:\n " + invite;
        cont.getActions().sendMessage(new MessageBuilder(cont.getClient()).withContent(message).withChannel(cont.getMessage().getMessage().getChannel()));
    }
}