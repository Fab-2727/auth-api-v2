package authapi02.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import authapi02.security.AuthenticationFilter;
import authapi02.security.AuthorizationFilter;
import authapi02.security.CustomAuthenticationEntryPoint;
import authapi02.service.CustomUserService;


@EnableGlobalMethodSecurity(
securedEnabled = true,
prePostEnabled = true,
jsr250Enabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomUserService customUserService;
	private BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();
	
	@Value("${token.secret.key}")
	private String secret;
	@Value("${token.expiration.time}")
	private long expirationTime;
	
	@Value("${security.role.hierarchy}")
	private String roleHierarchyStr;
	
	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		roleHierarchy.setHierarchy(this.roleHierarchyStr);
		return roleHierarchy;
	}

	@Bean
    public DefaultWebSecurityExpressionHandler webExpressionHandler() {
    	DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
        return defaultWebSecurityExpressionHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	http.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
    	.and()
    	.cors().and().csrf().disable()
        		.authorizeRequests()
        		.expressionHandler(webExpressionHandler())
                .antMatchers(HttpMethod.POST, "/api/v1/register").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .addFilter(new AuthenticationFilter(authenticationManager(),secret, expirationTime, customUserService))
                .addFilter(new AuthorizationFilter(authenticationManager(),customUserService, secret))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
    
    // 
    // the global one. 
    /**
     * As this method is overrides configure, a "local" autheticationManager is being build, a.k.a, a child of the global one.<br>
     * If we want this to be the global one, we must use @Autowired instead of @Override.
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService).passwordEncoder(passEncoder);
    }
    
}
