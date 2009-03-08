import { InjectionToken } from "@angular/core";
import { environment } from "src/environments/environment";

export interface AppConfig {
  serviceEndPoint: string
}

export const APP_CONFIG = new InjectionToken<AppConfig>('APP_CONFIG');
export const APP_CONFIG_DEFAULT : AppConfig = {
  serviceEndPoint : environment.serviceEndPoint
};