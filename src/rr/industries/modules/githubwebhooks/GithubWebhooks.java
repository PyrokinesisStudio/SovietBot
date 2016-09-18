package rr.industries.modules.githubwebhooks;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rr.industries.modules.Module;
import rr.industries.util.BotActions;
import spark.Request;
import spark.Response;
import spark.Spark;
import sx.blah.discord.util.MessageBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Credit to Chrislo for the basis and the POJOs
 */
public class GithubWebhooks implements Module {

    private final List<ChannelSettings> channels = new ArrayList<>();
    private final Gson gson = new Gson();
    private final Mac mac;
    private boolean isEnabled;
    private final BotActions actions;
    private final Logger LOG = LoggerFactory.getLogger(GithubWebhooks.class);

    /**
     * Should be initalized in ReadyEvent
     */
    public GithubWebhooks(BotActions actions) {
        isEnabled = false;
        this.actions = actions;

        final SecretKeySpec keySpec = actions.getConfig().secret == null ? null : new SecretKeySpec(actions.getConfig().secret.getBytes(), "HmacSHA1");
        Mac tmpMac = null;
        if (actions.getConfig().secret != null) {
            try {
                tmpMac = Mac.getInstance("HmacSHA1");
                tmpMac.init(keySpec);
            } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
                LOG.warn("Error Getting Decryption Mac", ex);
            }
        }
        mac = tmpMac;
    }

    @Override
    public void enable() {
        Spark.port(actions.getConfig().webhooksPort);
        Spark.post("/github", (Request request, Response response) -> {
            if (!"application/json".equals(request.headers("Content-Type"))) {
                LOG.warn("Received Non-JSON POST");
                response.status(500);
                return "Content-Type must be application/json!";
            }

            if (actions.getConfig().secret != null && mac != null) {
                String signature = request.headers("X-Hub-Signature");

                byte[] digestBytes = mac.doFinal(request.body().getBytes());
                StringBuilder builder = new StringBuilder();
                for (byte b : digestBytes) {
                    builder.append(String.format("%02x", b));
                }

                String digest = "sha1=" + builder.toString();
                if (!signature.equals(digest)) {
                    LOG.warn("Signature did not match: got " + signature + ", digested " + digest);
                    response.status(500);
                    return "Signatures did not match!";
                }
            }

            String event = request.headers("X-Github-Event");

            if (event.equalsIgnoreCase("ping")) {
                Ping ping = gson.fromJson(request.body(), Ping.class);
                String pingMessage = "Ping from webhook " + ping.hook_id + " with zen " + ping.zen;
                LOG.info(pingMessage);
                actions.sendMessage(new MessageBuilder(actions.getClient()).withContent(pingMessage).withChannel(actions.getClient().getOrCreatePMChannel(actions.getClient().getUserByID("141981833951838208"))));
            }
            // 👌 OK
            response.status(200);
            return "\uD83D\uDC4C OK";

        });
        Spark.post("/command", (Request request, Response response) -> {
            Restart restart = gson.fromJson(request.body(), Restart.class);
            LOG.info("Command POST received - " + restart.command);
            if (restart.command.equals("restart") && restart.secret.equals(actions.getConfig().secret)) {
                LOG.info("Everything Looks good, Restarting...");
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        actions.terminate(true);
                    }
                };
                thread.start();
                response.status(200);
                return "\uD83D\uDC4C OK";
            }
            response.status(418);
            return "I'm a teapot";
        });

        LOG.info("Initialized webhooks on port " + actions.getConfig().webhooksPort);
        isEnabled = true;
    }

    @Override
    public void disable() {
        Spark.stop();
        isEnabled = false;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public GithubWebhooks addChannel(ChannelSettings cs) {
        channels.add(cs);

        return this;
    }

    private void sendMessageToChannels(String repo, String event, String content) {
        LOG.info("Sent a webhook message to channels for event " + event);
        actions.sendMessage(new MessageBuilder(actions.getClient()).withContent(content).withChannel(actions.getClient().getChannelByID("161155978199302144")));
    }

    public String getJsonFromUrl(String address) throws IOException {
        URL url;
        url = new URL(address);
        InputStream is;
        URLConnection con = url.openConnection();
        is = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuilder builder = new StringBuilder();
        while ((inputLine = br.readLine()) != null)
            builder.append(inputLine);
        br.close();
        return builder.toString();
    }

    private static class ChannelSettings {

        String id;

        /**
         * If empty, will not accept any events. Use * to indicate any event.
         */

        List<String> events = new ArrayList<>();
        /**
         * If empty, takes all repos. Syntax is name/repo.
         */

        List<String> branches = new ArrayList<>();

        public ChannelSettings(String id) {
            this.id = id;
        }

        public ChannelSettings addEvent(String event) {
            events.remove(event);
            events.add(event);

            return this;
        }

        public ChannelSettings removeEvent(String event) {
            events.remove(event);

            return this;
        }

        public ChannelSettings addRepo(String repo) {
            repo = repo.toLowerCase(Locale.ROOT);

            branches.remove(repo);
            branches.add(repo);

            return this;
        }

        public ChannelSettings removeRepo(String repo) {
            branches.remove(repo);

            return this;
        }
    }
}