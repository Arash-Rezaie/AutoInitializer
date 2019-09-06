package com.arash.autoinitializer;

import java.lang.reflect.Field;

public final class EmptyInitializer implements Initializer {

    @Override
    public void init(Object containerObj, Field targetField, String initInfo) throws Exception {

    }
}
