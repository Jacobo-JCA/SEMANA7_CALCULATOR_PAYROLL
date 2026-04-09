package com.sofkau.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${internal.signature.secret}")
    private String signatureSecret;

    public static class Config {
    }

    public AuthenticationFilter() {
        super(Config.class);
    }

    private io.jsonwebtoken.Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateSignature(String payload) {
        try {
            logger.debug("Generating signature with payload: {}", payload);
            logger.debug("Signature secret configured: {}", (signatureSecret != null && !signatureSecret.isEmpty()));
            
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    Decoders.BASE64.decode(signatureSecret),
                    "HmacSHA256"
            );
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(payload.getBytes());
            String signature = Base64.getEncoder().encodeToString(rawHmac);
            logger.debug("Generated signature: {}", signature);
            return signature;
        } catch (Exception e) {
            logger.error("Error generating signature", e);
            throw new RuntimeException("Error generating signature", e);
        }
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization header");
        }
        if (!authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization format");
        }
        return authHeader.substring(7);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String token = extractToken(request);
            try {
                Claims claims = getClaims(token);
                List<String> roles = claims.get("roles", List.class);
                String username = claims.getSubject();
                String userId = String.valueOf(claims.get("userId"));

                String rolesStr = String.join(",", roles);
                String payload = userId + "|" + username + "|" + rolesStr;
                String signature = generateSignature(payload);

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .header("X-User-Id", userId)
                        .header("X-User-Name", username)
                        .header("X-User-Roles", rolesStr)
                        .header("X-Signature", signature)
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token: " + e.getMessage());
            }
        };
    }
}
