package authapi02.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum ErrorDictionary {

	TOKEN_HAS_EXPIRED(1, HttpStatus.UNAUTHORIZED, "The token has expired."),
	
	MALFORMED_TOKEN(2, HttpStatus.BAD_REQUEST, "The token is malformed."),

	//AUTHehtication filter
	BAD_CREDENTIALS(3, HttpStatus.UNAUTHORIZED, "Bad credentials."),
	
	BAD_REQUEST_EMPTY_BODY(4, HttpStatus.BAD_REQUEST, "The payload is unprocessable due that is empty."),
	
	;
	private int idError;
	private String reason;
	private HttpStatus httpStatus;
	
	ErrorDictionary(int idError, HttpStatus httpStatus, String reason ) {
		this.idError = idError;
		this.httpStatus = httpStatus;
		this.reason = reason;
	}
	
	public int getIdError () {
		return this.idError;
	}
	public String getReason() {
		return this.reason;
	}
	
	public HttpStatus getHttpStatus () {
		return this.httpStatus;
	}
	
	public ResponseEntity<ApiResponseDto> getResponseEntity() {
		return ResponseEntity.status(this.getHttpStatus()).body(new ApiResponseDto(this));
	}
	
	public ResponseEntity<String> getResponseEntityStr() {
		return ResponseEntity.status(this.getHttpStatus()).body(new ApiResponseDto(this).toString());
	}
	
}
