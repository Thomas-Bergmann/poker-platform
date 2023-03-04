package de.hatoka.poker.player.capi.remote;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BotInfoRO
{
    @JsonProperty("balance")
    @NotNull
    private Integer balance;

    public Integer getBalance()
    {
        return balance;
    }

    public void setBalance(Integer balance)
    {
        this.balance = balance;
    }
}
