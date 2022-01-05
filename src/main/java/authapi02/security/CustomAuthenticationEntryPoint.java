package authapi02.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import authapi02.model.ApiResponseDto;
import authapi02.model.ErrorDictionary;


/**
 * Note that org.springframework.security.web.access.ExceptionTranslationFilter#handleSpringSecurityException 
 * <br>
 * delegates to AuthenticationEntryPoint only for AuthenticationException and AccessDeniedException
 * @author fab
 *
 */
@Component("CustomAuthenticationEntryPoint")
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	
	//TODO: deshardcode attributes from request. See AuthorizationFilter.

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + "localhost" + "\"");
		
		System.out.println(authException.getClass().getSimpleName());
		System.out.println(authException.getMessage());
		
		// Handling exceptions from AuthorizationFilter
		Object tokenExpiredAttr = request.getAttribute("EXPIRED");
		if (tokenExpiredAttr != null) {
			ApiResponseDto errorDto = new ApiResponseDto(ErrorDictionary.TOKEN_HAS_EXPIRED,(String) tokenExpiredAttr);
			response.setStatus(errorDto.getHttp_code());
			response.getOutputStream().println(errorDto.toString());
		}
		
		Object malFormedTokenAttr = request.getAttribute("MALFORMED_TOKEN");
		if (malFormedTokenAttr != null) {
			ApiResponseDto errorDto = new ApiResponseDto(ErrorDictionary.MALFORMED_TOKEN);
			response.setStatus(errorDto.getHttp_code());
			response.getOutputStream().println(errorDto.toString());
		}
		// END Handling exceptions from AuthorizationFilter
		
		// Handling exceptions from AuthenthicationFilter
		Object badCredentialsAttr = request.getAttribute("BAD_CREDENTIALS");
		if (badCredentialsAttr != null) {
			ApiResponseDto errorDto = new ApiResponseDto(ErrorDictionary.BAD_CREDENTIALS);
			response.setStatus(errorDto.getHttp_code());
			response.getOutputStream().println(errorDto.toString());
		}
		
		Object emptyBodyAttr = request.getAttribute("EMPTY_BODY");
		if (emptyBodyAttr != null) {
			ApiResponseDto errorDto = new ApiResponseDto(ErrorDictionary.BAD_REQUEST_EMPTY_BODY);
			response.setStatus(errorDto.getHttp_code());
			response.getOutputStream().println(errorDto.toString());
		}
		
		Object unknownUserAttr = request.getAttribute("UNKNOWN_USER");
		if (unknownUserAttr != null) {
			ApiResponseDto errorDto = new ApiResponseDto(ErrorDictionary.BAD_CREDENTIALS);
			response.setStatus(errorDto.getHttp_code());
			response.getOutputStream().println(errorDto.toString());
		}
		// END Handling exceptions from AuthenthicationFilter

	}
}
