import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { OIDCState, OIDCProvider, selectProviders, defineProvider } from 'src/app/oidc/store';
import { OIDCAuthenticationService, OIDCFacade } from 'src/app/oidc/service';

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.sass']
})

export class LoginComponent implements OnInit {
  identityProviders$: Observable<readonly OIDCProvider[]>;

  constructor(
    private readonly oidcStore: Store<OIDCState>,
    private readonly oidcService : OIDCAuthenticationService,
    private readonly oidcFacade: OIDCFacade
  ) {
    this.identityProviders$ = this.oidcStore.select(selectProviders);
    this.identityProviders$.subscribe(allProviders => {
      // console.debug("idp loaded", allProviders);
      if (allProviders.length == 1)
      {
        this._selectProvider(allProviders[0]);
      }
    });
  }

  ngOnInit(): void {
    this.oidcFacade.loadIdentityProviders();
  }
  _selectProvider(identityProvider : OIDCProvider) {
    this.oidcStore.dispatch(defineProvider({ provider : identityProvider}));
    this.oidcService.getCodeForCodeFlow(identityProvider);
  }

}
