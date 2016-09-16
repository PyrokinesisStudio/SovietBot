package rr.industries.commands;

import rr.industries.CommandList;
import rr.industries.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static sx.blah.discord.util.audio.AudioPlayer.getAudioPlayerForGuild;

@CommandInfo(
        commandName = "rekt",
        helpText = "Plays a slightly obnoxious sound in voice chat.",
        permLevel = Permissions.REGULAR
)
public class Rekt implements Command {
    static {
        CommandList.defaultCommandList.add(Rekt.class);
    }
    private AudioInputStream[] sfx;

    /*this.sfxIndex = new String[6];
    sfxIndex[0] = "wombo";
    sfxIndex[1] = "wrong";
    sfxIndex[2] = "airhorn";
    sfxIndex[3] = "never";
    sfxIndex[4] = "scope";
    sfxIndex[5] = "nope";*/

    public Rekt() {
        try {
            sfx = new AudioInputStream[]{
                    getAudioInputStream(resourceLoader.getResource("ohs/womboCombo.mp3")),
                    getAudioInputStream(resourceLoader.getResource("ohs/wrongNumber.mp3")),
                    getAudioInputStream(resourceLoader.getResource("ohs/violinAirhorn.mp3")),
                    getAudioInputStream(resourceLoader.getResource("ohs/noOneHasEver.mp3")),
                    getAudioInputStream(resourceLoader.getResource("ohs/noscoped.mp3")),
                    getAudioInputStream(resourceLoader.getResource("ohs/nopeSong.mp3"))
            };
        } catch (IOException | UnsupportedAudioFileException ex) {
            LOG.warn("Error initializing audio streams", ex);
        }
    }

    @SubCommand(name = "", Syntax = {
            @Syntax(helpText = "Plays a random rekt clip", args = {}),
            @Syntax(helpText = "Plays the specified rekt clip", args = {Arguments.TEXT}, options = {"wombo", "wrong", "airhorn", "never", "scope", "nope"})
    })
    public void execute(CommContext cont) {
        int i = 0;
        AudioInputStream[] sources = sfx;
        /*if (cont.getArgs().size() > 1) {
            for (String command : sfxIndex) {
                if (command.equalsIgnoreCase(cont.getArgs().get(1))) {
                    sources = new AudioInputStream[1];
                    sources[0] = sfx[i];
                }
                i++;
            }
        }*/

        try {
            getAudioPlayerForGuild(cont.getMessage().getMessage().getGuild()).queue(sources[rn.nextInt(sources.length)]);
        } catch (IOException ex) {
            cont.getActions().customException("Rekt", ex.getMessage(), ex, LOG, true);
        }
    }
}