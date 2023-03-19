package de.hatoka.oauth.capi.business;

/**
 * declares the usage of the token - so access or id token can't be used for refresh
 */
public enum TokenUsage
{
    access, refresh, id, none
};
