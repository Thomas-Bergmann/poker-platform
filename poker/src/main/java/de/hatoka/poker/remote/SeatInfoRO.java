package de.hatoka.poker.remote;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SeatInfoRO
{
    /**
     * Name of player
     */
    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("position")
    private Integer position;

    @NotNull
    @JsonProperty("tableResourceURI")
    private String tableResourceURI;

    @NotNull
    @JsonProperty("out")
    private boolean out = false;

    @JsonProperty("botRef")
    private String botRef;

    @JsonProperty("userRef")
    private String userRef;

    public Integer getPosition()
    {
        return position;
    }

    public void setPosition(Integer position)
    {
        this.position = position;
    }

    public String getTableResourceURI()
    {
        return tableResourceURI;
    }

    public void setTableResourceURI(String tableResourceURI)
    {
        this.tableResourceURI = tableResourceURI;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isOut()
    {
        return out;
    }

    public void setOut(boolean out)
    {
        this.out = out;
    }

    public String getBotRef()
    {
        return botRef;
    }

    public void setBotRef(String botRef)
    {
        this.botRef = botRef;
    }

    public String getUserRef()
    {
        return userRef;
    }

    public void setUserRef(String userRef)
    {
        this.userRef = userRef;
    }
}
