package com.arash.autoinitializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoInit {
    /**
     * when ever you want to initialize a resource process, pass something in
     * @return some class implementing AbstractInitializer interface
     */
    Class<? extends AbstractInitializer> initializer() default EmptyInitializer.class;

    /**
     * any extra information which is necessary for initializer
     * @return some information such as R.type.someValue
     */
    String initInfo() default "";

    /**
     * by default, variables are going to be saved through class changes unless you pass false for this parameter
     * @return true or false to memorize field values
     */
    boolean memorize() default true;
}
