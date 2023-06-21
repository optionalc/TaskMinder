package rs.lukamatovic.TaskMinder.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

@Service
public class TaskService {
	
	public boolean validate(LocalDate dueDate) {
		if (dueDate.isBefore(LocalDate.now())) {
			return false;
		}
		return true;
	}
}
