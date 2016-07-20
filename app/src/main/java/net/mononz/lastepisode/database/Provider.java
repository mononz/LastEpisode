package net.mononz.lastepisode.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class Provider extends ContentProvider {

    public static final String CONTENT_AUTHORITY = "net.mononz.lastepisode";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private Database mOpenHelper;

    private static final int SHOWS = 100;
    private static final int SHOWS_BY_ID = 200;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, Database.shows.TABLE_NAME, SHOWS);
        matcher.addURI(authority, Database.shows.TABLE_NAME + "/#", SHOWS_BY_ID);
        return matcher;
    }

    @Override
    public boolean onCreate(){
        mOpenHelper = new Database(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri){
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHOWS:
                return Database.shows.CONTENT_DIR_TYPE;
            case SHOWS_BY_ID:
                return Database.shows.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor retCursor;
        Log.d("query", uri.toString());
        switch (sUriMatcher.match(uri)) {
            case SHOWS:
                SQLiteQueryBuilder HEROES = new SQLiteQueryBuilder();
                HEROES.setTables(Database.shows.TABLE_NAME);
                retCursor = HEROES.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SHOWS_BY_ID:
                SQLiteQueryBuilder HEROES_BY_ID = new SQLiteQueryBuilder();
                HEROES_BY_ID.setTables(Database.shows.TABLE_NAME);
                HEROES_BY_ID.appendWhere(Database.shows.id + " = " + uri.getLastPathSegment());
                retCursor = HEROES_BY_ID.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case SHOWS:
                long HEROES_ID = db.insert(Database.shows.TABLE_NAME, null, values);
                if (HEROES_ID > 0) {
                    returnUri = Database.shows.buildUri(HEROES_ID);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d("Insert", "notify changed: " + uri.toString());
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch(match){
            case SHOWS:
                numDeleted = db.delete(Database.shows.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            Log.d("Delete", "notify changed (" + numDeleted + ")");
        }
        return numDeleted;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated;
        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }
        switch (sUriMatcher.match(uri)) {
            case SHOWS:
                numUpdated = db.update(Database.shows.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            Log.d("Update", "notify changed (" + numUpdated + ")");
        }
        return numUpdated;
    }

}