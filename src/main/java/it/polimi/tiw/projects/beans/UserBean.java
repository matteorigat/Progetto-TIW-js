package it.polimi.tiw.projects.beans;

public class UserBean {

	private int id;
	private String username;

	private String name;
	private String surname;

	private String email;

	private String password;

	private Boolean checked = false; // quando creo una conference e seleziono più utenti del dovuto la pagina di creazione conferenza si riaggiorna dandomi errore ma gli utenti che avevo selezionato rimangono selezionati perchè checked

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
}
