import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { APP_CONFIG, APP_CONFIG_DEFAULT } from './app/app.config';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';

fetch('/assets/config/config.json')
  .then((response) => response.json())
  .then((config) => {
    if (environment.production) {
      enableProdMode()
      environment.serviceEndPoint = config.serviceEndPoint;
    }
    console.log("environment.serviceEndPoint", environment.serviceEndPoint);
    platformBrowserDynamic([{ provide: APP_CONFIG, useValue: config }])
      .bootstrapModule(AppModule)
      .catch((err) => console.error(err))
    })
    .catch((err) => {
      console.error(err);
      platformBrowserDynamic([{ provide: APP_CONFIG, useValue: APP_CONFIG_DEFAULT }])
      .bootstrapModule(AppModule)
      .catch((err) => console.error(err))
    });
