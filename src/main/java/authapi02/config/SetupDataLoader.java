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
 * This class inserts the ROLES in the database if necessary.<br>
 * Also, it creates an admin and normal user.
 *
 */
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private boolean alreadySetup = false;
	
	/* 	ADMIN USER	*/
	// TODO: des-hardcode this
	private String usernameAdmin = "admin";
	private String passwordAdmin = "admin";
	
	// TODO: des-hardcode this
	private String usernameUser = "user";
	private String passwordUser = "1234";
	
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
		createAdminUserIfNotFound();
		// Creation of normal user.
		createNormalUserIfNotFound();
		
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
	private CustomUser createAdminUserIfNotFound() {
		CustomUser userFetched = customUserRepository.findByUsername(usernameAdmin);
		if (userFetched == null) {
			Role adminRole = roleRepository.findByRole("ROLE_ADMIN");
			CustomUser userAdmin = new CustomUser();
			userAdmin.setUsername(usernameAdmin);
			userAdmin.setPassword(bCryptPassEncoder.encode(passwordAdmin));
			userAdmin.setRoles(new HashSet<Role>(Arrays.asList(adminRole)));
			userAdmin.setActive(true);
			return customUserRepository.save(userAdmin);
		}
		return userFetched;
	}
	
	@Transactional
	private CustomUser createNormalUserIfNotFound() {
		CustomUser userFetched = customUserRepository.findByUsername(usernameUser);
		if (userFetched == null) {
			Role roles = roleRepository.findByRole("ROLE_USER");
			CustomUser user = new CustomUser();
			user.setUsername(usernameUser);
			user.setPassword(bCryptPassEncoder.encode(passwordUser));
			user.setRoles(new HashSet<Role>(Arrays.asList(roles)));
			user.setActive(true);
			return customUserRepository.save(user);
		}
		return userFetched;
	}
	
	
}
