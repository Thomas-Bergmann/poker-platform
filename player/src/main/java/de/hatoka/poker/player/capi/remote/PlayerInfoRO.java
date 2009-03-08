package de.hatoka.poker.player.capi.remote;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerInfoRO
{
    @JsonProperty("nick-name")
    @NotNull
    private String nickName;

    @JsonProperty("balance")
    @NotNull
    private Integer balance;

    @JsonProperty("type")
    @NotNull
    private String type;

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public Integer getBalance()
    {
        return balance;
    }

    public void setBalance(Integer balance)
    {
        this.balance = balance;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
