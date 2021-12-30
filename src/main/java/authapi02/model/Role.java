package authapi02.model;

import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


@Entity
@AttributeOverride(name = "id",column = @Column(name = "role_id"))
@Table(name = "role")
public class Role extends BaseEntity {

	public Role() {
		super();
	}
	
	public Role(String role, Set<CustomUser> users) {
		super();
		this.role = role;
		this.users = users;
	}

	@Column(name = "role")
	private String role;
	
	@ManyToMany(mappedBy = "roles")
	private Set<CustomUser> users;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Set<CustomUser> getUsers() {
		return users;
	}

	public void setUsers(Set<CustomUser> users) {
		this.users = users;
	}

	public void addUser(CustomUser user) {
		this.users.add(user);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(role, users);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		return Objects.equals(role, other.role) && Objects.equals(users, other.users);
	}
	
}
