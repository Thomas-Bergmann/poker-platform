package de.hatoka.poker.remote;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TableInfoRO
{
    @JsonProperty("name")
    @NotNull
    private String name;

    @JsonProperty("variant")
    @NotNull
    private String variant;

    @JsonProperty("limit")
    @NotNull
    private String limit;

    @JsonProperty("max-buyin")
    @NotNull
    private Integer maxBuyIn;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVariant()
    {
        return variant;
    }

    public void setVariant(String variant)
    {
        this.variant = variant;
    }

    public String getLimit()
    {
        return limit;
    }

    public void setLimit(String limit)
    {
        this.limit = limit;
    }

    public Integer getMaxBuyIn()
    {
        return maxBuyIn;
    }

    public void setMaxBuyIn(Integer maxBuyIn)
    {
        this.maxBuyIn = maxBuyIn;
    }
}
