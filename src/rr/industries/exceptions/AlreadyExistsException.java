package rr.industries.exceptions;

import rr.industries.util.BotUtils;
import rr.industries.util.Permissions;

import java.util.Optional;

/**
 * @author Sam
 */
public class AlreadyExistsException extends BotException {
    private Permissions neededPerm;

    /**
     * A(n) (type) named (name) already exists, and was not overwritten because (reason)
     */
    public AlreadyExistsException(String name, String type, String reason) {
        super("A" + BotUtils.startsWithVowel(type, "n ", " ", true) + " named `" + name + "` already exists, and was not overwritten because " + reason);
    }

    /**
     * A(n) (type) named (name) already exists, and was not overwritten because you are not a (neededPerm).formatted
     */
    public AlreadyExistsException(String name, String type, Permissions neededPerm) {
        super("A" + BotUtils.startsWithVowel(type, "n ", " ", true) + " named `" + name + "` already exists, and was not overwritten because you are not a " + neededPerm.formatted);
        this.neededPerm = neededPerm;
    }

    /**
     * A(n) (type) named (name) already exists
     */
    public AlreadyExistsException(String name, String type) {
        super("A" + BotUtils.startsWithVowel(type, "n ", " ", true) + " named `" + name + "` already exists");
    }

    /**
     * The perm needed to complete the action. May be null
     */
    public Permissions getNeededPerm() {
        return neededPerm;
    }

    @Override
    public Optional<String> criticalMessage() {
        return Optional.empty();
    }
}
