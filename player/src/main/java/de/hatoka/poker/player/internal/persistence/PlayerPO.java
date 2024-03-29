package de.hatoka.poker.player.internal.persistence;

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

    @NotNull
    @Column(name = "balance", nullable = false)
    private int balance = 0;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "owner_ref", nullable = false)
    private String userref;

    @Column(name = "api_key", nullable = true)
    private String apiKey;

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

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }
}
