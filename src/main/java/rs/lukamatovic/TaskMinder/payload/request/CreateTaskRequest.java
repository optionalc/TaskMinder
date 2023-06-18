package rs.lukamatovic.TaskMinder.payload.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTaskRequest {

	@NotBlank
	private String title;
	private String description;
	@NotNull
	private LocalDate dueDate;
	@Min(0)
	@NotBlank
	private int priority;
	private String additionalInfo;
	@NotBlank
	private Long userId;

	public CreateTaskRequest() {

	}

	public CreateTaskRequest(@NotBlank String title, String description, @NotNull LocalDate dueDate,
			@Min(0) @NotBlank int priority, String additionalInfo, @NotBlank Long userId) {
		super();
		this.title = title;
		this.description = description;
		this.dueDate = dueDate;
		this.priority = priority;
		this.additionalInfo = additionalInfo;
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

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
