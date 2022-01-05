package authapi02.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import authapi02.AuthApi02Application;
import authapi02.model.CustomUser;
import authapi02.model.ErrorDictionary;
import authapi02.service.CustomUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

	/**
	 * As we are using HS512, this string must be of no less than 512 bits
	 */
	private String secret;
	private AuthenticationManager authenticationManager;
	private long expirationTime;
	private CustomUserService customUserService;
	
	
	private static Map<String, Object> headerMap = new HashMap<String, Object>();
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	public AuthenticationFilter(AuthenticationManager authenticationManager, String secret, long expirationTime, CustomUserService customUserService) {
		super();
		this.secret = secret;
		this.expirationTime = expirationTime;
		this.authenticationManager = authenticationManager;
		this.customUserService = customUserService;
		
		headerMap.put("typ", "JWT");
		headerMap.put("alg", SignatureAlgorithm.HS512.getValue());
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException { 
		try {
				InputStream inputStream = request.getInputStream();
				CustomUser customUser = objectMapper.readValue(inputStream, CustomUser.class);
				List<GrantedAuthority> grantedAuthorities = customUserService.getUserRolesByUser(customUser);

				return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customUser.getUsername(),
						customUser.getPassword(), grantedAuthorities));
		// Catching exceptions
		} catch (DisabledException e) {
			logger.error(ErrorDictionary.DISABLED_ACCOUNT.toString());
			logger.error(AuthApi02Application.getExceptionDescriptionForLogging(e));
			throw new RuntimeException(e);
		} catch (BadCredentialsException e) {
			logger.error(ErrorDictionary.BAD_CREDENTIALS.toString());
			logger.error(AuthApi02Application.getExceptionDescriptionForLogging(e));
			request.setAttribute("BAD_CREDENTIALS", e.getMessage());
			throw new RuntimeException(e);
		} catch (LockedException e) {
			logger.error(ErrorDictionary.LOCKED_ACCOUNT.toString());
			logger.error(AuthApi02Application.getExceptionDescriptionForLogging(e));
			throw new RuntimeException(e);
		} catch (IllegalStateException | IOException e) {
			logger.error(ErrorDictionary.UNEXPECTED_ERROR.toString());
			logger.error(AuthApi02Application.getExceptionDescriptionForLogging(e));
			throw new RuntimeException(e);
		} catch (Exception e) {
			logger.error(ErrorDictionary.UNEXPECTED_ERROR.toString());
			logger.error(AuthApi02Application.getExceptionDescriptionForLogging(e));
			if (e.getClass().getSimpleName().equalsIgnoreCase("InternalAuthenticationServiceException")) {
				request.setAttribute("UNKNOWN_USER", "UNKNOWN_USER");
			}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * <p>A JWT consist of a header, a payload and signature.</p>
	 * <p>These three parts are encoded separately in BASE64 and concatenate using periods to produce the JWT.</p>
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

		// java.lang.NoSuchMethodError: 'int io.jsonwebtoken.SignatureAlgorithm.getMinKeyLength()'
		// The above error was solved deleting the 'io.jsonwebtoken/jjwt/0.9.1' dependency from POM
		
		Date exp = new Date(System.currentTimeMillis() + (this.expirationTime * 1000L));
		Key key = Keys.hmacShaKeyFor(secret.getBytes());
		Claims claims = Jwts.claims()
						.setSubject( ((User) auth.getPrincipal()).getUsername())
						.setIssuedAt(new Date(System.currentTimeMillis()));
					
		String token = Jwts.builder()
						.setHeader(headerMap)
						.setClaims(claims)
						.signWith(key, SignatureAlgorithm.HS512)
						.setExpiration(exp)
						.compact();
		
		res.setHeader("Authorization", token);
		res.getWriter().write("{\"bearer\": \""+ token + "\"}");
		res.getWriter().flush();
	}
	
	
}
