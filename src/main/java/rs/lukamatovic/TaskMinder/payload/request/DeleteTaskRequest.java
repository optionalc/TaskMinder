package rs.lukamatovic.TaskMinder.payload.request;

import jakarta.validation.constraints.NotBlank;

public class DeleteTaskRequest {

	@NotBlank
	private Long userId;
	@NotBlank
	private Long taskId;

	public DeleteTaskRequest() {

	}

	public DeleteTaskRequest(@NotBlank Long userId, @NotBlank Long taskId) {
		super();
		this.userId = userId;
		this.taskId = taskId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

}
