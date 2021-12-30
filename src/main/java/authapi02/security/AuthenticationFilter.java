package authapi02.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import authapi02.model.CustomUser;
import authapi02.service.CustomUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	// NOTE: @Autowired doesn't seem to work here, use constructor.
	
	/**
	 * As we are using HS512, this string must be of no less than 512 bits
	 */
	private AuthenticationManager authenticationManager;
	private String secret;
	private long expirationTime;
	private CustomUserService customUserService;
	
	private static Map<String, Object> headerMap = new HashMap<String, Object>();
	private final static ObjectMapper objectMapper = new ObjectMapper();
	
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
			if (inputStream != null) {
				CustomUser customUser = objectMapper.readValue(inputStream, CustomUser.class);
				// TODO validate this
				List<GrantedAuthority> grantedAuthorities = customUserService.getUserRolesByUser(customUser);
				System.out.println("AUTH SIZE: " + grantedAuthorities.size());
				
				return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customUser.getUsername(),
						customUser.getPassword(), grantedAuthorities));
				
			}
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("", "", Collections.emptyList()));
		} catch (Exception e) {
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
		
		Date exp = new Date(System.currentTimeMillis() + expirationTime);
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
		
		// WORKS
		res.setHeader("Authorization", token);
		res.getWriter().write("{\"bearer\": \""+ token + "\"}");
		res.getWriter().flush();
	}
	
	
}
