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
    @JsonProperty("coins-inplay")
    private int inPlay;

    @NotNull
    @JsonProperty("out")
    private boolean out = false;

    @NotNull
    @JsonProperty("button")
    private boolean onButton = false;

    @NotNull
    @JsonProperty("allin")
    private boolean allIn = false;

    @NotNull
    @JsonProperty("has-action")
    private boolean hasAction = false;

    @NotNull
    @JsonProperty("cards-hole")
    private String holeCards;

    @NotNull
    @JsonProperty("rank")
    private String rank;

    @JsonProperty("botRef")
    private String botRef;

    @JsonProperty("userRef")
    private String userRef;

    public int getInPlay()
    {
        return inPlay;
    }

    public void setInPlay(int inPlay)
    {
        this.inPlay = inPlay;
    }

    public boolean isAllIn()
    {
        return allIn;
    }

    public void setAllIn(boolean allIn)
    {
        this.allIn = allIn;
    }

    public String getHoleCards()
    {
        return holeCards;
    }

    public void setHoleCards(String holeCards)
    {
        this.holeCards = holeCards;
    }

    public String getRank()
    {
        return rank;
    }

    public void setRank(String rank)
    {
        this.rank = rank;
    }

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

    public boolean isHasAction()
    {
        return hasAction;
    }

    public void setHasAction(boolean hasAction)
    {
        this.hasAction = hasAction;
    }

    public boolean isOnButton()
    {
        return onButton;
    }

    public void setOnButton(boolean onButton)
    {
        this.onButton = onButton;
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
