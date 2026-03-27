import {LogLevel} from "angular-auth-oidc-client";

export const environment = {
  production: true,
  apiBaseUrl: 'https://scfleetfinder.com/api',
  wsBaseUrl: "wss://scfleetfinder.com",
  vapidPublicKey: "BDwOYrwYIWZzQpJY-QZ93bElLlD33ly4Kx-NKwu6RneHyZ0pb2xGJ8eogXQX2tMOsQjvKx8WkEaZAMJwSndeK8E",
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
