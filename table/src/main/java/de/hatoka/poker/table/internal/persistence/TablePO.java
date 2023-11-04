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
@Table(name = "tables", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class TablePO implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Internal identifier for persistence only
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "table_id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "poker_limit", nullable = false)
    private String limit;

    @NotNull
    @Column(name = "poker_variant", nullable = false)
    private String variant;

    @NotNull
    @Column(name = "buyin_max", nullable = false)
    private Integer maxBuyIn = 500;

    /**
     * current game (last given number)
     */
    @NotNull
    @Column(name = "last_gameno", nullable = false)
    private Long lastGameNo = 0L;

    @NotNull
    @Column(name = "blind_small", nullable = false)
    private Integer smallBlind = 5;
    @NotNull
    @Column(name = "blind_big", nullable = false)
    private Integer bigBlind = 10;

    public TablePO()
    {
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TablePO other = (TablePO)obj;
        return Objects.equals(name, other.name);
    }

    public String getLimit()
    {
        return limit;
    }

    public void setLimit(String limit)
    {
        this.limit = limit;
    }

    public String getVariant()
    {
        return variant;
    }

    public void setVariant(String variant)
    {
        this.variant = variant;
    }

    public Integer getMaxBuyIn()
    {
        return maxBuyIn;
    }

    public void setMaxBuyIn(Integer maxBuyIn)
    {
        this.maxBuyIn = maxBuyIn;
    }

    public Integer getSmallBlind()
    {
        return smallBlind;
    }

    public void setSmallBlind(Integer smallBlind)
    {
        this.smallBlind = smallBlind;
    }

    public Integer getBigBlind()
    {
        return bigBlind;
    }

    public void setBigBlind(Integer bigBlind)
    {
        this.bigBlind = bigBlind;
    }
    public Long getLastGameNo()
    {
        return lastGameNo;
    }

    public void setLastGameNo(Long lastGameNo)
    {
        this.lastGameNo = lastGameNo;
    }
}
