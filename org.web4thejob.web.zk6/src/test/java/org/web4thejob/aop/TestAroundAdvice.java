/*
 * Copyright (c) 2012-2013 Veniamin Isaias.
 *
 * This file is part of web4thejob.
 *
 * Web4thejob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Web4thejob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with web4thejob.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.web4thejob.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Aspect
@Component
public class TestAroundAdvice {

    @Pointcut("execution(* org.web4thejob..*.*(..))")
    public void myMethods() {
    }

    @Around("myMethods()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        // final Signature sig = pjp.getSignature();
        // final String method = sig.getDeclaringType().getName() + "."
        // + sig.getName();
        //
        // System.out
        // .println("-----------------------------------------------------");

        return pjp.proceed();
    }

}
