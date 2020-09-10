package es.uca.becogames.business.dto;

import java.io.Serializable;

public class CommonGoodsGameResult extends GameResult implements Serializable {

	private int ranking;

	private String username;

	private String dni;

	private String firstName;

	private String lastName;

	private String invested;

	private String gained;

	
	public CommonGoodsGameResult() {

	}


	public int getRanking() {
		return ranking;
	}


	public void setRanking(int i) {
		this.ranking = i;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	
	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getInvested() {
		return invested;
	}

	public void setInvested(String invested) {
		this.invested = invested;
	}

	public String getGained() {
		return gained;
	}

	public void setGained(String gained) {
		this.gained = gained;
	}


	@Override
	public String toString() {
		return "CommonGoodsGameResult [ranking=" + ranking + ", username=" + username + ", invested=" + invested
				+ ", gained=" + gained + "]";
	}


}
