package rs.lukamatovic.TaskMinder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.lukamatovic.TaskMinder.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
