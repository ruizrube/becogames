package es.uca.becogames.business.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails, Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private static final long serialVersionUID = -8883789651072229337L;

	@NotEmpty
	private String dni;

	@NotEmpty
	private String firstName;

	@NotEmpty
	private String lastName;
	
	@NotEmpty
	@Column(unique = true)
	private String username;

	@NotEmpty
	private String password;

	@Enumerated(EnumType.STRING)
	@NotNull
	private Role role;

	@NotNull
	@ManyToOne
	private Organization organization;

	@Enumerated(EnumType.STRING)
	@NotNull
	private Gender gender;

	@NotNull
	@Past
	private LocalDate birthDate;

	@Email
	@NotEmpty
	private String mail;

	@NotNull
	private Profile profile;
	
	private Float extroversion;
	private Float cordiality;
	private Float responsibility;
	private Float stability;
	private Float openness;	
	
	private boolean subscription;

	private boolean enabled;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private Set<Player> userGames;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private Set<QuestionnaireResponse> responses;

	public Set<QuestionnaireResponse> getResponses() {
		return responses;
	}

	public void setResponses(Set<QuestionnaireResponse> responses) {
		this.responses = responses;
	}

	public User() {
		this.profile = Profile.NO_DEFINED_PROFILE;
		this.gender = Gender.NO_DEFINED_SEX;
		this.role = Role.Player;	
		this.enabled=false;
	}

	public User(String firstName, String lastName, String username) {
		this();
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
	}

	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
		list.add(new SimpleGrantedAuthority(this.role.name()));
		return list;

	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	
	@Override
	public String toString() {
		return firstName + " " + lastName;
	}

	public int getAge() {
		if (birthDate != null) {
			return Period.between(birthDate, LocalDate.now()).getYears();
		} else {
			return -1;
		}
	}

	public boolean isSubscription() {
		return subscription;
	}

	public void setSubscription(boolean subscription) {
		this.subscription = subscription;
	}

	
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Set<Player> getUserGames() {
		return userGames;
	}

	protected void addUserGame(Player ug) {

		if (userGames == null) {
			userGames = new LinkedHashSet<Player>();
		}
		userGames.add(ug);

		// TODO Auto-generated method stub

	}


	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Float getExtroversion() {
		return extroversion;
	}

	public void setExtroversion(Float extroversion) {
		this.extroversion = extroversion;
	}

	public Float getCordiality() {
		return cordiality;
	}

	public void setCordiality(Float cordiality) {
		this.cordiality = cordiality;
	}

	public Float getResponsibility() {
		return responsibility;
	}

	public void setResponsibility(Float responsibility) {
		this.responsibility = responsibility;
	}

	public Float getStability() {
		return stability;
	}

	public void setStability(Float stability) {
		this.stability = stability;
	}

	public Float getOpenness() {
		return openness;
	}

	public void setOpenness(Float openness) {
		this.openness = openness;
	}


	public Long countInvitedGames() {
		return (Long) this.getUserGames().stream().map(p -> p.getGame()).filter(g -> g.isUserInvited(this)).count();
	}

	public Long countJoinedGames() {

		return (Long) this.getUserGames().stream().map(p -> p.getGame()).filter(g -> g.isUserJoined(this)).count();
	}

}