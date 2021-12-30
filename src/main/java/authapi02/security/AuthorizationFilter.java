package authapi02.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import authapi02.AuthApi02Application;
import authapi02.model.ApiResponseDto;
import authapi02.model.ErrorDictionary;
import authapi02.service.CustomUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

public class AuthorizationFilter extends BasicAuthenticationFilter {

	/**
	 * As we are using HS512, this string must be of no less than 512 bits.
	 * We need this value in order to decrypt the token.
	 */
	private String secret;
	
	private static Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);
	
	@Autowired
	CustomUserService customUserService;
	
	public AuthorizationFilter(AuthenticationManager authenticationManager, CustomUserService customUserService, String secret) {
		super(authenticationManager);
		this.customUserService = customUserService;
		this.secret = secret;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String header = request.getHeader("Authorization");

		if (header == null) {
			chain.doFilter(request, response);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = null;
		try {
			authentication = authenticate(request);
		} catch (ExpiredJwtException e) {
			logger.error(ErrorDictionary.TOKEN_HAS_EXPIRED.toString());
			logger.error(AuthApi02Application.getExceptionDescriptionForLogging(e));
			response.getWriter().write(new ApiResponseDto(ErrorDictionary.TOKEN_HAS_EXPIRED).toString());
		} catch (MalformedJwtException e) {
			logger.error(ErrorDictionary.MALFORMED_TOKEN.toString());
			logger.error(AuthApi02Application.getExceptionDescriptionForLogging(e));
			response.getWriter().write(new ApiResponseDto(ErrorDictionary.MALFORMED_TOKEN).toString());
		}
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);
	}

	@SuppressWarnings("unused")
	private UsernamePasswordAuthenticationToken authenticate(HttpServletRequest request) throws JwtException{
		String token = request.getHeader("Authorization");
		token = token.split(" ")[1];
		if (token != null) {
			
			Claims user = null;
			Set<GrantedAuthority> rolesFromUser = null;
			// If an exception occurs, return null	
			user = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).build().parseClaimsJws(token.trim()).getBody();

			rolesFromUser = new HashSet<GrantedAuthority>(customUserService.loadUserByUsername(user.getSubject()).getAuthorities());
			if (user != null) {
				return new UsernamePasswordAuthenticationToken(user, null, rolesFromUser);
			} else {
				return null;
			}
		}
		return null;
	}
	
	
	
	
}
