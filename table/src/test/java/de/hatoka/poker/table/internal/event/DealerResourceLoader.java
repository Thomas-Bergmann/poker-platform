package de.hatoka.poker.table.internal.event;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import de.hatoka.poker.table.capi.business.DealerGameCommunication;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.seat.PlayerEvent;
import de.hatoka.poker.table.internal.json.GameEventType;

public class DealerResourceLoader
{
    private static final Charset CHARSET = Charset.forName("utf-8");

    private class LocaleGame implements DealerGameCommunication
    {
        List<GameEvent> events = new ArrayList<>();
        public LocaleGame(List<GameEvent> events)
        {
            this.events.addAll(events);
        }

        @Override
        public void publishEvent(DealerEvent event)
        {
            LoggerFactory.getLogger(getClass()).debug("DealerEvent {}:{}.", event.getClass().getSimpleName(), GameEventType.valueOf(event.getClass()).serialize(event));
            this.events.add(event);
        }

        @Override
        public void publishEvent(PlayerEvent event)
        {
            LoggerFactory.getLogger(getClass()).debug("PlayerEvent {}:{}.", event.getClass().getSimpleName(), GameEventType.valueOf(event.getClass()).serialize(event));
            this.events.add(event);
        }

        @Override
        public List<GameEvent> getAllEvents()
        {
            return events;
        }
    }
    
    public GameInfo load(URI resource, int numberOfEvents) throws IOException, URISyntaxException
    {
        return getDealerGameInfo(loadFromCSV(resource).subList(0, numberOfEvents));
    }
    
    public GameInfo load(URI resource) throws IOException, URISyntaxException
    {
        return getDealerGameInfo(loadFromCSV(resource));
    }

    private GameInfo getDealerGameInfo(List<GameEvent> events)
    {
        DealerGameCommunication game = new LocaleGame(events);
        return new GameInfo(game);
    }

    private List<GameEvent> loadFromCSV(URI resource) throws IOException, URISyntaxException
    {
        List<String> lines = Files.readAllLines(Paths.get(resource), CHARSET);
        return lines.subList(1, lines.size()).stream().map(this::decode).toList();
    }

    private GameEvent decode(String line)
    {
        int comma = line.indexOf(",");
        try
        {
            return GameEventType.valueOf(line.substring(0, comma)).deserialize(unquote(line.substring(comma + 1)));
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Can't parse line. " + line, e);
        }
    }

    private String unquote(String event)
    {
        return event.substring(1, event.length() - 1).replaceAll("\"\"", "\"");
    }

}
