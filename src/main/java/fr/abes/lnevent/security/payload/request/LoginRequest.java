package fr.abes.lnevent.security.payload.request;

import javax.validation.constraints.NotBlank;

public class LoginRequest {

	@NotBlank
	private String login; //siren

	@NotBlank
	private String password;

	public String getLogin() {
		return login;
	}

	public void setUsername(String username) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
