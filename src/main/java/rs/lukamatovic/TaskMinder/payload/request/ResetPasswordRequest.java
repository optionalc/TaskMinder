package rs.lukamatovic.TaskMinder.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequest {

	@NotBlank
	@Email
	private String email;

	public ResetPasswordRequest() {

	}

	public ResetPasswordRequest(@NotBlank @Email String email) {
		super();
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
