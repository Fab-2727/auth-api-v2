package authapi02.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import authapi02.model.CustomUser;
import authapi02.model.Role;

public interface CustomUserRepository extends JpaRepository<CustomUser, Long>{

	public CustomUser findByUsername(String username);
	
}
