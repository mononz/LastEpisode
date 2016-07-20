package net.mononz.lastepisode.library;

import java.text.SimpleDateFormat;

public class Constants {

    public static final int MAX_SEASONS = 1000;
    public static final int MAX_EPISODES = 1000;

    private static final int TEN = 10;

    public static String makeDoubleDigit(int digit) {
        return (digit < TEN) ? "0" + digit : "" + digit;
    }

    public static long getSystemTime() {
        return System.currentTimeMillis()/1000;
    }

    public static String formattedTimestamp(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE',' MMMM dd',' yyyy h:mm a", java.util.Locale.getDefault());
        return format.format(timestamp * 1000);
    }

}