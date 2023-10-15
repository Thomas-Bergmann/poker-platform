import { Action, createReducer, on } from '@ngrx/store';
import { OIDCProvider } from './oidc-models';
import { addProviders, defineProvider, defineUser, setAccessToken, addResources, rememberRouteBeforeLogin, setRefreshToken, clearAccessToken } from './oidc-actions';

export const oidcFeatureKey = 'oidcState';
export interface Token {
  token: string | undefined; // token content
  expiresIn: number | undefined // token expire time
}

export interface OIDCState {
  currentOIDCProvider: string; // local ref
  currentUser: string; // global ref
  accessToken: Token | undefined; // token to make requests
  refreshToken: Token | undefined; // token to get new access token
  resources: Set<string>;
  allOIDCProviders : OIDCProvider[];
  route: string | undefined;
}

const initialState : OIDCState = {
  currentOIDCProvider : sessionStorage.getItem("hatoka_oidc_provider") || '',
  currentUser : sessionStorage.getItem("hatoka_oidc_user") || '',
  accessToken : undefined,
  refreshToken : undefined,
  resources : new Set<string>(),
  allOIDCProviders : [],
  route : sessionStorage.getItem("hatoka_oidc_route") || undefined
}

const _oidcReducer = createReducer(
  initialState,
  on(addProviders, (state, action) => _addProviders(state, action.providers)),
  on(defineProvider, (state, action) => _defineProvider(state, action.provider)),
  on(defineUser, (state, action) => _defineUser(state, action.user)),
  on(setAccessToken, (state, action) => _setAccessToken(state, action.token, action.expires_in)),
  on(clearAccessToken, (state) => _clearAccessToken(state)),
  on(setRefreshToken, (state, action) => _setRefreshToken(state, action.token, action.expires_in)),
  on(addResources, (state, action) => _addResource(state, action.resources)),
  on(rememberRouteBeforeLogin, (state, action) => _rememberRouteBeforeLogin(state, action.url)),
);

export function oidcReducer(state: OIDCState | undefined, action: Action) {
  return _oidcReducer(state, action);
}

function _addProviders(state:OIDCState, providers:OIDCProvider[]):OIDCState
{
  return ({
    ...state,
    allOIDCProviders: providers
  });
}

function _defineProvider(state:OIDCState, provider:OIDCProvider):OIDCState
{
  sessionStorage.setItem("hatoka_oidc_provider", provider.localRef)
  return ({
    ...state,
    currentOIDCProvider: provider.localRef
  });
}

function _defineUser(state:OIDCState, user:string):OIDCState
{
  sessionStorage.setItem("hatoka_oidc_user", user)
  return ({
    ...state,
    currentUser: user
  });
}

function _setAccessToken(state:OIDCState, token:string, expires_in: number):OIDCState
{
  return ({
    ...state,
    accessToken: {token: token, expiresIn: expires_in},
  });
}

function _clearAccessToken(state:OIDCState):OIDCState
{
  return ({
    ...state,
    accessToken: undefined,
  });
}

function _setRefreshToken(state:OIDCState, token:string, expires_in: number):OIDCState
{
  return ({
    ...state,
    refreshToken: {token: token, expiresIn: expires_in},
  });
}

function _addResource(state:OIDCState, newResources:string[]):OIDCState
{
  var resources = new Set<string>(state.resources);
  newResources.forEach(r => resources.add(r));
  return ({
    ...state,
    resources: resources
  });
}

function _rememberRouteBeforeLogin(state:OIDCState, url:string):OIDCState
{
  sessionStorage.setItem("hatoka_oidc_route", url);
  return ({
    ...state,
    route: url,
  });
}
