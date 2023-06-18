package rs.lukamatovic.TaskMinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.lukamatovic.TaskMinder.model.Task;
import rs.lukamatovic.TaskMinder.model.User;
import rs.lukamatovic.TaskMinder.payload.request.CreateTaskRequest;
import rs.lukamatovic.TaskMinder.payload.response.MessageResponse;
import rs.lukamatovic.TaskMinder.repository.TaskRepository;
import rs.lukamatovic.TaskMinder.repository.UserRepository;
import rs.lukamatovic.TaskMinder.security.service.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/task")
public class TaskController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	UserRepository userRepository;

	@PostMapping("/create")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> createTask(@RequestBody CreateTaskRequest createTaskRequest) throws NotFoundException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		Long idOfLoggedInUser = userDetails.getId();
		
		if (idOfLoggedInUser != createTaskRequest.getUserId()) {
			return ResponseEntity.badRequest().body(new MessageResponse("You can create task only for yourself!"));
		}
		
		User user = userRepository.findById(idOfLoggedInUser).orElseThrow(NotFoundException::new);
		Task task = new Task(createTaskRequest.getTitle(), createTaskRequest.getDescription(),
				createTaskRequest.getDueDate(), createTaskRequest.getPriority(), createTaskRequest.getAdditionalInfo(),
				user);
		taskRepository.save(task);
		return ResponseEntity.ok(new MessageResponse("Task created successfully!"));
	}
}
