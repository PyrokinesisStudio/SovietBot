package rr.industries.util.sql;

import rr.industries.util.BotUtils;
import rr.industries.util.Entry;
import rr.industries.util.Permissions;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sam
 * @project sovietBot
 * @created 9/17/2016
 */
public class PermTable extends Table {
    public PermTable(Statement executor) {
        super("perms", executor,
                new Column("guildid", "text", false),
                new Column("userid", "text", false),
                new Column("perm", "int", false)
        );
        this.createIndex("permsindex", "guildid, userid", true);

    }

    public Permissions getPerms(IUser user, IGuild guild) {
        try {
            return BotUtils.toPerms(queryValue("perm", "userid='" + user.getID() + "' AND guildid='" + guild.getID() + "'").getInt("perm"));
        } catch (SQLException ex) {
            LOG.error("SQL Error", ex);
            return Permissions.NORMAL;
        }
    }

    public void setPerms(IUser user, IGuild guild, Permissions permissions) {
        if (permissions == Permissions.NORMAL)
            removeEntry("userid='" + user.getID() + "' and guildid='" + guild.getID() + "'");
        else
            setValue("userid='" + user.getID() + "' AND guildid='" + guild.getID() + "'", guild.getID(), user.getID(), Integer.toString(permissions.level));
    }

    public List<Entry<String, Integer>> getAllPerms(IGuild guild) {
        try {
            List<Entry<String, Integer>> list = new ArrayList<>();
            ResultSet rs = executor.executeQuery("SELECT userid, perm FROM" + getName() + " where guildid=" + guild.getID() + " ORDER BY perm DESC;");
            while (rs.next()) {
                list.add(new Entry<String, Integer>(rs.getString("userid"), rs.getInt("perm")));
            }
            return list;
        } catch (SQLException ex) {
            LOG.warn("SQL Error", ex);
        }
        return new ArrayList<>();
    }
}
