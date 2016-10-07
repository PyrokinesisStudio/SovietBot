package rr.industries.commands;

import org.apache.commons.io.IOUtils;
import org.bitpipeline.lib.owm.OwmClient;
import org.bitpipeline.lib.owm.StatusWeatherData;
import org.bitpipeline.lib.owm.WeatherStatusResponse;
import org.json.JSONException;
import rr.industries.Instance;
import rr.industries.exceptions.BotException;
import rr.industries.geoCoding.AddressComponent;
import rr.industries.geoCoding.GeoCoding;
import rr.industries.geoCoding.Result;
import rr.industries.util.*;
import sx.blah.discord.util.MessageBuilder;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@CommandInfo(
        commandName = "weather",
        helpText = "Interface for getting the weather in your area."
)
public class Weather implements Command {
    private OwmClient map;
    //link - https://github.com/migtavares/owmClient
    //todo: implement forcasting
    @SubCommand(name = "forecast", Syntax = {@Syntax(helpText = "Displays forecast for your area. Give your location any way you like.", args = {Arguments.LOCATION})})
    public void forecast(CommContext cont) {
        OwmClient map = new OwmClient();
        map.setAPPID(cont.getActions().getConfig().owmKey);
        List<String> googleQuery = cont.getArgs();
        googleQuery.remove(0);
        MessageBuilder message = new MessageBuilder(cont.getClient()).withChannel(cont.getMessage().getChannel());
        cont.getActions().channels().sendMessage(message.withContent("Coming Soon"));
    }

    @SubCommand(name = "", Syntax = {
            @Syntax(helpText = "Displays the current weather in your area. Give your location any way you like.", args = {Arguments.LOCATION})
    })
    public void execute(CommContext cont) throws BotException {
        OwmClient map = new OwmClient();
        map.setAPPID(cont.getActions().getConfig().owmKey);
        List<String> googleQuery = cont.getArgs();
        googleQuery.remove(0);
        MessageBuilder message = new MessageBuilder(cont.getClient()).withChannel(cont.getMessage().getChannel());
        try {
            Result location = queryGoogle(googleQuery);
            WeatherStatusResponse weather = map.currentWeatherAroundPoint((float) location.geometry.location.lat, (float) location.geometry.location.lng, 1);
            Optional<AddressComponent> place = location.addressComponents.stream().filter((v) -> v.types.contains("locality")).findFirst();
            if (place.isPresent())
                message.appendContent("Weather in **" + place.get().shortName + "**\n");
            if (weather.hasWeatherStatus() && weather.getWeatherStatus().size() > 0) {
                StatusWeatherData set = weather.getWeatherStatus().get(0);
                message.appendContent("Station: " + set.getName() + "\n");
                String deg = "\u00B0F";
                message.appendContent(Math.round(set.getMain().getTemp()) + deg + " (" + Math.round(set.getMain().getTempMax()) + deg + "/" + Math.round(set.getMain().getTempMin()) + deg + ")\n");
                message.appendContent(Math.round(set.getHumidity()) + "% Humidity\n");
                if (set.hasRain() && set.getRain() != Integer.MIN_VALUE) {
                    message.appendContent("Rain: " + Integer.toString(set.getRain()) + "mm of rain in last 3h\n");
                }
                if (set.hasWind()) {
                    message.appendContent("Wind: " + Integer.toString(Math.round(set.getWindSpeed())) + "mph at " + set.getWindDeg() + "\u00B0\n");
                }
            } else {
                message.appendContent("No weather data available for this location...");
            }
        } catch (IOException ex) {
            throw new InternalError("IOException connecting to OWM Servers", ex);
        } catch (NoSuchElementException ex) {
            message.appendContent("No weather data available for this location...");
        } catch (JSONException ex) {
            throw new InternalError("Malformed JSON on weather command", ex);
        }
        cont.getActions().channels().sendMessage(message);

    }


    @Override
    public Predicate<List<String>> getValiddityOverride() {
        return (v) -> (v.size() >= 2 && !v.get(1).equals("forecast") || v.size() >= 3);
    }

    private Result queryGoogle(List<String> args) throws NoSuchElementException, IOException {
        URL url;
        url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + args.stream().collect(Collectors.joining(", ")).replace(" ", "%20"));
        URLConnection con = url.openConnection();
        GeoCoding response = Instance.gson.fromJson(IOUtils.toString(con.getInputStream()), GeoCoding.class);
        if (response.results.size() == 0) {
            throw new NoSuchElementException("Location not found");
        }
        if (!response.status.equals("OK"))
            throw new IOException("Google returned the status: " + response.status);
        return response.results.get(0);
    }
}
