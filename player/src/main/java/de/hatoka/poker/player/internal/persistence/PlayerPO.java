package de.hatoka.poker.player.internal.persistence;

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
@Table(name = "players", uniqueConstraints = { @UniqueConstraint(columnNames = { "name_nick" }) })
public class PlayerPO implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Internal identifier for persistence only
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "player_id")
    private Long id;

    @NotNull
    @Column(name = "name_nick", nullable = false)
    private String nickname;

    @Column(name = "balance", nullable = false)
    private int balance = 0;

    @Column(name = "type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "owner_ref", nullable = false)
    private String userref;

    public PlayerPO()
    {
    }

    public Long getId()
    {
        return id;
    }

    public String getNickName()
    {
        return nickname;
    }

    public void setNickName(String name)
    {
        this.nickname = name;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(nickname);
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
        PlayerPO other = (PlayerPO)obj;
        return Objects.equals(nickname, other.nickname);
    }

    public int getBalance()
    {
        return balance;
    }

    public void setBalance(int balance)
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

    public String getUserRef()
    {
        return userref;
    }

    public void setUserRef(String userRef)
    {
        this.userref = userRef;
    }
}
