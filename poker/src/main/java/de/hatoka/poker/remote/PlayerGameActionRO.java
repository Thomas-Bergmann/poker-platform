package de.hatoka.poker.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerGameActionRO
{
    public static PlayerGameActionRO valueOf(Action action)
    {
        return valueOf(action, null);
    }

    public static PlayerGameActionRO valueOf(Action action, Integer betTo)
    {
        PlayerGameActionRO result = new PlayerGameActionRO();
        result.setAction(action);
        result.setBetTo(betTo);
        return result;
    }

    public enum Action
    {
        fold, check, call, bet, raise, allin
    };

    @JsonProperty("action")
    private Action action;
    @JsonProperty("bet-to")
    private Integer betTo;
    
    public Action getAction()
    {
        return action;
    }

    public void setAction(Action action)
    {
        this.action = action;
    }

    public Integer getBetTo()
    {
        return betTo;
    }

    public void setBetTo(Integer betTo)
    {
        this.betTo = betTo;
    }
}
