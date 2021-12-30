package authapi02.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import authapi02.model.CustomUser;
import authapi02.model.Role;
import authapi02.repository.CustomUserRepository;
import authapi02.repository.RoleRepository;

@Service
public class CustomUserService implements UserDetailsService {

	@Autowired
	private CustomUserRepository customUserRepository;
	@Autowired
	private RoleRepository roleRepository;
	
	private static BCryptPasswordEncoder bCryptPassEncoder = new BCryptPasswordEncoder();

	@Transactional
	public CustomUser createUser (CustomUser user) {
		
		user.setPassword(bCryptPassEncoder.encode(user.getPassword()));
		user.setActive(true);
		// By default, the role is going to be 'USER'. It means that it has access to all endpoints, except for those that require an 'ADMIN' role.
		Role userRole = roleRepository.findByRole("ROLE_USER");
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		
		return customUserRepository.save(user);
	}
	
	@Transactional
	public List<GrantedAuthority> getUserRolesByUser (CustomUser user) {
		return getUserAuthority(roleRepository.getUserRolesByUser(user));
	}
	
	@SuppressWarnings("unused")
	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		CustomUser userFetched = customUserRepository.findByUsername(username);
		List<GrantedAuthority> grantedAuthorities = CustomUserService.getUserAuthority(userFetched.getRoles());
		
		if (userFetched != null) {
			return new User(username, userFetched.getPassword(), userFetched.isActive(), true, true, true, grantedAuthorities);
		} else {
			return null;
		}
	}
	
	/**
	 * This method is only used when we retrieve information from the DB.
	 * We don't save SimpledGrantedAuthority in the DB.
	 */
	private static List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
		for (Role role : userRoles) {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
		
	}
	
	
}
