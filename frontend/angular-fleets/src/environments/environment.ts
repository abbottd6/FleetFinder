import {LogLevel} from "angular-auth-oidc-client";

export const environment = {
  production: false,
  apiBaseUrl: '/api',
  oidc: {
    authority: 'http://localhost:8180/realms/oauthrealm',
    redirectUrl: window.location.origin,
    postLogoutRedirectUri: window.location.origin,
    clientId: 'fleetfinder-frontend',
    responseType: 'code',
    scope: 'openid profile email',
    useRefreshToken: true,
    silentRenew: true,
    logLevel: LogLevel.Debug,
  },
  // keycloak: {
  //   url: 'http://localhost:8180',
  //   realm: 'oauthrealm',
  //   clientId: 'fleetfinder-frontend',
  // }
}
