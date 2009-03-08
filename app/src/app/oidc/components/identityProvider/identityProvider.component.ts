import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';

import { EMPTY, OIDCState, OIDCProvider, defineProvider } from 'src/app/oidc/store';
import { OIDCAuthenticationService } from 'src/app/oidc/service';

@Component({
  selector: 'identityProvider',
  templateUrl: './identityProvider.component.html',
  styleUrls: ['./identityProvider.component.sass']
})
export class IdentityProviderComponent {
  @Input() identityProvider : OIDCProvider = EMPTY;
  constructor(
    private readonly oidcStore: Store<OIDCState>,
    private readonly oidcService : OIDCAuthenticationService,
  ) {
  }
}
