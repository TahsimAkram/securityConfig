package com.bj.security.entity;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

	private Integer userId;
	private Long organizationId;
	private Long userTypeId;
	private String salutation;
	private String firstName;
	private String lastName;
	private String email;
	private String statusCode;
	private Long createId;
	private LocalDate createDate;
	private Long updateId;
	private LocalDate updateDate;
	private String sourceOrg;
	private String branch;
	private String phone;
	private String mobile;
	private String fax;
	private String address;
	private String userPlatform;
	private String role;
	private String orgName;
}
