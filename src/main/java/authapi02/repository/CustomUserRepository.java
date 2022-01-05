package authapi02.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import authapi02.model.CustomUser;

public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {

	public CustomUser findByUsername(String username);

}
