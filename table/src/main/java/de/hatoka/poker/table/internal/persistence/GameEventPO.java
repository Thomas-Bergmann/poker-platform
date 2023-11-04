package de.hatoka.poker.table.internal.persistence;

import java.io.Serializable;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "game_events", uniqueConstraints = { @UniqueConstraint(columnNames = { "table_id", "game_no", "event_no" }) })
public class GameEventPO implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Internal identifier for persistence only
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "game_event_id")
    private Long id;

    @NotNull
    @Column(name = "table_id", nullable = false)
    private Long tableid;

    @NotNull
    @Column(name = "game_no", nullable = false)
    private Long gameno;

    @NotNull
    @Column(name = "event_no", nullable = false)
    private Integer eventno;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_data", nullable = false, length = 4048)
    private String eventData;

    public GameEventPO()
    {
    }

    public Long getId()
    {
        return id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(gameno, tableid);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GameEventPO other = (GameEventPO)obj;
        return Objects.equals(gameno, other.gameno) && Objects.equals(tableid, other.tableid);
    }

    public Long getTableId()
    {
        return tableid;
    }

    public void setTableId(Long tableId)
    {
        this.tableid = tableId;
    }

    public Long getGameNo()
    {
        return gameno;
    }

    public void setGameNo(Long gameNo)
    {
        this.gameno = gameNo;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getEventData()
    {
        return eventData;
    }

    public void setEventData(String eventData)
    {
        if (eventData.length() > 4048)
        {
            throw new IllegalArgumentException("EventData is too long " + eventData.length());
        }
        this.eventData = eventData;
    }

    public Integer getEventNo()
    {
        return eventno;
    }

    public void setEventNo(Integer eventNo)
    {
        this.eventno = eventNo;
    }
}
