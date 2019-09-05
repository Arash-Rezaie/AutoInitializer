package com.arash.autoinitializer;

import java.lang.reflect.Field;

public final class EmptyInitializer extends AbstractInitializer {
    @Override
    void init(Object obj, Field field, String extraInfo) {

    }
}
