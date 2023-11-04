package de.hatoka.poker.remote;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SeatGameInfoRO
{
    @NotNull
    @JsonProperty("coins-inplay")
    private int inPlay;

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

    public int getInPlay()
    {
        return inPlay;
    }

    public void setInPlay(int inPlay)
    {
        this.inPlay = inPlay;
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

    public boolean isAllIn()
    {
        return allIn;
    }

    public void setAllIn(boolean allIn)
    {
        this.allIn = allIn;
    }
}
