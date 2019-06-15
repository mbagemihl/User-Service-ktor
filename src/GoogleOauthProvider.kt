package de.novatec

import io.ktor.http.HttpMethod

val googleOauthProvider = OAuthServerSettings.OAuth2ServerSettings(
    name = "google",
    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
    accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
    requestMethod = HttpMethod.Post,

    clientId = "969901986915-8ggi00mddvt6ba64vc7008imfdqhja3c.apps.googleusercontent.com",
    clientSecret = "9_MFQ-Ocxn7wOxACkvqEfCif",
    defaultScopes = listOf("profile") // no email, but gives full name, picture, and id
)
