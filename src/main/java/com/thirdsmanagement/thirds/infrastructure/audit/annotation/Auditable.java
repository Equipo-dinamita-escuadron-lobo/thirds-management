package com.thirdsmanagement.thirds.infrastructure.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    OperationType operationType();

    String moduleName() default "THIRDS";

    String affectedTable();
    
    int idArgIndex() default 0;
    
    int enterpriseIdArgIndex() default 1;
    
}
