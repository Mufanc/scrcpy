package com.genymobile.scrcpy;

import android.annotation.TargetApi;
import android.content.AttributionSource;
import android.content.Context;
import android.content.MutableContextWrapper;
import android.os.Binder;
import android.os.Build;
import android.os.Process;

import org.joor.Reflect;

public final class FakeContext extends MutableContextWrapper {

    public static final String PACKAGE_NAME = "com.android.shell";
    public static final int ROOT_UID = 0; // Like android.os.Process.ROOT_UID, but before API 29

    private static final FakeContext INSTANCE = new FakeContext();

    private static final Binder sWindowToken = new Binder();

    public static FakeContext get() {
        return INSTANCE;
    }

    private FakeContext() {
        super(null);
    }

    @Override
    public String getPackageName() {
        return PACKAGE_NAME;
    }

    @Override
    public String getOpPackageName() {
        return PACKAGE_NAME;
    }

    @TargetApi(Build.VERSION_CODES.S)
    @Override
    public AttributionSource getAttributionSource() {
        AttributionSource.Builder builder = new AttributionSource.Builder(Process.SHELL_UID);
        builder.setPackageName(PACKAGE_NAME);
        return builder.build();
    }

    // @Override to be added on SDK upgrade for Android 14
    @SuppressWarnings("unused")
    public int getDeviceId() {
        return 0;
    }

    @Override
    public Object getSystemService(String name) {
        Object service = super.getSystemService(name);

        if (name.equals(Context.WINDOW_SERVICE)) {
            Reflect.on(service).call("setDefaultToken", sWindowToken);
        }

        return service;
    }
}
