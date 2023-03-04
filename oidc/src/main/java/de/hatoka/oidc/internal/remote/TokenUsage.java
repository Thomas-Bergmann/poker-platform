package de.hatoka.oidc.internal.remote;

/**
 * declares the usage of the token - so access or id token can't be used for refresh
 */
enum TokenUsage
{
    access, refresh, id, none
};
