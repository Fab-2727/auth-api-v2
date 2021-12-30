package authapi02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthApi02Application {

	public static void main(String[] args) {
		SpringApplication.run(AuthApi02Application.class, args);
	}
	
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
	
	public static String getExceptionClassAndMessage(Throwable e) {
		String exceptionClassAndMessage = e.getClass().getSimpleName() + " : " + e.getMessage();
		return exceptionClassAndMessage;
	}

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
