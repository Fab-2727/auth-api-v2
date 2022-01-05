package authapi02.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum ErrorDictionary {

	/**
	 * Exception when the given token has already expired.<br>
	 * <strong>Data:</strong><br>
	 * ID: 1 <br>
	 * HttpStatus: 401 Unauthorized.<br>
	 * Message: The token has expired.<br>
	 */
	TOKEN_HAS_EXPIRED(1, HttpStatus.UNAUTHORIZED, "The token has expired."),
	
	/**
	 * When the given token is malformed.<br>
	 * <strong>Data:</strong><br>
	 * ID: 2 <br>
	 * HttpStatus: 404 Bad Request.<br>
	 * Message: The token is malformed.<br>
	 */
	MALFORMED_TOKEN(2, HttpStatus.BAD_REQUEST, "The token is malformed."),


	/**
	 * An exceptions has occurred during loging due to bad credentials.<br>
	 * <strong>Data:</strong><br>
	 * ID: 3 <br>
	 * HttpStatus: 401 Unauthorized.<br>
	 * Message: Bad credentials.<br>
	 */
	BAD_CREDENTIALS(3, HttpStatus.UNAUTHORIZED, "Bad credentials."),
	
	/**
	 * An exception has occurred due that the payload is empty.<br>
	 * <strong>Data:</strong><br>
	 * ID: 4 <br>
	 * HttpStatus: 400 Bad Request.<br>
	 * Message: The payload is unprocessable due that is empty.<br>
	 */
	BAD_REQUEST_EMPTY_BODY(4, HttpStatus.BAD_REQUEST, "The payload is unprocessable due that is empty."),
	
	/**
	 * Thrown if an authentication request is rejected because the account is disabled.
	 * 
	 * <strong>Data:</strong><br>
	 * ID: 5 <br>
	 * HttpStatus: 401 Unauthorized.<br>
	 * Message: Authentication request rejected because the account is disabled.<br>
	 */
	DISABLED_ACCOUNT(5, HttpStatus.UNAUTHORIZED, "Authentication request rejected because the account is disabled."),
	
	/**
	 * Thrown if an authentication request is rejected because the account is locked.
	 * <strong>Data:</strong><br>
	 * ID: 6 <br>
	 * HttpStatus: 401 Unauthorized.<br>
	 * Message: Authentication request rejected because the account is locked.<br>
	 */
	LOCKED_ACCOUNT(6, HttpStatus.UNAUTHORIZED, "Authentication request rejected because the account is locked."),
	
	/**
	 * Default message when an unexpected error has happened.
	 * 
	 * <strong>Data:</strong><br>
	 * ID: 7 <br>
	 * HttpStatus: 500 Internal Server Error.<br>
	 * Message: An unexpected error has happened.<br>
	 */
	UNEXPECTED_ERROR(7, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error has happened."),
	
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
