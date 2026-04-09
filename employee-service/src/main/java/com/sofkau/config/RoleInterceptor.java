package com.sofkau.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.annotation.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RoleInterceptor.class);

    @Value("${internal.signature.secret}")
    private String signatureSecret;

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AdminOnly {}

    private String generateSignature(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    Base64.getDecoder().decode(signatureSecret),
                    "HmacSHA256"
            );
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(payload.getBytes());
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        logger.debug("=== RoleInterceptor: Checking authentication ===");
        logger.debug("URI: {}", request.getRequestURI());
        
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            logger.debug("Not a handler method, allowing request");
            return true;
        }
        
        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-User-Name");
        String rolesHeader = request.getHeader("X-User-Roles");
        String signature = request.getHeader("X-Signature");
        
        logger.debug("X-User-Id: {}", userId);
        logger.debug("X-User-Name: {}", username);
        logger.debug("X-User-Roles: {}", rolesHeader);
        logger.debug("X-Signature: {}", signature);
        logger.debug("signatureSecret configured: {}", (signatureSecret != null && !signatureSecret.isEmpty()));
        
        if (userId == null || username == null || rolesHeader == null || signature == null) {
            logger.warn("Missing authentication headers - rejecting request");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing authentication headers");
            return false;
        }
        
        String payload = userId + "|" + username + "|" + rolesHeader;
        logger.debug("Payload for signature: {}", payload);
        
        String expectedSignature = generateSignature(payload);
        logger.debug("Expected signature: {}", expectedSignature);
        
        if (!expectedSignature.equals(signature)) {
            logger.warn("Invalid signature - rejecting request");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid signature");
            return false;
        }
        
        if (handlerMethod.hasMethodAnnotation(AdminOnly.class)) {
            List<String> roles = Arrays.asList(rolesHeader.split(","));
            logger.debug("Checking admin role, user roles: {}", roles);

            if (!roles.contains("ROLE_ADMIN")) {
                logger.warn("User does not have ADMIN role - rejecting request");
                response.sendError(HttpStatus.FORBIDDEN.value(), "ADMIN role required");
                return false;
            }
        }
        
        logger.debug("Authentication successful - allowing request");
        return true;
    }
}
