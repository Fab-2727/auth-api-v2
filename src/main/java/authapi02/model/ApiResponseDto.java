package authapi02.model;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiResponseDto {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	private String message;
	private String httpStatus;
	private int httpCode;
	
	public ApiResponseDto(ErrorDictionary error) {
		super();
		this.message = error.getReason();
		this.httpCode = error.getHttpStatus().value();
		this.httpStatus = error.getHttpStatus().getReasonPhrase();
		
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}
	public int getHttpCode() {
		return httpCode;
	}
	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}
	
	@Override
	public String toString() {
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(httpCode, httpStatus, message);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApiResponseDto other = (ApiResponseDto) obj;
		return httpCode == other.httpCode && Objects.equals(httpStatus, other.httpStatus)
				&& Objects.equals(message, other.message);
	}
	
}
