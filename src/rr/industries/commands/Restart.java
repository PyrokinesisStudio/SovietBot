package rr.industries.commands;

import rr.industries.util.BotActions;
import rr.industries.util.CommContext;
import rr.industries.util.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Created by Sam on 8/28/2016.
 */
public class Restart extends Command {

    public Restart() {
        permLevel = Permissions.BOTOPERATOR;
        commandName = "restart";
        helpText = "Restarts the bot.";
        deleteMessage = false;
    }

    @Override
    public void execute(CommContext cont) {

        if (!cont.getMessage().getMessage().getAuthor().getID().equals("141981833951838208")) {
            BotActions.sendMessage(new MessageBuilder(cont.getClient()).withContent("Communism marches on!").withChannel(cont.getMessage().getMessage().getChannel()));
            return;
        }
        if (!cont.getMessage().getMessage().getChannel().isPrivate()) {
            try {
                cont.getMessage().getMessage().delete();
            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                LOG.debug("Error while deleting restart command", ex);
            }
        }
        BotActions.terminate(true, cont.getClient());
    }
}
