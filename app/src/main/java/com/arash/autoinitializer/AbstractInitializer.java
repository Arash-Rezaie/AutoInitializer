package com.arash.autoinitializer;

import java.lang.reflect.Field;

/**
 * Some times you need to initialize a field as you wish, then you may pass in an initializer.<br>
 * For example, you are whiling to bind a TextView to a field and some extra changes in color, type face,... are needed. There are 2 ways to handle this.<br>
 * 1. Extend TextView (recommended), 2.Do it some where in your activity or dialog or what ever.<br>
 * If I were in your shoes and I wanted to go with the second strategy, I would use this class
 */
public abstract class AbstractInitializer {
    public AbstractInitializer() {
    }

    abstract void init(Object obj, Field field, String initInfo) throws Exception;
}
