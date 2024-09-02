package sit.int221.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

//@RestControllerAdvice
//public class CustomExceptionHandler {
//    @ExceptionHandler(Exception.class)
//    public ProblemDetail handleSecurityException(Exception ex) {
//        ProblemDetail errorDetail = null;
//        if (ex instanceof BadCredentialsException) {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
//            errorDetail.setProperty("access_denied_reason", "Authentication Failure");
//        }

//        if (ex instanceof AccessDeniedException) {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
//            errorDetail.setProperty("access_denied_reason", "not_authorized");
//        }

//        tempered
//        if (ex instanceof SignatureException) {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
//            errorDetail.setProperty("access_denied_reason", "JWT signature not valid");
//        }
//
////        expired
//        if (ex instanceof ExpiredJwtException) {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
//            errorDetail.setProperty("access_denied_reason", "JWT token already expired!!");
//        }
//
//        if (ex instanceof MalformedJwtException) {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
//            errorDetail.setProperty("access_denied_reason", "Malformed JWT token");
//        }
//        return errorDetail;

//    @ExceptionHandler(SignatureException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex, WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.UNAUTHORIZED.value(),
//                "JWT signature not valid",
//                request.getDescription(false)
//        );
//        errorResponse.addValidationError("access_denied_reason", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
//    }
//    }
//}



