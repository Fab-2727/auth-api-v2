package authapi02.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure")
public class SecuredController {

	
	@GetMapping(path = "/greeting")
	public ResponseEntity<Object> secureEndpoint () {
		return ResponseEntity.ok("You have reached a secure endpoint.");
	}
	
	@GetMapping(path = "/greeting-two")
	public ResponseEntity<Object> secureEndpointAdmin () {
		return ResponseEntity.ok("You have reached a secure endpoint.");
	}
	
	
}
