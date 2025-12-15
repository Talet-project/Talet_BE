package com.talet.talet.util;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.talet.talet.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;

@Component
public class ApplePublicKeyProvider {
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    public Claims verify(String idToken) {
        try {
            DecodedJWT decodedJWT = JWT.decode(idToken);
            String kid = decodedJWT.getKeyId();

            URL url = new URL("https://appleid.apple.com/auth/keys");
            JwkProvider provider = new UrlJwkProvider(url);
            Jwk jwk = provider.get(kid);
            RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();

            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .requireIssuer(APPLE_ISSUER)
                    .build()
                    .parseClaimsJws(idToken)
                    .getBody();

        } catch (Exception e) {
            throw new CustomException(ErrorEnum.AUTH_CLAIM_PARSING_FAILED);
        }
    }
}
