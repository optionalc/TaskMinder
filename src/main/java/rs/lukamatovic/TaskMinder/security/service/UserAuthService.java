package rs.lukamatovic.TaskMinder.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import rs.lukamatovic.TaskMinder.model.User;
import rs.lukamatovic.TaskMinder.repository.UserRepository;

@Service
public class UserAuthService {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	public Authentication authenticateUser(String username, String password) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				username, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		return authentication;
	}
	
	public boolean validateEmailChange(Authentication authentication, Long requestId, String newEmail) throws NotFoundException {
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		Long lId = userDetails.getId();
		
		if(lId == requestId) {
			User u = userRepository.findById(requestId).orElseThrow(NotFoundException::new);
			u.setEmail(newEmail);
			userRepository.save(u);
			return true;
		}
		return false;
	}
	
	public boolean validatePasswordChange(Authentication authentication, Long requestId, String newPassword) throws NotFoundException {
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		Long lId = userDetails.getId();
		
		if(lId == requestId) {
			User u = userRepository.findById(requestId).orElseThrow(NotFoundException::new);
			u.setPassword(encoder.encode(newPassword));
			userRepository.save(u);
			return true;
		}
		return false;
	}
}
