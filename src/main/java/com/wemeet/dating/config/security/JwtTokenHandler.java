package com.wemeet.dating.config.security;


import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.TokenInfo;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.TokenType;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.AuthService;
import com.wemeet.dating.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenHandler {

    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${security.jwt.token.secret}")
    private String secretKey;
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${security.jwt.token.expire.hour}")
    private long validityInMilliseconds;

    @Autowired
    private UserService userService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        validityInMilliseconds = validityInMilliseconds * 3600000;
    }

    public String createToken(User user, List<String> roles) {

        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("roles", roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();
    }

    public UserResult getAuthentication(String token) {
        User user = userService.findUserByEmail(getUsername(token));

        return UserResult
                .builder()
                .tokenInfo(
                        TokenInfo.builder()
                                .accessToken(token)
                                .tokenType(TokenType.BEARER)
                                .build()
                )
                .user(user)
                .build();

    }

    private String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(HEADER_STRING);
        if (bearerToken != null && bearerToken.toLowerCase().startsWith(TOKEN_PREFIX.toLowerCase())) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public boolean validateToken(String token) throws Exception {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }
    }


}
