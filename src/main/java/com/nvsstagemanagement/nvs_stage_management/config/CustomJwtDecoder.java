package com.nvsstagemanagement.nvs_stage_management.config;

import com.nimbusds.jose.JOSEException;
import com.nvsstagemanagement.nvs_stage_management.dto.request.IntrospectRequest;
import com.nvsstagemanagement.nvs_stage_management.service.impl.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    private final String signerKey;
    private final AuthenticationService authenticationService;
    private NimbusJwtDecoder nimbusJwtDecoder;

    @Autowired
    public CustomJwtDecoder(@Value("${jwt.signerKey}") String signerKey,
                            AuthenticationService authenticationService) {
        this.signerKey = signerKey;
        this.authenticationService = authenticationService;
        initJwtDecoder();
    }

    private void initJwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");
        this.nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            System.out.println("Decoding Token: " + token);

            var response = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build()
            );

            System.out.println("Introspection Response: " + response);

            if (!response.isValid()) {
                System.out.println("Token invalid");
                throw new JwtException("Token invalid");
            }

        } catch (JOSEException | ParseException e) {
            System.out.println("Error decoding Token: " + e.getMessage());
            throw new JwtException(e.getMessage());
        }

        Jwt decodedJwt = nimbusJwtDecoder.decode(token);
        System.out.println("Token verified successfully: " + decodedJwt.getSubject());
        return decodedJwt;
    }
}
