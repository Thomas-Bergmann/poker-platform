package de.hatoka.poker.table.capi.event;

import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.internal.json.GameEventType;

public class IllegalGameEventException extends IllegalArgumentException
{
    private static final long serialVersionUID = -7242408088884043613L;
    private final GameEvent event;

    public IllegalGameEventException(String message, GameEvent event)
    {
        super(message);
        this.event = event;
    }

    public GameEvent getEvent()
    {
        return event;
    }
    
    @Override
    public String getMessage()
    {
        String message = super.getMessage();
        return message + ":" + GameEventType.serializeEvent(event);
    }

}
