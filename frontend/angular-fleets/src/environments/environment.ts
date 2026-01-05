import {LogLevel} from "angular-auth-oidc-client";

export const environment = {
  production: false,
  apiBaseUrl: '/api',
  wsBaseUrl: "ws://localhost:8080",
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
    loadUserInfo: false,
    autoUserInfo: false,
  }
}
