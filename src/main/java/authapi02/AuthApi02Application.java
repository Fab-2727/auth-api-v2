package authapi02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthApi02Application {

	public static void main(String[] args) {
		SpringApplication.run(AuthApi02Application.class, args);
	}
	
	/**
	 * This method returns a more comprehensive exception log.
	 * The final message contains the exception's class, the message and the file name, as well as the method and the line were the exception occurred.
	 * If the exception has a cause, then it will be logged.
	 * @param e
	 * @return exceptionDescription
	 */
	public static String getExceptionDescriptionForLogging(Throwable e) {
		if (e == null) {
			return "";
		}
		String exceptionDescription = "";
		exceptionDescription = getExceptionClassAndMessage(e) + " " + getExceptionInfo(e);

		String causedBy = "";
		if (e.getCause() != null) {
			causedBy = "[Caused by : " + getExceptionClassAndMessage(e.getCause()) + " " + getExceptionInfo(e.getCause()) + " ]";
		}
		return exceptionDescription + causedBy;
		
	}
	
	/**
	 * Helper method
	 */
	public static String getExceptionClassAndMessage(Throwable e) {
		String exceptionClassAndMessage = e.getClass().getSimpleName() + " : " + e.getMessage();
		return exceptionClassAndMessage;
	}

	
	/**
	 * Helper method
	 */
	public static String getExceptionInfo(Throwable e) {
		if (e == null) {
			return "";
		}
		String exceptionCause = "";
		try {
			exceptionCause = "[at: " + e.getStackTrace()[0].getFileName() + "#" + e.getStackTrace()[0].getMethodName() + ":" + e.getStackTrace()[0].getLineNumber() + "]";
			return exceptionCause;
		} catch (Exception e2) {
			return "";
		}
		
	}
	
}
