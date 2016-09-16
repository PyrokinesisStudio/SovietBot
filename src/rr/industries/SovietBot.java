/*
Add This Bot to Your Server:
https://discordapp.com/oauth2/authorize?&client_id=184445488093724672&scope=bot&permissions=19950624
 */

 /* Permissions List
MANAGE_GUILD	0x00000020  Allows management and editing of the guild
READ_MESSAGES	0x00000400  Allows reading messages in a channel. The channel will not appear for users without this permission
SEND_MESSAGES	0x00000800  Allows for sending messages in a channel
EMBED_LINKS     0x00004000  Links sent by this user will be auto-embedded
CONNECT 	    0x00100000  Allows for joining of a voice channel
SPEAK           0x00200000  Allows for speaking in a voice channel
MOVE_MEMBERS	0x01000000  Allows for moving of members between voice channels
MANAGE_MESSAGES 0x00002000  Allows for deletion of other users messages

Total:          19950624
 */
package rr.industries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rr.industries.commands.*;
import rr.industries.util.GenHelpDocs;
import sx.blah.discord.util.DiscordException;

import java.util.Arrays;
import java.util.List;

public class SovietBot {

    public static final Logger LOG = LoggerFactory.getLogger(SovietBot.class);
    private static Instance bot;
    public static final ClassLoader resourceLoader = Instance.class.getClassLoader();
    public static final List<Command> commands;
    public static final Configuration defaultConfig = new Configuration("SovietBot", "person.jpeg", ">", "", "", 1000, new String[0]);

    static {
        commands = Arrays.asList(
                new Bring(), new Cat(), new Coin(), new Connect(),
                new Disconnect(), new Help(), new Info(), new Invite(), new Log(), new Music(),
                new Purge(), new Quote(), new Rekt(), new Restart(), new Roll(), new Stop(),
                new Unafk(), new Uptime(), new Weather(), new Prefix(), new Rip(),
                new Environment(), new Echo(), new Test(), new Perms(), new Time());
    }
    public static final String botName = "SovietBot";
    public static final String frameName = sx.blah.discord.Discord4J.NAME;
    public static final String frameVersion = sx.blah.discord.Discord4J.VERSION;
    public static final String helpCommand = "help";
    public static final String author = "robot_rover";
    public static final String invite = "https://discordapp.com/oauth2/authorize?&client_id=184445488093724672&scope=bot&permissions=87354385";
    public static final String website = "https://robot-rover.github.io/SovietBot/";

    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equals("generate")) {
            GenHelpDocs.generate(commands);
            return;
        }
        LOG.info("\n------------------------------------------------------------------------\n### {} ###\n------------------------------------------------------------------------", botName);
        try {
            bot = new Instance();
        } catch (DiscordException e) {
            LOG.warn("Bot could not start", e);
        }
    }

}