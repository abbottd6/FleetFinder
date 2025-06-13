import {LogLevel} from "angular-auth-oidc-client";

export const environment = {
  production: true,
  apiBaseUrl: 'https://scfleetfinder.com/api',
  oidc: {
    authority: 'https://scfleetfinder.com/auth/realms/oauthrealm',
    redirectUrl: window.location.origin,
    postLogoutRedirectUri: window.location.origin,
    clientId: 'fleetfinder-frontend',
    responseType: 'code',
    scope: 'openid profile email',
    useRefreshToken: true,
    silentRenew: true,
    logLevel: LogLevel.Warn,
    loadUserInfo: false,
    autoUserInfo: false,
  }
}
