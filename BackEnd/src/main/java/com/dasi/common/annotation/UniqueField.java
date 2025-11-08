package com.dasi.common.annotation;

import com.dasi.common.enumeration.ResultInfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueField {
    Class<?> serviceClass();
    String fieldName();
    String idName() default "id";
    ResultInfo resultInfo() default ResultInfo.UNIQUE_FIELD_CONFLICT;
}
