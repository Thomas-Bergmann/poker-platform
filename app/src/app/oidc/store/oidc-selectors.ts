import { createSelector, createFeatureSelector } from '@ngrx/store';

import { OIDCState, oidcFeatureKey } from './oidc-reducers';

const selectFeature = createFeatureSelector<OIDCState>(oidcFeatureKey);

export const selectProviders = createSelector(
  selectFeature,
  (state: OIDCState) => state.allOIDCProviders
);

export const selectCurrentProvider = createSelector(
  selectFeature,
  (state: OIDCState) => state.allOIDCProviders.find(p => (p.localRef == state.currentOIDCProvider)));

export const selectCurrentUser = createSelector(
  selectFeature,
  (state: OIDCState) => state.currentUser);

export const selectAccessToken = createSelector(
  selectFeature,
  (state: OIDCState) => state.accessToken);

export const selectRefreshToken = createSelector(
  selectFeature,
  (state: OIDCState) => state.refreshToken);

export const selectResources = createSelector(
  selectFeature,
  (state: OIDCState) => state.resources);

export const selectRoute = createSelector(
  selectFeature,
  (state: OIDCState) => state.route);
