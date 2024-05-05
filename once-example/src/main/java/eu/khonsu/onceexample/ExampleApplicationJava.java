package eu.khonsu.onceexample;

import android.app.Application;

import eu.khonsu.once.Once;

public class ExampleApplicationJava extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Once.initialise(this);
    }
}
