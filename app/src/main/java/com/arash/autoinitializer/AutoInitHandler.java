package com.arash.autoinitializer;

import com.arash.sessionrepository.SessionRepository;
import com.arash.sessionrepository.SessionRepository.Session;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

/**
 * You have face findViewById() or context.getResources().getValueOfSomeThing() methods in Android application programming.
 * Also, some thing which you have to handle usually, is store and restore things during activity changes.<br>
 * The fact is that, They all walk on my nerves. So I created this class to ignore theme all.
 * <p>
 * This class uses SessionRepository (some other of mine libraries) to manage the whole operation.
 * By this way, it stores all field by default, also, you have to clear its cache on exit (Activity, Fragment, Dialog, ...)
 * </p>
 * <p>
 * The idea is to store the whole class in a session (of course I mean its instance), when new instance of the same class comes in, I initialize its fields, then I copy old values into the new one.
 * just that!
 * </p>
 */
public class AutoInitHandler {
    // some strings to use in SessionRepository
    private static final String INITIALIZER_GROUP = "INITIALIZER_GROUP";
    private static final String SESSION_PREFIX = "AUTO_HNDLER_PRFIX_";
    private static final String TARGET_CLASS = "TARGET_CLASS";

    // by this factory I can pick appropriate initializer for each field
    private static InitFactory initFactory;

    static {
        setInitFactory(new InitFactory());
    }

    public static void setInitFactory(InitFactory factory) {
        initFactory = factory;
    }

    public static void init(Object obj) {
        init(obj, 0);
    }

    /**
     * Every thing starts from here.<br>
     * For example, you are writing an Activity. Normally, you overload onCreate() method and find views, initialize theme, ... .<br>
     * Instead all of theme, call this method and pass your Activity instance. It also could be Fragment, FragmentActivity or Dialog.<br?
     * If your Activity extends another one and there are some fields needed to be managed too, pass in 1 as nodeIndex. nodeIndex can be any other positive number including 0 (parents are not important).
     * I will search all parents till I get to Object class. So do not worry, I handle parent fields too.<br>
     * Do not forget to call clearCache() when ever you finished.
     *
     * @param obj       your target class instance
     * @param nodeLevel in case of inheritance, your node level, o.w 0
     */
    public static void init(Object obj, int nodeLevel) {
        Session session = getSession(obj);  // catch a session. If it exists, the old one wil be given

        // all field are managed via their initializer, so, let's obtain a set of initializer at first
        ArrayList arrayList = (ArrayList) session.get(INITIALIZER_GROUP, null);
        if (arrayList == null) {
            arrayList = getInitList(obj, nodeLevel);
            session.put(INITIALIZER_GROUP, arrayList);
        }

        // initialization takes effect here. I call every declared initializer. If it fails, I will print out a text
        int size = arrayList.size() - 1;
        while (size >= 0) {
            try {
                ((AbstractDataInitializer) arrayList.get(size)).init(obj);
                size--;
            } catch (Exception e) {
                String sb = "initializing operation failed for item: " +
                        obj.getClass().getSimpleName() + "." +
                        ((AbstractDataInitializer) arrayList.get(size)).field.getName();
                throw new RuntimeException(sb, e);
            }
        }
    }

    /**
     * call this method, if you want to save your class state. It's so fast. Actually, I only put the given object into a HashMap.Just, that.<br>
     * Usually, you call this at pause point.
     *
     * @param obj your target class instance
     */
    public static void store(Object obj) {
        getSession(obj).put(TARGET_CLASS, obj);
    }

    /**
     * This method restores all annotated fields marked as memorize of the old object into this one. There is no search operation. I did that in init() method.<br>
     * You call this method at resume point usually.
     *
     * @param obj your target class instance
     * @return true if there is some history. At first, no memory exists, so it returns false.
     */
    public static boolean restore(Object obj) {
        Session session = getSession(obj);  // catch a session. If it exists, the old one wil be given
        Object obj2 = session.get(TARGET_CLASS, null);  // catch ininitializers firstly
        ArrayList arrayList = (ArrayList) session.get(INITIALIZER_GROUP, null);
        if (obj2 == null || arrayList == null) {
            return false;
        }

        if (obj != obj2) {
            int size = arrayList.size() - 1;
            while (size >= 0) {
                try {
                    ((AbstractDataInitializer) arrayList.get(size)).doCopy(obj2, obj);   //copy old value into the given object
                    size--;
                } catch (Exception e) {
                    String sb = "copy operation failed for item: " +
                            obj.getClass().getSimpleName() + "." +
                            ((AbstractDataInitializer) arrayList.get(size)).field.getName();
                    throw new RuntimeException(sb, e);
                }
            }
        }
        return true;
    }

    /**
     * clear cache at exit point. Do not forget to call this method, as old instance remains during application life time. So if you call init() method for this class again, you will see old values again.
     *
     * @param obj your target class instance
     */
    public static void clearCache(Object obj) {
        SessionRepository.removeSession(getSession(obj));
    }

    /**
     * This method creates a initializer for all annotated fields
     *
     * @param obj your target class instance
     * @param i   node level
     * @return a list of initializers
     */
    private static ArrayList<AbstractDataInitializer> getInitList(Object obj, int i) {
        ArrayList<Field> arrayList = new ArrayList<>();
        Class cls = obj.getClass();

        // extract all fields
        while (true) if (i > -1 && cls != Object.class) {
            Collections.addAll(arrayList, cls.getDeclaredFields());
            cls = cls.getSuperclass();
            i--;
        } else {
            break;
        }

        // create initializer for all fields
        int size = arrayList.size();
        ArrayList<AbstractDataInitializer> arrayList2 = new ArrayList<>(size);
        for (int i2 = 0; i2 < size; i2++) {
            Field field = arrayList.get(i2);
            if (field.isAnnotationPresent(AutoInit.class)) {
                field.setAccessible(true);
                arrayList2.add(initFactory.getInitializer(field));
            }
        }
        return arrayList2;
    }

    /**
     * get a session for this instance. It there is another one exists, the old one will be returned
     *
     * @param obj your target class instance
     * @return a session
     */
    private static Session getSession(Object obj) {
        String sb = SESSION_PREFIX + obj.getClass().getName();
        return SessionRepository.getSession(sb);
    }
}
