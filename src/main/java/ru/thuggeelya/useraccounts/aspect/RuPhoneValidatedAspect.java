package ru.thuggeelya.useraccounts.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static ru.thuggeelya.useraccounts.util.ValidationUtils.normalizeRuPhone;

@Aspect
@Component
public class RuPhoneValidatedAspect {

    @Around("@annotation(RuPhoneValidated)")
    public Object validateAndFormatPhones(final ProceedingJoinPoint joinPoint) throws Throwable {

        final Object[] args = joinPoint.getArgs();

        for (int i = 0; i < args.length; i++) {

            if (args[i] instanceof String s) {
                args[i] = normalizeRuPhone(s);
            }
        }

        return joinPoint.proceed(args);
    }
}