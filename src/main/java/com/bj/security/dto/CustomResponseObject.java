package com.bj.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomResponseObject {

	private Long responseId;
	private Long responseCode;
	private String responseMessage;
	private String responseDescription;

}