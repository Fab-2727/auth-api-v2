package authapi02.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import authapi02.model.Role;
import authapi02.repository.RoleRepository;

@Service
public class RoleService {

	@Autowired
	RoleRepository roleRepository;
	
	public Role createRole( Role role) {
		return roleRepository.save(role);
	}
	
	public Role findByRole (@PathVariable(name = "role") String role) {
		Role roleFetched = roleRepository.findByRole(role);
		if ( roleFetched != null) {
			return roleFetched;
		}
		return null;
	}
	
	
}
