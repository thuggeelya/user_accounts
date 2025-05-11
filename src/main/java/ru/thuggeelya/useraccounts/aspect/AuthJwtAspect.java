package ru.thuggeelya.useraccounts.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.thuggeelya.useraccounts.exception.AccessDeniedException;
import ru.thuggeelya.useraccounts.exception.AuthenticationException;
import ru.thuggeelya.useraccounts.security.utils.JwtUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthJwtAspect {

    private final JwtUtils jwtUtils;

    @Around("execution(* ru.thuggeelya.useraccounts.controller.UserController.*(..))")
    public Object checkUser(final ProceedingJoinPoint joinPoint) throws Throwable {

        final Long id = getUserIdFromRequest(joinPoint);

        if (getRequestAttributes() instanceof ServletRequestAttributes attributes) {

            final String headerAuth = attributes.getRequest().getHeader(AUTHORIZATION);

            final String jwt = jwtUtils.parseJwt(headerAuth);

            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                throw new AuthenticationException("Invalid JWT token");
            }

            if (id != null && !id.equals(jwtUtils.getUserIdFromJwtToken(jwt))) {
                throw new AccessDeniedException("Access denied to endpoint with ID = " + id + " with this JWT token");
            }
        }

        return joinPoint.proceed();
    }

    private Long getUserIdFromRequest(final ProceedingJoinPoint joinPoint) {

        final Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        final Parameter[] params = method.getParameters();
        final Object[] args = joinPoint.getArgs();

        for (int i = 0; i < params.length; i++) {

            if (
                    params[i].isAnnotationPresent(PathVariable.class)
                    && "id".equals(params[i].getAnnotation(PathVariable.class).value())
            ) {
                return (Long)args[i];
            }
        }

        return null;
    }
}
