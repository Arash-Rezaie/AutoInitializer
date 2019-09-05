package com.arash.autoinitializer;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;

public class InitFactory {
    public AbstractDataInitializer getInitializer(Field field){
        Class type = field.getType();
        if (CheckBox.class.isAssignableFrom(type)) {
            return new AbstractDataInitializer(field) {
                @Override
                void copy(Object srcContainer, Object destContainer, Field field) throws Exception {
                    ((CheckBox) getFieldObject(destContainer)).setChecked(((CheckBox) getFieldObject(srcContainer)).isChecked());
                }
            };
        }
        if (RadioGroup.class.isAssignableFrom(type)) {
            return new AbstractDataInitializer(field) {
                @Override
                void copy(Object srcContainer, Object destContainer, Field field) throws Exception {
                    ((RadioButton) ((RadioGroup) getFieldObject(destContainer)).findViewById(((RadioGroup) getFieldObject(srcContainer)).getCheckedRadioButtonId())).setChecked(true);
                }
            };
        }
        if (Spinner.class.isAssignableFrom(type)) {
            return new AbstractDataInitializer(field) {
                @Override
                void copy(Object srcContainer, Object destContainer, Field field) throws Exception {
                    ((Spinner) getFieldObject(destContainer)).setSelection(((Spinner) getFieldObject(srcContainer)).getSelectedItemPosition());
                }
            };
        }
        if (TextView.class.isAssignableFrom(type)) {
            return new AbstractDataInitializer(field) {
                @Override
                void copy(Object srcContainer, Object destContainer, Field field) throws Exception {
                    ((TextView) getFieldObject(destContainer)).setText(((TextView) getFieldObject(srcContainer)).getText());
                }
            };
        }
        if (ImageView.class.isAssignableFrom(type)) {
            return new AbstractDataInitializer(field) {
                @Override
                void copy(Object srcContainer, Object destContainer, Field field) throws Exception {
                    ((ImageView) getFieldObject(destContainer)).setImageDrawable(((ImageView) getFieldObject(srcContainer)).getDrawable());
                }
            };
        }
        if (View.class.isAssignableFrom(type)) {
            return new AbstractDataInitializer(field) {
                @Override
                void copy(Object srcContainer, Object destContainer, Field field) {
                }
            };
        }
        return new AbstractDataInitializer(field) {
            void copy(Object srcContainer, Object destContainer, Field field) throws Exception {
                field.set(destContainer, getFieldObject(srcContainer));
            }
        };
    }
}
