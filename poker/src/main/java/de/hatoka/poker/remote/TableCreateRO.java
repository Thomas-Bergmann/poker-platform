package de.hatoka.poker.remote;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TableCreateRO
{
    @JsonProperty("variant")
    @NotNull
    private String variant;

    @JsonProperty("limit")
    @NotNull
    private String limit;

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
}
