package de.hatoka.oauth.capi.business;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Component;

import de.hatoka.poker.remote.oauth.OAuthTokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import jakarta.xml.bind.DatatypeConverter;

@Component
public class TokenUtils
{
    private static final String TOKEN_TYPE_BEARER = "bearer";
    private static final String CLAIM_TOKEN_META = "token-metadata";
    // access token validity period in milliseconds (5min)
    private static final long JWT_ACCESS_TOKEN_VALIDITY = 5 * 60 * 1000;
    // refresh token validity period in milliseconds (5h)
    private static final long JWT_REFRESH_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    // extract claim usage from claims
    @SuppressWarnings("unchecked")
    private static final Function<Claims, TokenMetaData> CLAIM_METADATA_PROVIDER = (claims) -> {
        return TokenMetaData.valueOf((Map<String, Object>)claims.get(CLAIM_TOKEN_META));
    };
    /**
     * Algorithm used for key signature
     */
    private static final MacAlgorithm ALGORITHM = Jwts.SIG.HS256;
    /**
     * create a key and retrieve the algorithm
     */
    private static final String ALGORITHM_NAME = ALGORITHM.key().build().getAlgorithm();

    @Value("${jwt.secret}")
    private String secret;

    /**
     * @param subject
     * @return token response includes access, refresh and id token for given subject
     */
    public OAuthTokenResponse createTokenForSubject(String subject)
    {
        OAuthTokenResponse result = new OAuthTokenResponse();
        result.setTokenType(TOKEN_TYPE_BEARER);
        Map<String, Object> claims = Collections.emptyMap();
        long now = getNow();
        TokenMetaData meta = TokenMetaData.valueOf(TokenUsage.none, now, now);
        result.setIdToken(generateIdToken(meta, subject, claims));
        result.setAccessToken(generateAccessToken(meta, subject, claims));
        result.setRefreshToken(generateRefreshToken(meta, subject, claims));
        result.setExpiresIn(getExpiresIn(result.getAccessToken()));
        result.setRefreshExpiresIn(getExpiresIn(result.getRefreshToken()));
        result.setNotBeforePolicy(now);
        return result;
    }

    /**
     * @param refreshToken
     * @return a new token response with given refresh token
     */
    public OAuthTokenResponse createTokenFromRefreshToken(String refreshToken)
    {
        if (isTokenExpired(refreshToken))
        {
            OAuth2Error oauth2Error = new OAuth2Error("invalid_token", "The Refresh Token expired", null);
            throw new OAuth2AuthenticationException(oauth2Error);
        }
        TokenMetaData meta = getClaimFromToken(refreshToken, CLAIM_METADATA_PROVIDER);
        if (TokenUsage.refresh != meta.getUsage())
        {
            OAuth2Error oauth2Error = new OAuth2Error("invalid_token", "The token is not a refresh token", null);
            throw new OAuth2AuthenticationException(oauth2Error);
        }
        String subject = getSubjectFromToken(refreshToken);
        OAuthTokenResponse result = new OAuthTokenResponse();
        result.setTokenType(TOKEN_TYPE_BEARER);
        Map<String, Object> claims = Collections.emptyMap();
        result.setIdToken(generateIdToken(meta, subject, claims));
        result.setAccessToken(generateAccessToken(meta, subject, claims));
        result.setRefreshToken(generateRefreshToken(meta, subject, claims));
        result.setExpiresIn(getExpiresIn(result.getAccessToken()));
        result.setRefreshExpiresIn(getExpiresIn(result.getRefreshToken()));
        return result;
    }

    /**
     * Generates an identity token (can be used to get information about player or bot
     * 
     * @param meta
     * @param subject
     * @param claims
     * @return jwt token
     */
    private String generateIdToken(TokenMetaData meta, String subject, Map<String, Object> claims)
    {
        return generateToken(meta.setUsage(TokenUsage.id), subject, claims, JWT_REFRESH_TOKEN_VALIDITY);
    }

