import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { slideInAnimation } from './animations';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { OIDCState, selectCurrentUser } from 'src/app/oidc';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass'],
  animations: [ slideInAnimation ]
})
export class AppComponent {
  currentUser$: Observable<string>;

  constructor(
    private readonly oidcStore: Store<OIDCState>,
  ) {
    this.currentUser$ = this.oidcStore.select(selectCurrentUser);
  }
  getAnimationData(outlet: RouterOutlet) {
    return outlet?.activatedRouteData?.['animation'];
  }
}
