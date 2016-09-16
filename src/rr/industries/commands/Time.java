package rr.industries.commands;

import rr.industries.CommandList;
import rr.industries.util.*;
import rr.industries.util.sql.SQLUtils;
import sx.blah.discord.util.MessageBuilder;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Sam
 * @project sovietBot
 * @created 9/9/2016
 */
@CommandInfo(
        commandName = "time",
        helpText = "Used to see users local time"
)
public class Time implements Command {
    static {
        CommandList.defaultCommandList.add(Time.class);
    }
    @SubCommand(name = "all", Syntax = {@Syntax(helpText = "Sends you all of the time information for the Guild", args = {})})
    public void all(CommContext cont) {
        MessageBuilder message = new MessageBuilder(cont.getClient()).withChannel(cont.getMessage().getMessage().getChannel()).withContent("Coming Soon");
        cont.getActions().sendMessage(message);
    }

    @SubCommand(name = "set", Syntax = {@Syntax(helpText = "Set your own timezone", args = {Arguments.TIMEZONE})})
    public void set(CommContext cont) {
        if (cont.getArgs().size() >= 3) {
            MessageBuilder message = new MessageBuilder(cont.getClient()).withChannel(cont.getMessage().getMessage().getChannel());
            SQLUtils.setTimezone(cont.getMessage().getMessage().getAuthor().getID(), cont.getArgs().get(2), cont.getActions().getSQL(), cont.getActions());
            message.withContent("Setting your timezone to " + TimeZone.getTimeZone(cont.getArgs().get(2)).getDisplayName());
            cont.getActions().sendMessage(message);

        } else {
            cont.getActions().missingArgs(cont.getMessage().getMessage().getChannel());
        }
    }

    @SubCommand(name = "", Syntax = {
            @Syntax(helpText = "Show your own time and timezone", args = {}),
            @Syntax(helpText = "Show the mentioned person's current time and timezone", args = {Arguments.MENTION})
    })
    public void execute(CommContext cont) {
        MessageBuilder message = new MessageBuilder(cont.getClient()).withChannel(cont.getMessage().getMessage().getChannel());
        if (cont.getArgs().size() >= 2 && cont.getMessage().getMessage().getMentions().size() >= 1) {
            String timezone = SQLUtils.getTimezone(cont.getMessage().getMessage().getMentions().get(0).getID(), cont.getActions().getSQL(), cont.getActions());
            if (timezone != null) {
                Calendar time = Calendar.getInstance(TimeZone.getTimeZone(timezone));
                message.withContent(cont.getMessage().getMessage().getMentions().get(0).getDisplayName(cont.getMessage().getMessage().getGuild()) + "'s timezone: " + timezone + "\n" + BotUtils.getPrettyTime(time));
            } else {
                message.withContent("They have not set your timezone yet.");
            }
        } else {
            String timezone = SQLUtils.getTimezone(cont.getMessage().getMessage().getAuthor().getID(), cont.getActions().getSQL(), cont.getActions());
            if (timezone != null) {
                Calendar time = Calendar.getInstance(TimeZone.getTimeZone(timezone));
                message.withContent("Your timezone: " + timezone + "\n" + BotUtils.getPrettyTime(time));
            } else {
                message.withContent("You have not set your timezone yet. Use " + cont.getActions().getConfig().commChar + "time set GMT+(Your Timezone)");
            }
        }
        cont.getActions().sendMessage(message);
    }
}