package rs.lukamatovic.TaskMinder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.lukamatovic.TaskMinder.payload.request.CreateTaskRequest;
import rs.lukamatovic.TaskMinder.payload.request.UpdateTaskRequest;
import rs.lukamatovic.TaskMinder.payload.response.MessageResponse;
import rs.lukamatovic.TaskMinder.repository.TaskRepository;
import rs.lukamatovic.TaskMinder.repository.UserRepository;
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
		int errCode = taskService.createTask(createTaskRequest);

		if (errCode == 1) {
			return ResponseEntity.badRequest().body(new MessageResponse("You can create task only for yourself!"));
		}

		if (errCode == 2) {
			return ResponseEntity.badRequest()
					.body(new MessageResponse("You can create tasks with due date that is ahead of today!"));
		}

		return ResponseEntity.ok(new MessageResponse("Task created successfully!"));
	}

	@PostMapping("/update")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> updateTask(@RequestBody UpdateTaskRequest updateTaskRequest) throws NotFoundException {
		int errCode = taskService.updateTask(updateTaskRequest);

		if (errCode == 1) {
			return ResponseEntity.badRequest().body(new MessageResponse("You can only update task that belong to you"));
		}

		if (errCode == 2) {
			return ResponseEntity.badRequest().body(new MessageResponse("Task not found, check your entries!"));
		}

		if (errCode == 3) {
			return ResponseEntity.badRequest().body(new MessageResponse("That task is connected with another user!"));
		}
		return ResponseEntity.ok(new MessageResponse("Task updated successfully"));
	}
}
