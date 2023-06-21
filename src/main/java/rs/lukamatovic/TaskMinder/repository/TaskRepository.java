package rs.lukamatovic.TaskMinder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.lukamatovic.TaskMinder.model.Task;
import rs.lukamatovic.TaskMinder.model.User;

public interface TaskRepository extends JpaRepository<Task, Long> {
	List<Task> findByUser(User user);
	Optional<Task> findByIdAndUser_Id(Long taskId, Long userId);
}
