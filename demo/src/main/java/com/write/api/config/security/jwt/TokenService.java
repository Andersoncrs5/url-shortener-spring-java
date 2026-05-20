package com.write.api.config.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.shared.Result;
import com.write.api.config.security.properties.JwtProperties;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtProperties properties;

    public AuthTokenResponseDTO createTokens(UserModel user) {
        String token = this.generateToken(user);
        String refreshToken = this.generateRefreshToken(user);

        return new AuthTokenResponseDTO(
                token,
                refreshToken,
                user,
                user.getRoles()
        );
    }

    public String generateToken(UserModel user) {
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(this.genExpirationDate()))
                .claim("roles", user.getRoles())
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);

        try {
            signedJWT.sign(new MACSigner(properties.getJwt().getSecret().getBytes()));
            return signedJWT.serialize();
        } catch (KeyLengthException e) {
            log.error("Error signing token! Secret string might be too short. Error: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new InternalServerErrorException("Error processing security tokens: " + e.getMessage());
        }
    }

    public String generateRefreshToken(UserModel user) {
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(this.genExpirationDateRefreshToken()))
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);

        try {
            signedJWT.sign(new MACSigner(properties.getJwt().getSecret().getBytes()));
            return signedJWT.serialize();
        } catch (KeyLengthException e) {
            log.error("Error signing refresh token! Error: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new InternalServerErrorException("Error processing security tokens: " + e.getMessage());
        }
    }

    public String validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            MACVerifier verifier = new MACVerifier(properties.getJwt().getSecret().getBytes());

            if (!signedJWT.verify(verifier)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Assinatura do token inválida.");
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            if (claimsSet.getExpirationTime().before(new Date())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expirado.");
            }

            return claimsSet.getSubject();
        } catch (ParseException | JOSEException e) {
            log.debug("Erro ao parsear ou verificar o token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido.");
        }
    }

    public Result<String> validateTokenV2(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            MACVerifier verifier = new MACVerifier(properties.getJwt().getSecret().getBytes());

            if (!signedJWT.verify(verifier)) {
                return Result.failure(401 ,"Assinatura do token inválida.");
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            if (claimsSet.getExpirationTime().before(new Date())) {
                return Result.failure(401 ,"Token expirado.");
            }

            return Result.success(claimsSet.getClaimAsString("userId"));
        } catch (ParseException | JOSEException e) {
            log.debug("Erro ao parsear ou verificar o token: {}", e.getMessage());
            return Result.failure(401, "Token inválido.");
        }
    }

    public Map<String, Object> extractAllClaims(String token) {
        log.debug("Extraindo todas as claims do token.");
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getClaims();
        } catch (ParseException e) {
            log.error("Erro ao extrair claims do token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token mal formatado.", e);
        }
    }

    public String extractSubjectFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        return getClaimsFromToken(token).getSubject();
    }

    public Long extractUserIdFromRequest(HttpServletRequest request) {
        log.debug("Extracting id of token...");
        String token = extractTokenFromRequest(request);
        try {
            Long userId = getClaimsFromToken(token).getLongClaim("userId");
            log.debug("Id extracted with success! Id: {}", userId);
            return userId;
        } catch (ParseException e) {
            log.error("Erro ao extrair o userId como Long: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token contém dados de usuário inválidos.");
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Cabeçalho de autorização não encontrado ou formato inválido.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return authHeader.substring(7);
    }

    private JWTClaimsSet getClaimsFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet();
        } catch (ParseException e) {
            log.error("Erro ao parsear o token para extrair claims: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido.", e);
        }
    }

    private Instant genExpirationDate() {
        return Instant.now().plus(properties.getExp().getToken(), ChronoUnit.HOURS);
    }

    private Instant genExpirationDateRefreshToken() {
        return Instant.now().plus(properties.getExp().getRefresh(), ChronoUnit.HOURS);
    }
}