package com.bj.security.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.bj.security.dto.CustomResponseObject;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@ExceptionHandler(value = Exception.class)	
	public @ResponseBody CustomResponseObject handleException(Exception ex) {
		ex.printStackTrace();
		logger.error("Error occurred for ", ex.getMessage());
		return new CustomResponseObject(Long.parseLong(HttpStatus.INTERNAL_SERVER_ERROR.series().value() + ""),
				Long.parseLong(HttpStatus.INTERNAL_SERVER_ERROR.value() + ""),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage());
	}

}
