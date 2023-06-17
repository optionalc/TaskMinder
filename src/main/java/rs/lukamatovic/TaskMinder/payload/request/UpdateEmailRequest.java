package rs.lukamatovic.TaskMinder.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateEmailRequest {

	@NotBlank
	private Long userId;
	@NotBlank
	private String username;
	@NotBlank
	private String password;
	@NotBlank
	@Email
	private String email;

	public UpdateEmailRequest() {

	}

	public UpdateEmailRequest(@NotBlank Long userId, @NotBlank String username, @NotBlank String password,
			@NotBlank @Email String email) {
		super();
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
