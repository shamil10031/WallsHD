package com.shomazzapp.vavilonWalls.Utils;

import android.content.Context;

public final class RoboErrorReporter {

    private RoboErrorReporter() {
    }

    public static void bindReporter(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.inContext(context));
    }

    public static void reportError(Context context, Throwable error) {
        ExceptionHandler.reportOnlyHandler(context).uncaughtException(Thread.currentThread(), error);
    }

}