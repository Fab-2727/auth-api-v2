package authapi02.model;

import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
@Table(name = "user")
public class CustomUser extends BaseEntity {

	public CustomUser() {
		super();
	}
	
	public CustomUser(String username, String password, Set<Role> roles) {
		super();
		this.username = username;
		this.password = password;
		this.roles = roles;
	}
	
	@Column(unique = true)
	private String username;
	
	private String password;

	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "user_role", 
	joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), 
	inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
	private Set<Role> roles;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public void addRole(Role role) {
		this.roles.add(role);
	}

	
	
	@Override
	public String toString() {
		return "CustomUser [username=" + username + ", password=" + password + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(password, username);
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
		CustomUser other = (CustomUser) obj;
		return Objects.equals(password, other.password) && Objects.equals(username, other.username);
	}
	
	
	
}
