package authapi02.security;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import authapi02.AuthApi02Application;
import authapi02.model.CustomUser;
import authapi02.model.ErrorDictionary;
import authapi02.service.CustomUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

public class AuthorizationFilter extends BasicAuthenticationFilter {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

	/**
	 * As we are using HS512, this string must be of no less than 512 bits.
	 * We need this value in order to decrypt the token.
	 */
	private String secret;
	
	private CustomUserService customUserService;
	private static final ObjectMapper objectMapper =  new ObjectMapper();
	
	public AuthorizationFilter(AuthenticationManager authenticationManager, CustomUserService customUserService, String secret) {
		super(authenticationManager);
		this.customUserService = customUserService;
		this.secret = secret;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException, RuntimeException {
		String header = request.getHeader("Authorization");

		if (header == null) {
			// validate body of request
			validateBodyOfRequestIfNecessary(request);
			chain.doFilter(request, response);
			return;
		}
		
		UsernamePasswordAuthenticationToken authentication = null;
		try {	
			authentication = authenticate(request);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (ExpiredJwtException e) {
			logger.error(ErrorDictionary.TOKEN_HAS_EXPIRED.toString());
			logger.error(AuthApi02Application.getExceptionDescriptionForLogging(e));
			// We set attribute in order to identify the exception in the 'CustomAuthenticationEntryPoint'
			request.setAttribute("EXPIRED", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error(ErrorDictionary.MALFORMED_TOKEN.toString());
			logger.error(AuthApi02Application.getExceptionDescriptionForLogging(e));
			request.setAttribute("MALFORMED_TOKEN", e.getClass().getSimpleName() + "," + e.getMessage());
		} catch (Exception e) {
			logger.error(ErrorDictionary.UNEXPECTED_ERROR.toString());
			logger.error(AuthApi02Application.getExceptionDescriptionForLogging(e));
		}
		
		// Finally, we pass the chain.
		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken authenticate(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		token = token.split(" ")[1];
		if (token != null) {
			
			Claims user = null;
			UserDetails userDetails;

			// If an exception occurs, throws JwtException
			user = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).build().parseClaimsJws(token.trim()).getBody();
			userDetails = customUserService.loadUserByUsername(user.getSubject());
			
			if (user != null && userDetails != null) {
				return new UsernamePasswordAuthenticationToken(user, null, userDetails.getAuthorities());
			} else {
				return null;
			}
		}
		return null;
	}
	
	private static boolean validateBodyOfRequestIfNecessary(HttpServletRequest request) {
		
		// Validation of '/login' endpoint if the payload is empty.
		String requestUri = request.getRequestURI();
		if (requestUri.indexOf("login") != -1) {
			InputStream bodyStream = null;
			try {
				bodyStream = request.getInputStream();
			} catch (IOException | IllegalStateException e) {
				logger.error(ErrorDictionary.UNEXPECTED_ERROR.toString());
				AuthApi02Application.getExceptionDescriptionForLogging(e);
			}
			try {
				CustomUser customUser = objectMapper.readValue(bodyStream, CustomUser.class);
				if (customUser.isEmpty()) {
					request.setAttribute("EMPTY_BODY", "EMPTY_BODY");
					throw new AuthenticationCredentialsNotFoundException("EMPTY_BODY");
				}
			} catch (Exception e) {
				logger.error(ErrorDictionary.UNEXPECTED_ERROR.toString());
				AuthApi02Application.getExceptionDescriptionForLogging(e);
				request.setAttribute("EMPTY_BODY", "EMPTY_BODY");
				throw new AuthenticationCredentialsNotFoundException("EMPTY_BODY");
			}
		}
		return true;
	}
	
	
	
	
}
