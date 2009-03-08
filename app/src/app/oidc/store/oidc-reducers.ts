import { Action, createReducer, on } from '@ngrx/store';
import { OIDCProvider } from './oidc-models';
import { addProviders, defineProvider, defineUser, setAccessToken, addResources, rememberRouteBeforeLogin } from './oidc-actions';

export const oidcFeatureKey = 'oidcState';
export interface OIDCState {
  currentOIDCProvider: string; // local ref
  currentUser: string; // global ref
  accessToken: string | undefined; // token to make requests
  resources: Set<string>;
  allOIDCProviders : OIDCProvider[];
  route: string | undefined;
}

const initialState : OIDCState = {
  currentOIDCProvider : sessionStorage.getItem("hatoka_oidc_provider") || '',
  currentUser : sessionStorage.getItem("hatoka_oidc_user") || '',
  accessToken : undefined,
  resources : new Set<string>(),
  allOIDCProviders : [],
  route : sessionStorage.getItem("hatoka_oidc_route") || undefined
}

const _oidcReducer = createReducer(
  initialState,
  on(addProviders, (state, action) => _addProviders(state, action.providers)),
  on(defineProvider, (state, action) => _defineProvider(state, action.provider)),
  on(defineUser, (state, action) => _defineUser(state, action.user)),
  on(setAccessToken, (state, action) => _setAccessToken(state, action.token)),
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

function _setAccessToken(state:OIDCState, token:string):OIDCState
{
  return ({
    ...state,
    accessToken: token
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
