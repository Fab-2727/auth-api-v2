package authapi02.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import authapi02.model.Role;
import authapi02.repository.RoleRepository;
import authapi02.service.CustomUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class AuthorizationFilter extends BasicAuthenticationFilter {

	/**
	 * As we are using HS512, this string must be of no less than 512 bits.
	 * We need this value in order to decrypt the token.
	 */
	@Value("${secret.key}")
	private String secret;

	@Autowired
	CustomUserService customUserService;
	
	public AuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String header = request.getHeader("Authorization");

		if (header == null) {
			chain.doFilter(request, response);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = authenticate(request);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);
	}

	@SuppressWarnings("unused")
	private UsernamePasswordAuthenticationToken authenticate(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null) {
			Claims user = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).build().parseClaimsJws(token).getBody();
			
			// TODO: validate this.
			Set<GrantedAuthority> rolesFromUser = new HashSet<GrantedAuthority>(customUserService.loadUserByUsername(user.getSubject()).getAuthorities());
			
			if (user != null) {
				return new UsernamePasswordAuthenticationToken(user, null, rolesFromUser);
			} else {
				return null;
			}
		}
		return null;
	}
	
	
	
	
}
