package de.hatoka.poker.player.capi.business;

import java.util.Objects;

import de.hatoka.user.capi.business.UserRef;

public class PlayerRef
{
    private static final String PLAYER_REF_PREFIX = "player:";
    private static final String BOT_REF_PREFIX = "bot:";

    private final boolean isBot;
    private final UserRef userRef;
    private final String name;

    public static PlayerRef globalRef(String globalRef)
    {
        if (globalRef.startsWith(PLAYER_REF_PREFIX))
        {
            String[] find = globalRef.split(":");
            return humanRef(UserRef.localRef(find[1]));
        }
        if (globalRef.startsWith(BOT_REF_PREFIX))
        {
            String[] findPrefix = globalRef.split(":");
            String[] splitAt = findPrefix[1].split("@");
            return botRef(splitAt[1], splitAt[0]);
        }
        throw new IllegalArgumentException("ref '" + globalRef + "' is not a player");
    }

    public static PlayerRef botRef(String userLocalRef, String bot)
    {
        return botRef(UserRef.localRef(userLocalRef), bot);
    }

    public static PlayerRef botRef(UserRef userRef, String bot)
    {
        return new PlayerRef(true, userRef, bot);
    }

    public static PlayerRef humanRef(String userLocalRef)
    {
        return humanRef(UserRef.localRef(userLocalRef));
    }

    public static PlayerRef humanRef(UserRef userRef)
    {
        return new PlayerRef(false, userRef, "");
    }

    private PlayerRef(boolean isBot, UserRef userRef, String name)
    {
        this.isBot = isBot;
        this.userRef = userRef;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return getGlobalRef();
    }

    public String getLocalRef()
    {
        return isBot ? name : userRef.getLocalRef();
    }

    public boolean isHuman()
    {
        return !isBot;
    }

    /**
     * @return user ref of player or owner of bot
     */
    public UserRef getUserRef()
    {
        return userRef;
    }

    public String getGlobalRef()
    {
        return isBot ? getBotGlobalRef() : getPlayerGlobalRef();
    }

    private String getPlayerGlobalRef()
    {
        return PLAYER_REF_PREFIX + userRef.getLocalRef();
    }

    private String getBotGlobalRef()
    {
        return BOT_REF_PREFIX + name + "@" + userRef.getLocalRef();
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(isBot, name, userRef);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PlayerRef other = (PlayerRef)obj;
        return isBot == other.isBot && Objects.equals(name, other.name) && Objects.equals(userRef, other.userRef);
    }
}
