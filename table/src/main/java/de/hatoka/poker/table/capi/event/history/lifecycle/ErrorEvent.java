package de.hatoka.poker.table.capi.event.history.lifecycle;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.PublicGameEvent;

public class ErrorEvent implements DealerEvent, PublicGameEvent
{
    public static ErrorEvent valueOf(Exception e)
    {
        StringBuilder b = new StringBuilder(e.getMessage() == null ? "(no-message)" : e.getMessage());
        for(StackTraceElement se : e.getStackTrace())
        {
            if (se.getClassName().startsWith("de.hatoka.poker"))
            {
                b.append("\n\t").append(se.toString());
            }
        }
        ErrorEvent result = new ErrorEvent();
        result.setException(b.toString());
        return result;
    }
    @JsonProperty("exception")
    private String exception;

    public String getException()
    {
        return exception;
    }

    public void setException(String exception)
    {
        this.exception = exception;
    }
}
