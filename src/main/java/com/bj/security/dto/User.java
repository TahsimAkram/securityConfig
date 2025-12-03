package com.bj.security.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements UserDetails{
	private Integer userId;
	private String userlogin;
	private String password;
	private String salutation;
	private String firstname;
	private String lastname;
	private String email;
	private String mobile;
	private String role;
	//private OrgTypeEnum orgType;
	private Collection<String> userorgroles;
	private int applicantusrid;
	private Integer organizationid;
	private String organizationname;
	private String tokenValue;

	/*@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (role == null) {
			return Collections.emptyList();
		}

		return List.of(new SimpleGrantedAuthority(role.name()));
	}*/

	public Collection<? extends GrantedAuthority> getAuthorities() {
		final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		if (CollectionUtils.isEmpty(userorgroles)) {
			return Collections.emptyList();
		}else {
			userorgroles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
		}
		return authorities;
	}

	@Override
	public String getUsername() {

		return email;
	}
	@Override
	public boolean isAccountNonExpired() {

		return true;
	}
	@Override
	public boolean isAccountNonLocked() {

		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}
	@Override
	public boolean isEnabled() {

		return true;
	}
}

