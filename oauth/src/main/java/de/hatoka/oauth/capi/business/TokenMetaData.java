package de.hatoka.oauth.capi.business;

import java.util.Map;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains meta data of token (immutable - manipulation methods return a new object)
 * <li>usage to minimize usage scope
 * <li>issued to generate a new token on every call (otherwise the token will be the same if the same second)
 * <li>authenticated time of real authentication (without refresh)
 */
class TokenMetaData
{
    private static final String META_AUTHED = "authenticated";
    private static final String META_ISSUED = "issued";
    private static final String META_USAGE = "usage";

    public static TokenMetaData valueOf(Map<String, Object> metaDataClaim)
    {
        return TokenMetaData.valueOf(TokenUsage.valueOf((String)metaDataClaim.get(META_USAGE)),
                        (Long)metaDataClaim.get(META_ISSUED), (Long)metaDataClaim.get(META_AUTHED));
    }

    public static TokenMetaData valueOf(TokenUsage usage, Long issuedAt, Long authenticatedAt)
    {
        return new TokenMetaData(usage, issuedAt, authenticatedAt);
    }

    private TokenMetaData(TokenUsage usage, Long issuedAt, Long authenticatedAt)
    {
        this.usage = usage;
        this.issuedAt = issuedAt;
        this.authenticatedAt = authenticatedAt;
    }

    @JsonProperty(META_USAGE)
    @NotNull
    private final TokenUsage usage;
    @JsonProperty(META_ISSUED)
    @NotNull
    private final Long issuedAt;
    @JsonProperty(META_AUTHED)
    @NotNull
    private final Long authenticatedAt;


    public TokenUsage getUsage()
    {
        return usage;
    }

    public Long getIssuedAt()
    {
        return issuedAt;
    }

    public Long getAuthenticatedAt()
    {
        return authenticatedAt;
    }

    public TokenMetaData setIssuedAt(long issuedAt)
    {
        return TokenMetaData.valueOf(this.usage, issuedAt, this.authenticatedAt);
    }

    public TokenMetaData setUsage(TokenUsage usage)
    {
        return TokenMetaData.valueOf(usage, this.issuedAt, this.authenticatedAt);
    }
}
