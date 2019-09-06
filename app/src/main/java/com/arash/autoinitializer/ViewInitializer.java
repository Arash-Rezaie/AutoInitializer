package com.arash.autoinitializer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import androidx.fragment.app.Fragment;

import java.lang.reflect.Field;

public class ViewInitializer implements Initializer {

    @Override
    public void init(Object containerObj, Field targetField, String initInfo) throws Exception {
        targetField.set(containerObj, findView(containerObj, Integer.valueOf(initInfo)));
    }

    private View findView(Object obj, int initInfo) throws Exception {
        return getViewById(initInfo, obj);
    }

    private View getViewById(int id, Object obj) throws Exception {
        View rootViewFromContainerInstance = getRootViewFromContainerInstance(obj);
        if (rootViewFromContainerInstance != null) {
            return rootViewFromContainerInstance.findViewById(id);
        }
        return null;
    }

    @SuppressLint({"NewApi"})
    private View getRootViewFromContainerInstance(Object obj) throws Exception {
        if (obj instanceof Activity) {
            return ((Activity) obj).getWindow().getDecorView().getRootView();
        }
        if (obj instanceof Fragment) {
            return ((Fragment) obj).getView();
        }
        if (obj instanceof Dialog) {
            return ((Dialog) obj).getWindow().getDecorView();
        }
        if (obj instanceof android.app.Fragment) {
            return ((android.app.Fragment) obj).getView();
        }
        throw new Exception("sorry I could not fetch the root view. Because your target object is not any kind of Activity, Fragment or Dialog");
    }
}
