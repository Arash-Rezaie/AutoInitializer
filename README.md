# AutoInitializer
This module enables you to initialize a field easily. Also, it delivers values to new class instance too.

I hate to write 'findViewById(R.id....)' down repeatedly. It becomes worse when I have to apply my theme to a view every time instead of extending a class. You think it is not disgusting enough? 
Add onSaveInstance and onRestore mechanism too. It seems that the class does not stop growing. :confounded:

So I wrote this module to ease the process :relieved: :smirk:

## How to use this library
1.Download module, unzip it and add it to your project as gradle module

2.We can initialize any field, in This example let's say we have an xml such is below:
```xml

<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
        
        ...

</androidx.constraintlayout.widget.ConstraintLayout>

```
now we want to bind it to a field in class. There is a class named ViewInitializer for binding view to resource id:
```java

@AutoInit(initializer = ViewInitializer.class, initInfo = R.id.textView + "", memorize = false) //memorize is true by default
private TextView textView;

@AutoInit
private List<Integer> nums; // all data type can be used 

```
so we can pass view initializer and view id via annotation.
 
3.Now, we have to invoke init method:
```java

AutoInitHandler.init(container class); // usually it is 'this'

```
If you are willing to save data, memorize parameter in annotation must be set true. Then call store and restore methods.
```java

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    AutoInitHandler.init(this);
    AutoInitHandler.restore(this);
}

@Override
protected void onPause() {
    super.onPause();
    if(isFinishing())
        AutoInitHandler.clearCache(this);
    else
        AutoInitHandler.store(this);
}

```
>restore method can be called after init or in onResume() method and store can get called in onPause or on dismiss

store() method pushes the mentioned object into a session. On Restroe(), all annotated fields with memorize flag, get filled by the last value in the session and the method returns true. If there is no historey, then nothing happens and the method returns false.

__Do not forget to clear cache when your class is going to be finished.__

### More customization
There is a class called AbstractInitializer. By extending this class you can make your own initializer. This can be adopted to any kind of data type.

Restore() method copies old value of the target object into the new one, If you have a custome view, then there is no copy operation suitable for you. You can extend InitializerFactory class and pass it to AutoInitHandler once when ever you like. When it comes to initialization, your initializer class will be triggered and initInfo (defined by annotation) will be passed over, then target filled will be initiated as you want.
