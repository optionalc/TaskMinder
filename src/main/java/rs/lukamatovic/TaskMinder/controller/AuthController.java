package rs.lukamatovic.TaskMinder.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rs.lukamatovic.TaskMinder.model.ERole;
import rs.lukamatovic.TaskMinder.model.Role;
import rs.lukamatovic.TaskMinder.model.User;
import rs.lukamatovic.TaskMinder.payload.request.LoginRequest;
import rs.lukamatovic.TaskMinder.payload.request.ResetPasswordRequest;
import rs.lukamatovic.TaskMinder.payload.request.SignupRequest;
import rs.lukamatovic.TaskMinder.payload.request.UpdateEmailRequest;
import rs.lukamatovic.TaskMinder.payload.request.UpdatePasswordRequest;
import rs.lukamatovic.TaskMinder.payload.response.JwtResponse;
import rs.lukamatovic.TaskMinder.payload.response.MessageResponse;
import rs.lukamatovic.TaskMinder.repository.RoleRepository;
import rs.lukamatovic.TaskMinder.repository.UserRepository;
import rs.lukamatovic.TaskMinder.security.jwt.JwtUtils;
import rs.lukamatovic.TaskMinder.security.service.UserAuthService;
import rs.lukamatovic.TaskMinder.security.service.UserDetailsImpl;
import rs.lukamatovic.TaskMinder.service.EmailService;
import rs.lukamatovic.TaskMinder.service.GenerateRandomPassService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserAuthService userAuthService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	EmailService emailService;

	@Autowired
	GenerateRandomPassService grps;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(
				new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				Role userRole = roleRepository.findByName(ERole.ROLE_USER)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
				roles.add(userRole);
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@PostMapping("/updateEmail")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> updateEmail(@RequestBody UpdateEmailRequest updateEmailRequest) throws NotFoundException {
		Authentication authentication = userAuthService.authenticateUser(updateEmailRequest.getUsername(),
				updateEmailRequest.getPassword());

		if (!userAuthService.validateEmailChange(authentication, updateEmailRequest.getUserId(),
				updateEmailRequest.getEmail())) {
			return ResponseEntity.badRequest().body(
					new MessageResponse("You can change email only if you are logged in and only for your account!"));
		}
		return ResponseEntity.ok(new MessageResponse("Email updated successfully!"));
	}

	@PostMapping("/updatePassword")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest)
			throws NotFoundException {
		Authentication authentication = userAuthService.authenticateUser(updatePasswordRequest.getUsername(),
				updatePasswordRequest.getPassword());

		if (!userAuthService.validatePasswordChange(authentication, updatePasswordRequest.getId(),
				updatePasswordRequest.getNewPassword())) {
			return ResponseEntity.badRequest().body(new MessageResponse(
					"You can change password here only if you are logged in and only for your account!"));
		}
		return ResponseEntity.ok(new MessageResponse("Password updated successfully!"));
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest)
			throws NotFoundException {

		if (!userRepository.existsByEmail(resetPasswordRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("User with email does not exis!"));
		}

		String generatedPassword = grps.generatePassword();
		String pass = encoder.encode(generatedPassword);
		emailService.sendEmail(resetPasswordRequest.getEmail(), generatedPassword);
		User u = userRepository.findByEmail(resetPasswordRequest.getEmail()).orElseThrow(NotFoundException::new);
		u.setPassword(pass);
		userRepository.save(u);
		return ResponseEntity.ok(new MessageResponse("Email with temproary password sent successfully!"));
	}
}
