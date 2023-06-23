package rs.lukamatovic.TaskMinder.security.service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import rs.lukamatovic.TaskMinder.model.ERole;
import rs.lukamatovic.TaskMinder.model.Role;
import rs.lukamatovic.TaskMinder.model.User;
import rs.lukamatovic.TaskMinder.payload.request.LoginRequest;
import rs.lukamatovic.TaskMinder.payload.request.ResetPasswordRequest;
import rs.lukamatovic.TaskMinder.payload.request.SignupRequest;
import rs.lukamatovic.TaskMinder.payload.request.UpdateEmailRequest;
import rs.lukamatovic.TaskMinder.payload.request.UpdatePasswordRequest;
import rs.lukamatovic.TaskMinder.repository.RoleRepository;
import rs.lukamatovic.TaskMinder.repository.UserRepository;
import rs.lukamatovic.TaskMinder.security.jwt.JwtUtils;
import rs.lukamatovic.TaskMinder.service.EmailService;

@Service
public class UserAuthService {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	EmailService emailService;

	@Autowired
	JwtUtils jwtUtils;

	public String authenticateUser(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		return jwt;
	}

	public UserDetailsImpl getUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		return userDetails;
	}

	public List<String> getRoles(UserDetailsImpl userDetails) {
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());
		return roles;
	}

	public int registerUser(SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return 1;
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return 2;
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
		return 0;
	}

	public int updateEmail(UpdateEmailRequest updateEmailRequest) throws NotFoundException {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				updateEmailRequest.getUsername(), updateEmailRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		Long lId = userDetails.getId();
		if (lId != updateEmailRequest.getUserId()) {
			return 1;
		}
		User u = userRepository.findById(lId).orElseThrow(NotFoundException::new);
		u.setEmail(updateEmailRequest.getEmail());
		userRepository.save(u);
		return 0;
	}

	public int updatePassword(UpdatePasswordRequest updatePasswordRequest) throws NotFoundException {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				updatePasswordRequest.getUsername(), updatePasswordRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		Long lId = userDetails.getId();
		if (lId != updatePasswordRequest.getId()) {
			return 1;
		}
		User u = userRepository.findById(lId).orElseThrow(NotFoundException::new);
		u.setPassword(encoder.encode(updatePasswordRequest.getNewPassword()));
		userRepository.save(u);
		return 0;
	}

	public int resetPassword(ResetPasswordRequest resetPasswordRequest) throws NotFoundException {
		if (!userRepository.existsByEmail(resetPasswordRequest.getEmail())) {
			return 1;
		}
		int leftLimit = 48;
		int rightLimit = 122;
		int targetStringLenght = 10;
		Random random = new Random();
		String generatedPassword = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLenght)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
		String pass = encoder.encode(generatedPassword);
		emailService.sendEmail(resetPasswordRequest.getEmail(), generatedPassword);
		User u = userRepository.findByEmail(resetPasswordRequest.getEmail()).orElseThrow(NotFoundException::new);
		u.setPassword(pass);
		userRepository.save(u);
		return 0;
	}
}
