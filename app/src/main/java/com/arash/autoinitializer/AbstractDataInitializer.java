package com.arash.autoinitializer;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * This class defines basic operations needed for initializing fields to their values.
 * Methods are defined package visible as I prefer to collect all my stuff here. By this way others see nothing else those I want
 */
public abstract class AbstractDataInitializer {
    private final static HashMap<Class<? extends AbstractInitializer>, AbstractInitializer> INITIALIZER_CONTAINER = new HashMap<>();
    /**
     * received annotation
     */
    protected final AutoInit annotationInfo;

    /**
     * target field
     */
    final Field field;

    /**
     * Initializing operation needs container object by the way, so I need to catch it right now.
     *
     * @param container container object
     * @throws Exception there is always possibility of failure while initializing. Simply, through exception to stop the operation
     */
    void init(Object container) throws Exception {
        AbstractInitializer mInitializer = INITIALIZER_CONTAINER.get(this.annotationInfo.initializer());
        if (mInitializer == null) {
            mInitializer = this.annotationInfo.initializer().newInstance();
            INITIALIZER_CONTAINER.put(this.annotationInfo.initializer(), mInitializer);
        }
        mInitializer.init(container, this.field, this.annotationInfo.initInfo());
    }

    /**
     * This method is due to make a copy out of obj2.field into obj.field
     *
     * @param srcContainer  source container
     * @param destContainer destination container
     * @param field   target field
     * @throws Exception Just throw an exception to stop the operation
     */
    abstract void copy(Object srcContainer, Object destContainer, Field field) throws Exception;

    /**
     * An initializer should be defined for each field so, I can extract the information out of provided annotation object into the desired field
     *
     * @param field The field you want to handle its value automatically
     */
    AbstractDataInitializer(Field field) {
        this.annotationInfo = field.getAnnotation(AutoInit.class);
        this.field = field;
    }

    /**
     * This is where copy operation executes
     *
     * @param srcContainer  source object
     * @param destContainer destination object
     * @throws Exception take it easy and throw out exception when ever needed
     */
    void doCopy(Object srcContainer, Object destContainer) throws Exception {
        if (this.annotationInfo.memorize()) { //am I allowed to make a copy
            copy(srcContainer, destContainer, this.field);
        }
    }

    /**
     * In case of reading a field value, just call this method with an appropriate container
     *
     * @param container the container
     * @return value of target field
     * @throws Exception well, it may face an exception during the operation
     */
    Object getFieldObject(Object container) throws Exception {
        return this.field.get(container);
    }
}
