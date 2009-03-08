package de.hatoka.poker.table.internal.persistence;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "seats", uniqueConstraints = { @UniqueConstraint(columnNames = { "table_id", "position" }) })
public class SeatPO implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Internal identifier for persistence only
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "seat_id")
    private Long id;

    @NotNull
    @Column(name = "table_id", nullable = false)
    private Long tableid;

    @NotNull
    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "player_id", nullable = true)
    private Long playerid;

    @Column(name = "coins_on_seat", nullable = false)
    private int amountOfCoinsOnSeat = 0; // coins of player on seat

    @Column(name = "is_out", nullable = false)
    private boolean sittingOut = true; // not on table

    public SeatPO()
    {
    }

    public Long getId()
    {
        return id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getPosition(), getTableId());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SeatPO other = (SeatPO)obj;
        return Objects.equals(getPosition(), other.getPosition()) && Objects.equals(getTableId(), other.getTableId());
    }

    public Long getTableId()
    {
        return tableid;
    }

    public void setTableId(Long tableId)
    {
        this.tableid = tableId;
    }

    public Integer getPosition()
    {
        return position;
    }

    public void setPosition(Integer position)
    {
        this.position = position;
    }

    public Long getPlayerId()
    {
        return playerid;
    }

    public void setPlayerId(Long playerId)
    {
        this.playerid = playerId;
    }

    public int getAmountOfCoinsOnSeat()
    {
        return amountOfCoinsOnSeat;
    }

    public void setAmountOfCoinsOnSeat(int amountOfCoinsOnSeat)
    {
        this.amountOfCoinsOnSeat = amountOfCoinsOnSeat;
    }

    public boolean isSittingOut()
    {
        return sittingOut;
    }

    public void setSittingOut(boolean sittingOut)
    {
        this.sittingOut = sittingOut;
    }
}
