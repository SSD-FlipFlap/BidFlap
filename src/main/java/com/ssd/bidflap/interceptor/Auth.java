package com.ssd.bidflap.interceptor;

import com.ssd.bidflap.domain.enums.MemberRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {

    MemberRole role() default MemberRole.USER;
}