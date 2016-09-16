package rr.industries;

public class Configuration {
    public String botName;
    public String botAvatar;
    public String commChar;
    public String token;
    public String secret;
    public int webhooksPort;
    public String[] operators;

    public Configuration(String botName, String botAvatar, String commChar, String token, String secret, int webhooksPort, String[] operators) {
        this.botName = botName;
        this.botAvatar = botAvatar;
        this.commChar = commChar;
        this.token = token;
        this.secret = secret;
        this.webhooksPort = webhooksPort;
        this.operators = (operators == null ? new String[0] : operators);
    }
}