    /**
     * Generates an access token (can be used to get access to non authorization resources
     * 
     * @param meta
     * @param subject
     * @param claims
     * @return jwt token
     */
    private String generateAccessToken(TokenMetaData meta, String subject, Map<String, Object> claims)
    {
        return generateToken(meta.setUsage(TokenUsage.access), subject, claims, JWT_ACCESS_TOKEN_VALIDITY);
    }

    /**
     * Generates a refresh token (can be used to generate a new access token, without transferring credentials again)
     * 
     * @param subject
     * @param claims
     * @return jwt token
     */
    private String generateRefreshToken(TokenMetaData meta, String subject, Map<String, Object> claims)
    {
        return generateToken(meta.setUsage(TokenUsage.refresh), subject, claims, JWT_REFRESH_TOKEN_VALIDITY);
    }

    /**
     * @param token
     * @return true if token is not expired
     */
    public boolean isTokenValid(String token)
    {
        return !isTokenExpired(token);
    }

    /**
     * @param token
     * @param usage
     * @return true if token is not expired and define usage is correct
     */
    public boolean isTokenValid(String token, TokenUsage usage)
    {
        return isTokenValid(token) && usage == getClaimFromToken(token, CLAIM_METADATA_PROVIDER).getUsage();
    }

    public boolean isTokenValid(String token, TokenUsage usage, String subject, Map<String, Object> claims)
    {
        return isTokenValid(token, usage) && subject.equals(getSubjectFromToken(token));
    }

    /**
     * @param token jwt token
     * @return true if token has expired or checked before (JWT token must be signed with secret)
     */
    private boolean isTokenExpired(String token)
    {
        try
        {
            getClaimFromToken(token, Claims::getExpiration);
        }
        catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }

    private long getNow()
    {
        return System.currentTimeMillis();
    }

    /**
     * @param token jwt token
     * @return subject from token (player or bot ref)
     */
    private TokenMetaData getMetaDataFromToken(String token)
    {
        return getClaimFromToken(token, CLAIM_METADATA_PROVIDER);
    }

    /**
     * @param token jwt token
     * @return subject from token (player or bot ref)
     */
    private String getSubjectFromToken(String token)
    {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * retrieving any information from token we will need the secret key
     * @param token
     * @return claims
     * @throws ExpiredJwtException
     */
    private Claims getAllClaimsFromToken(String token) throws ExpiredJwtException
    {
        Object payload = getJwtParser().parse(token).getPayload();
        if (payload instanceof Claims claims)
        {
            return claims;
        }
        throw new JwtException("no claims found");
    }

    private JwtParser getJwtParser()
    {
        return Jwts.parser().verifyWith(getKey()).build();
    }

    /**
     * Provides the secret key, which is configured at application.
     * <br>Create a secret with
     * <code>private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();</code>
     * @return configured secret key
     */
    private SecretKey getKey()
    {
        byte[] secretBytes = DatatypeConverter.parseBase64Binary(secret);
        return new SecretKeySpec(secretBytes, ALGORITHM_NAME);
    }

    private SecureDigestAlgorithm<SecretKey, SecretKey> getAlgoritm()
    {
        return ALGORITHM;
    }

    // while creating the token -
    // 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
    // 2. Sign the JWT using the HS512 algorithm and secret key.
    // 3. According to JWS Compact
    // Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    // compaction of the JWT to a URL-safe string
    private String generateToken(TokenMetaData meta, String subject, Map<String, Object> claims, long validity)
    {
        long now = getNow();
        Map<String, Object> withMetaClaims = new HashMap<>();
        withMetaClaims.putAll(claims);
        withMetaClaims.put(CLAIM_TOKEN_META, meta.setIssuedAt(now));
        return Jwts.builder()
                   .claims(withMetaClaims)
                   .subject(subject)
                   .notBefore(new Date(now))
                   .issuedAt(new Date(now))
                   .expiration(new Date(now + validity))
                   .signWith(getKey(), getAlgoritm())
                   .compact();
    }

    private Long getExpiresIn(String token)
    {
        return getClaimFromToken(token, Claims::getExpiration).getTime();
    }

    Long getAuthenticatedAt(String token)
    {
        return getMetaDataFromToken(token).getAuthenticatedAt();
    }
}