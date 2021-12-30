package authapi02.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import authapi02.model.CustomUser;
import authapi02.service.CustomUserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	
	@Autowired
	CustomUserService customUserService;
	
	@PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> registerUser(@RequestBody CustomUser user) {
		System.out.println("Request received: " + user.toString());
		customUserService.createUser(user);
		return ResponseEntity.ok(null);
	}
	
}
