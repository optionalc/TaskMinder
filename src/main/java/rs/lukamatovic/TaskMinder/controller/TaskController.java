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
import rs.lukamatovic.TaskMinder.payload.request.UpdateTaskRequest;
import rs.lukamatovic.TaskMinder.payload.response.MessageResponse;
import rs.lukamatovic.TaskMinder.repository.TaskRepository;
import rs.lukamatovic.TaskMinder.repository.UserRepository;
import rs.lukamatovic.TaskMinder.security.service.UserDetailsImpl;
import rs.lukamatovic.TaskMinder.service.TaskService;

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

	@Autowired
	TaskService taskService;

	@PostMapping("/create")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> createTask(@RequestBody CreateTaskRequest createTaskRequest) throws NotFoundException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		Long idOfLoggedInUser = userDetails.getId();

		if (idOfLoggedInUser != createTaskRequest.getUserId()) {
			return ResponseEntity.badRequest().body(new MessageResponse("You can create task only for yourself!"));
		}

		if (!taskService.validate(createTaskRequest.getDueDate())) {
			return ResponseEntity.badRequest()
					.body(new MessageResponse("You can create tasks with due date that was't in history!"));
		}
		User user = userRepository.findById(idOfLoggedInUser).orElseThrow(NotFoundException::new);
		Task task = new Task(createTaskRequest.getTitle(), createTaskRequest.getDescription(),
				createTaskRequest.getDueDate(), createTaskRequest.getPriority(), createTaskRequest.getAdditionalInfo(),
				user);
		taskRepository.save(task);
		return ResponseEntity.ok(new MessageResponse("Task created successfully!"));
	}

	@PostMapping("/update")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> updateTask(@RequestBody UpdateTaskRequest updateTaskRequest) throws NotFoundException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		Long loggedInId = userDetails.getId();
		if (loggedInId != updateTaskRequest.getUserId()) {
			return ResponseEntity.badRequest()
					.body(new MessageResponse("You can only update task that belongs to you!"));
		}

		if (!taskRepository.existsById(updateTaskRequest.getTaskId())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Task not found, check your entries!"));
		}

		User user = userRepository.findById(loggedInId).orElseThrow(NotFoundException::new);

		if (taskRepository.findByUser(user).isEmpty()) {
			return ResponseEntity.badRequest().body(new MessageResponse("That task does not belong to that user!"));
		}

		Task task = taskRepository.findByIdAndUser_Id(updateTaskRequest.getTaskId(), loggedInId)
				.orElseThrow(NotFoundException::new);

		if (!updateTaskRequest.getTitle().isEmpty() || !updateTaskRequest.getTitle().isBlank()) {
			task.setTitle(updateTaskRequest.getTitle());
		}

		if (!updateTaskRequest.getDescription().isEmpty() || !updateTaskRequest.getDescription().isBlank()) {
			task.setDescription(updateTaskRequest.getDescription());
		}

		if (updateTaskRequest.getDueDate() != null) {
			task.setDueDate(updateTaskRequest.getDueDate());
		}

		if (updateTaskRequest.getPriority() >= 0) {
			task.setPriority(updateTaskRequest.getPriority());
		}

		taskRepository.save(task);
		return ResponseEntity.ok(new MessageResponse("Task updated successfully"));
	}
}
