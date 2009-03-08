package de.hatoka.poker.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameEventRO
{
    public enum Action
    {
        start, pot, blind, fold, check, call, bet, raise, cards, showdown, transfer
    }

    @JsonProperty("action")
    private Action action;
    @JsonProperty("info")
    private String info;

    public Action getAction()
    {
        return action;
    }

    public void setAction(Action action)
    {
        this.action = action;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }
}
