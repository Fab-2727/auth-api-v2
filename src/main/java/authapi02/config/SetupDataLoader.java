package authapi02.config;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import authapi02.model.CustomUser;
import authapi02.model.Role;
import authapi02.repository.CustomUserRepository;
import authapi02.repository.RoleRepository;

/**
 * This class loads all the necessary data.
 *
 */
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private boolean alreadySetup = false;
	
	/* 	ADMIN USER	*/
	// TODO: des-hardcode this
	private String username = "admin";
	private String password = "admin";
	
	@Autowired
	private CustomUserRepository customUserRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	private BCryptPasswordEncoder bCryptPassEncoder = new BCryptPasswordEncoder();

	
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		if (alreadySetup) {
			return;
		}
		
		// Creation of basic ROLES.
		// TODO: implement an ENUM that contains this values.
		createRoleIfNotFound("ROLE_ADMIN");
		createRoleIfNotFound("ROLE_USER");
		createRoleIfNotFound("ROLE_STAFF");
	
		// Creation of unique admin user.
		createUserIfNotFound();

		alreadySetup = true;
	}
	
	@Transactional
	private Role createRoleIfNotFound(String roleName) {
		Role roleFetched = roleRepository.findByRole(roleName);
		if (roleFetched == null) {
			roleFetched = new Role(roleName, null);
			roleFetched.setActive(true);
			roleRepository.save(roleFetched);
		}
		return roleFetched;
	}
	
	@Transactional
	private CustomUser createUserIfNotFound() {
		CustomUser userFetched = customUserRepository.findByUsername(username);
		if (userFetched == null) {
			Role adminRole = roleRepository.findByRole("ROLE_ADMIN");
			CustomUser userAdmin = new CustomUser();
			userAdmin.setUsername(username);
			userAdmin.setPassword(bCryptPassEncoder.encode(password));
			userAdmin.setRoles(new HashSet<Role>(Arrays.asList(adminRole)));
			userAdmin.setActive(true);
			return customUserRepository.save(userAdmin);
		}
		return userFetched;
	}
	
	
}
