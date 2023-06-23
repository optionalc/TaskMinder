package rs.lukamatovic.TaskMinder.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rs.lukamatovic.TaskMinder.payload.request.LoginRequest;
import rs.lukamatovic.TaskMinder.payload.request.ResetPasswordRequest;
import rs.lukamatovic.TaskMinder.payload.request.SignupRequest;
import rs.lukamatovic.TaskMinder.payload.request.UpdateEmailRequest;
import rs.lukamatovic.TaskMinder.payload.request.UpdatePasswordRequest;
import rs.lukamatovic.TaskMinder.payload.response.JwtResponse;
import rs.lukamatovic.TaskMinder.payload.response.MessageResponse;
import rs.lukamatovic.TaskMinder.security.service.UserAuthService;
import rs.lukamatovic.TaskMinder.security.service.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	UserAuthService userAuthService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		String jwt = userAuthService.authenticateUser(loginRequest);
		UserDetailsImpl userDetails = userAuthService.getUserDetails();
		List<String> roles = userAuthService.getRoles(userDetails);

		return ResponseEntity.ok(
				new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		int errCode = userAuthService.registerUser(signUpRequest);
		if (errCode == 1) {
			return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken!"));
		}
		if (errCode == 2) {
			return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
		}
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@PostMapping("/updateEmail")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> updateEmail(@RequestBody UpdateEmailRequest updateEmailRequest) throws NotFoundException {
		int errCode = userAuthService.updateEmail(updateEmailRequest);
		if (errCode == 1) {
			return ResponseEntity.badRequest().body(
					new MessageResponse("You can change email only if you are logged in and only for your account!"));
		}
		return ResponseEntity.ok(new MessageResponse("Email updated successfully!"));
	}

	@PostMapping("/updatePassword")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest)
			throws NotFoundException {
		int errCode = userAuthService.updatePassword(updatePasswordRequest);
		if (errCode == 1) {
			return ResponseEntity.badRequest().body(new MessageResponse(
					"You can change password here only if you are logged in and only for your account!"));
		}
		return ResponseEntity.ok(new MessageResponse("Password updated successfully!"));
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest)
			throws NotFoundException {
		int errCode = userAuthService.resetPassword(resetPasswordRequest);
		if (errCode == 1) {
			return ResponseEntity.badRequest().body(new MessageResponse("User with entered email does not exist!"));
		}
		return ResponseEntity.ok(new MessageResponse("Email with temproary password sent successfully!"));
	}
}
