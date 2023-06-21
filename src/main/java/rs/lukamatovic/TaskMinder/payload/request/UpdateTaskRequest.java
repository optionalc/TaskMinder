package rs.lukamatovic.TaskMinder.payload.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public class UpdateTaskRequest {

	@NotBlank
	private Long taskId;
	@NotBlank
	private Long userId;
	private String title;
	private String description;
	private LocalDate dueDate;
	private int priority;

	public UpdateTaskRequest() {

	}

	public UpdateTaskRequest(@NotBlank Long taskId, @NotBlank Long userId, String title, String description,
			LocalDate dueDate, int priority) {
		super();
		this.taskId = taskId;
		this.userId = userId;
		this.title = title;
		this.description = description;
		this.dueDate = dueDate;
		this.priority = priority;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
