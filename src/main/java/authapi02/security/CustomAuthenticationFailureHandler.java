package authapi02.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import authapi02.model.ApiResponseDto;
import authapi02.model.ErrorDictionary;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request, 
			HttpServletResponse response,
			AuthenticationException exception) 
			throws IOException, ServletException 
	{
		ApiResponseDto errorRsp = new ApiResponseDto(ErrorDictionary.BAD_CREDENTIALS);
		System.out.println("ACA");
		response.setStatus(errorRsp.getHttpCode());
		response.getOutputStream().println(errorRsp.toString());
	}

	
}
