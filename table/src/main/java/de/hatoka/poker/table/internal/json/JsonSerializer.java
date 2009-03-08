package de.hatoka.poker.table.internal.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializer<T>
{
    private final Class<T> tClass;

    public JsonSerializer(Class<T> eventClass)
    {
        tClass = eventClass;
    }

    public String serialize(Object event)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(event);
        }
        catch(JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public T deserialize(String eventAsString)
    {
        if (eventAsString == null)
        {
            return null;
        }
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(eventAsString, tClass);
        }
        catch(JsonProcessingException e)
        {
            throw new IllegalStateException("Can't convert " + tClass.getSimpleName(), e);
        }
    }
}
