package rs.lukamatovic.TaskMinder.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import rs.lukamatovic.TaskMinder.model.Task;
import rs.lukamatovic.TaskMinder.model.User;
import rs.lukamatovic.TaskMinder.payload.request.CreateTaskRequest;
import rs.lukamatovic.TaskMinder.payload.request.DeleteTaskRequest;
import rs.lukamatovic.TaskMinder.payload.request.UpdateTaskRequest;
import rs.lukamatovic.TaskMinder.repository.TaskRepository;
import rs.lukamatovic.TaskMinder.repository.UserRepository;
import rs.lukamatovic.TaskMinder.security.service.UserDetailsImpl;

@Service
public class TaskService {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TaskRepository taskRepository;

	public int createTask(CreateTaskRequest createTaskRequest) throws NotFoundException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		Long idOfLoggedInUser = userDetails.getId();

		if (idOfLoggedInUser != createTaskRequest.getUserId()) {
			return 1;
		}

		if (createTaskRequest.getDueDate().isBefore(LocalDate.now())) {
			return 2;
		}

		User user = userRepository.findById(idOfLoggedInUser).orElseThrow(NotFoundException::new);
		Task task = new Task(createTaskRequest.getTitle(), createTaskRequest.getDescription(),
				createTaskRequest.getDueDate(), createTaskRequest.getPriority(), createTaskRequest.getAdditionalInfo(),
				user);
		taskRepository.save(task);
		return 0;
	}

	public int updateTask(UpdateTaskRequest updateTaskRequest) throws NotFoundException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		Long loggedInId = userDetails.getId();

		if (loggedInId != updateTaskRequest.getUserId()) {
			return 1;
		}

		if (!taskRepository.existsById(updateTaskRequest.getTaskId())) {
			return 2;
		}

		User user = userRepository.findById(loggedInId).orElseThrow(NotFoundException::new);

		if (taskRepository.findByUser(user).isEmpty()) {
			return 3;
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
		return 0;
	}

	public int deleteTask(DeleteTaskRequest deleteTaskRequest) throws NotFoundException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		Long loggedInUserId = userDetails.getId();

		if (loggedInUserId != deleteTaskRequest.getUserId()) {
			return 1;
		}

		if (!taskRepository.existsById(deleteTaskRequest.getTaskId())) {
			return 2;
		}

		User user = userRepository.findById(loggedInUserId).orElseThrow(NotFoundException::new);
		if (taskRepository.findByUser(user).isEmpty()) {
			return 3;
		}

		Task task = taskRepository.findByIdAndUser_Id(deleteTaskRequest.getTaskId(), loggedInUserId)
				.orElseThrow(NotFoundException::new);

		taskRepository.delete(task);
		return 0;
	}
}
