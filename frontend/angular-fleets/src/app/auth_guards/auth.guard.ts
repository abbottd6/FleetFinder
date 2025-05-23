import { inject } from "@angular/core";
import {Router, CanActivateFn} from '@angular/router';
import { createAuthGuard, AuthGuardData} from "keycloak-angular";
import Keycloak from 'keycloak-js';

  const isAccessAllowed = async (
    _route: any,
    _state: any,
    authData: AuthGuardData
  ): Promise<boolean> => {
    if (!authData.authenticated) {
      const kc = inject(Keycloak);
      await kc.login({ redirectUri: window.location.origin + '/user' });
      return false;
    }

    // const requiredRoles = route.data['roles'] as string[];
    // return requiredRoles.every(r => this.roles.includes(r));
    return true;
  };

  export const keycloakAppGuard: CanActivateFn =
    createAuthGuard<CanActivateFn>(isAccessAllowed);
