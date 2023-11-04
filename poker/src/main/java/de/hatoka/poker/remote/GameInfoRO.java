package de.hatoka.poker.remote;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameInfoRO
{
    @NotNull
    @JsonProperty("tableResourceURI")
    private String tableResourceURI;

    @NotNull
    @JsonProperty("seats")
    private List<SeatRO> seats;

    @NotNull
    @JsonProperty("cards-board")
    private String boardCards;

    @NotNull
    @JsonProperty("potsize")
    private Integer potSize;

    @NotNull
    @JsonProperty("blind-big")
    private Integer bigBlind;

    @NotNull
    @JsonProperty("events")
    private List<GameEventRO> events;

    public String getTableResourceURI()
    {
        return tableResourceURI;
    }

    public void setTableResourceURI(String tableResourceURI)
    {
        this.tableResourceURI = tableResourceURI;
    }

    public List<SeatRO> getSeats()
    {
        return seats;
    }

    public void setSeats(List<SeatRO> seats)
    {
        this.seats = seats;
    }

    public List<GameEventRO> getEvents()
    {
        return events;
    }

    public void setEvents(List<GameEventRO> events)
    {
        this.events = events;
    }

    public String getBoardCards()
    {
        return boardCards;
    }

    public void setBoardCards(String boardCards)
    {
        this.boardCards = boardCards;
    }

    public Integer getPotSize()
    {
        return potSize;
    }

    public void setPotSize(Integer potSize)
    {
        this.potSize = potSize;
    }

    public Integer getBigBlind()
    {
        return bigBlind;
    }

    public void setBigBlind(Integer bigBlind)
    {
        this.bigBlind = bigBlind;
    }
}
