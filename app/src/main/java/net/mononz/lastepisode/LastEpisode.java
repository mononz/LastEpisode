package net.mononz.lastepisode;

import android.app.Application;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.mononz.lastepisode.database.Database;

public class LastEpisode extends Application {

    private static GoogleAnalytics analytics;
    private static Tracker mTracker;

    public void onCreate() {
        super.onCreate();

        new Database(this);

        analytics = GoogleAnalytics.getInstance(this);
        mTracker = getDefaultTracker();
        if (mTracker != null) {
            Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                    getDefaultTracker(),                         // Currently used Tracker.
                    Thread.getDefaultUncaughtExceptionHandler(), // Current default uncaught exception handler.
                    this);                                       // Context of the application.
            // Make myHandler the new default uncaught exception handler.
            Thread.setDefaultUncaughtExceptionHandler(myHandler);
        }
    }

    synchronized private static Tracker getDefaultTracker() {
        try {
            if (mTracker == null) {
                mTracker = analytics.newTracker(R.xml.global_tracker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mTracker;
    }

    public static void sendScreen(String screen) {
        if (!BuildConfig.DEBUG) {
            Tracker mTracker = getDefaultTracker();
            if (mTracker != null) {
                mTracker.setScreenName(screen);
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            }
        }
    }

    public static void sendAction(String action) {
        if (!BuildConfig.DEBUG) {
            Tracker mTracker = getDefaultTracker();
            if (mTracker != null) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction(action)
                        .build());
            }
        }
    }

    public static void sendError(String action) {
        if (!BuildConfig.DEBUG) {
            Tracker mTracker = getDefaultTracker();
            if (mTracker != null) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Error")
                        .setAction(action)
                        .build());
            }
        }
    }

}