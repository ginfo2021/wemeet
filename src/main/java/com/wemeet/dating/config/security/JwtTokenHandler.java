package com.wemeet.dating.config.security;


import com.wemeet.dating.config.WemeetConfig;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.TokenInfo;
import com.wemeet.dating.model.entity.AdminUser;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.TokenType;
import com.wemeet.dating.model.enums.UserType;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.AdminUserService;
import com.wemeet.dating.service.AuthService;
import com.wemeet.dating.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    @Autowired
    WemeetConfig config;

    private long validityInMilliseconds;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminUserService adminUserService;


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        validityInMilliseconds = config.getWemeetJwtvalidityInMilliseconds() * 3600000;
    }

    public String createToken(User user, String role) {

        Claims claims = Jwts.claims().setSubject(user.getEmail());
        if (StringUtils.hasText(role)) {
            claims.put("role", role);
        }

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
        boolean admin = false;
        User user = userService.findUserByEmail(getUsername(token));
        String userRole = getRole(token);
        //FIND ADMIN USER HERE
        if (user == null && StringUtils.hasText(userRole)) {

            AdminUser adminUser = adminUserService.findUserByEmail(getUsername(token));
            if (adminUser != null) {
                user = new User();
                BeanUtils.copyProperties(adminUser, user);
                admin = true;
            }

        }

        UserType userType = admin ? UserType.ADMIN : UserType.USER;


        return UserResult
                .builder()
                .tokenInfo(
                        TokenInfo.builder()
                                .accessToken(token)
                                .tokenType(TokenType.BEARER)
                                .build()
                )
                .user(user)
                .userType(userType)
                .build();

    }

    private String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token).getBody().getSubject();
    }

    private String getRole(String token) {
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return claims.getBody().get("role", String.class);
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
