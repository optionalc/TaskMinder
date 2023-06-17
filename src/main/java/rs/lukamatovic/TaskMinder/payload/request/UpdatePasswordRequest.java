package rs.lukamatovic.TaskMinder.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdatePasswordRequest {

	@NotBlank
	private Long id;
	@NotBlank
	private String username;
	@NotBlank
	private String password;
	@NotBlank
	@Size(min = 6)
	private String newPassword;

	public UpdatePasswordRequest() {

	}

	public UpdatePasswordRequest(@NotBlank Long id, @NotBlank String username, @NotBlank String password,
			@NotBlank @Size(min = 6) String newPassword) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.newPassword = newPassword;
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

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
