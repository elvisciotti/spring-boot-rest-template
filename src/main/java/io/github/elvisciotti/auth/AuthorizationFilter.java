package io.github.elvisciotti.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Validates the token sent by the client to allow it to consume the protected endpoints.
 */
public class AuthorizationFilter extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.HEADER_NAME);

        if (header == null) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken userPassAuthToken = getUPATokenFromRequest(request);

        SecurityContextHolder.getContext().setAuthentication(userPassAuthToken);
        chain.doFilter(request, response);
    }

    /**
     * check if the token exists and if so, if it’s valid or not. In the case that it is, it will get an encrypted user f
     */
    private UsernamePasswordAuthenticationToken getUPATokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_NAME);
        if (token == null) {
            return null;
        }
        if (!token.startsWith("Bearer ")) {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(SecurityConstants.KEY.getBytes()))
                .parseClaimsJws(token.substring(7));
        Claims body = claims.getBody();

        if (body == null) {
            return null;
        }

        String subject = body.getSubject();

        return new UsernamePasswordAuthenticationToken(subject, null, new ArrayList<>());
    }
}
