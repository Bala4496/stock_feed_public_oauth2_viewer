package ua.bala.stock_feed_public_oauth2_viewer.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@Component
public class TokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String subject) {
        return createToken(subject, expiration);
    }

    private String createToken(String subject, long expiration) {
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(expiration, ChronoUnit.MILLIS))
                .sign(Algorithm.HMAC256(secret));
    }

    public boolean validateToken(UserDetails userDetails, String token) {
        return (extractSubject(token).equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractSubject(String token) {
        return extractClaim(token, DecodedJWT::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(Instant.now());
    }

    private Instant extractExpiration(String token) {
        return extractClaim(token, DecodedJWT::getExpiresAtAsInstant);
    }

    private <T> T extractClaim(String token, Function<DecodedJWT, T> decodeResolver) {
        return decodeResolver.apply(extractAllClaims(token));
    }

    private DecodedJWT extractAllClaims(String token) {
        return JWT.require(getSecretKey()).build()
                .verify(token);
    }

    private Algorithm getSecretKey() {
        return Algorithm.HMAC256(secret);
    }
}
