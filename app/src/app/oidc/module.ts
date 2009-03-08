import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AuthRoutingModule } from './routing';
import * as store from './store';
import * as service from './service';
import * as component from './components';

@NgModule({
  declarations: [
    component.LoginComponent,
    component.IdentityProviderComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    StoreModule.forFeature(store.oidcFeatureKey, store.oidcReducer),
    AuthRoutingModule,
  ],
  providers: [
    service.OIDCFacade,
    service.OIDCService,
    service.OIDCAuthenticationService,
    service.OIDCAuthorizationService,
    service.httpInterceptorProviders,
  ],
})

export class OIDCModule {}
