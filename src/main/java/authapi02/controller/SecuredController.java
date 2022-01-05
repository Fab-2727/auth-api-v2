package authapi02.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure")
public class SecuredController {

	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@GetMapping(path = "/greeting")
	public ResponseEntity<Object> secureEndpoint () {
		return ResponseEntity.ok("[ROLE_USER_ENDPOINT] You have reached a secure endpoint.");
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping(path = "/greeting-two")
	public ResponseEntity<Object> secureEndpointAdmin () {
		return ResponseEntity.ok("[ROLE_ADMIN_ENDPOINT] You have reached a secure endpoint.");
	}
	
	
}
