package authapi02.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import authapi02.model.CustomUser;
import authapi02.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
	public Role findByRole(String role);
	
	//TODO test this query
	@Query(value = "SELECT role.* FROM user "
			 + "INNER JOIN user_role ON user_role.user_id = user.user_id "
			 + "INNER JOIN role ON role.role_id = user_role.role_id " , nativeQuery = true)
	public Set<Role> getUserRolesByUser (CustomUser user);
	
}
