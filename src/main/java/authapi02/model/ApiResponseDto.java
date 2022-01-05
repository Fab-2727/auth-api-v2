package authapi02.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_EMPTY)
public class ApiResponseDto {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	private String message;
	private int http_code;
	private String http_status;
	private String description;
	
	// Default constructor
	public ApiResponseDto() {
		super();
	}

	public ApiResponseDto(String message, int http_code, String http_status, String description) {
		super();
		this.message = message;
		this.http_code = http_code;
		this.http_status = http_status;
		this.description = description;
	}
	
	public ApiResponseDto(ErrorDictionary error) {
		super();
		this.message = error.getReason();
		this.http_code = error.getHttpStatus().value();
		this.http_status = error.getHttpStatus().getReasonPhrase();
	}

	/*
	 * More descriptive object.
	 */
	public ApiResponseDto(ErrorDictionary error, String description) {
		super();
		this.message = error.getReason();
		this.http_code = error.getHttpStatus().value();
		this.http_status = error.getHttpStatus().getReasonPhrase();
		this.description = description;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getHttp_code() {
		return http_code;
	}

	public void setHttp_code(int http_code) {
		this.http_code = http_code;
	}

	public String getHttp_status() {
		return http_status;
	}

	public void setHttp_status(String http_status) {
		this.http_status = http_status;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
		return Objects.hash(description, http_code, http_status, message);
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
		return Objects.equals(description, other.description) && http_code == other.http_code
				&& Objects.equals(http_status, other.http_status) && Objects.equals(message, other.message);
	}
}
