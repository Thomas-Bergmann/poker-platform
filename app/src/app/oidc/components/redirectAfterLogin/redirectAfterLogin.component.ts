import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, Unsubscribable } from 'rxjs';
import { Router } from '@angular/router';

import { OIDCState, OIDCProvider, selectCurrentProvider, selectRoute, selectAccessToken } from 'src/app/oidc/store';
import { OIDCFacade } from 'src/app/oidc/service';

@Component({
  selector: 'redirectAfterLogin',
  templateUrl: './redirectAfterLogin.component.html',
  styleUrls: ['./redirectAfterLogin.component.sass']
})

export class RedirectAfterLoginComponent implements OnInit {
  identityProvider$: Observable<OIDCProvider | undefined>;
  redirectToRoute: string | undefined;
  accessTokenUnsub: Unsubscribable;
  constructor(
    private readonly oidcStore: Store<OIDCState>,
    private readonly oidcFacade: OIDCFacade,
    private router: Router,
  ) {
    this.identityProvider$ = this.oidcStore.select(selectCurrentProvider);
    this.oidcStore.select(selectRoute).subscribe(route =>
    {
      this.redirectToRoute = route;
    });
    this.accessTokenUnsub = this.oidcStore.select(selectAccessToken).subscribe(token => {
      if (token !== undefined && this.redirectToRoute !== undefined)
      {
        this.router.navigate([this.redirectToRoute]);
        this.accessTokenUnsub.unsubscribe();
      }
    });
  }

  ngOnInit() {
    this.oidcFacade.loadIdentityProviders();
    this.identityProvider$.subscribe(p => this.oidcFacade.loginViaCodeFlow(p));
  }
}
