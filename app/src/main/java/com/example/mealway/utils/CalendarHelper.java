package com.example.mealway.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarHelper {


    public static boolean hasPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean saveToCalendar(Context context, String title, String description, long timestamp) {
        try {
            ContentResolver cr = context.getContentResolver();
            long calID = getPrimaryCalendarId(cr);
            
            long normalizedTimestamp = normalizeTimestamp(timestamp);

            if (isEventScheduled(cr, calID, title, normalizedTimestamp)) {
                return true;
            }

            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, normalizedTimestamp);
            values.put(CalendarContract.Events.DTEND, normalizedTimestamp + 3600000);
            values.put(CalendarContract.Events.TITLE, title);
            values.put(CalendarContract.Events.DESCRIPTION, description);
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            return uri != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static long getPrimaryCalendarId(ContentResolver cr) {
        long calID = 1;
        String[] projection = new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.IS_PRIMARY};
        try (Cursor cur = cr.query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null)) {
            if (cur != null) {
                while (cur.moveToNext()) {
                    if (cur.getInt(1) == 1) {
                        return cur.getLong(0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calID;
    }


    public static void deleteFromCalendar(Context context, String title, long timestamp) {
        try {
            ContentResolver cr = context.getContentResolver();
            long calID = getPrimaryCalendarId(cr);
            long normalizedTimestamp = normalizeTimestamp(timestamp);

            String selection = CalendarContract.Events.CALENDAR_ID + " = ? AND " +
                              CalendarContract.Events.TITLE + " = ? AND " +
                              CalendarContract.Events.DTSTART + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(calID), title, String.valueOf(normalizedTimestamp)};

            cr.delete(CalendarContract.Events.CONTENT_URI, selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long normalizeTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private static boolean isEventScheduled(ContentResolver cr, long calID, String title, long timestamp) {
        String[] projection = new String[]{CalendarContract.Events._ID};
        String selection = CalendarContract.Events.CALENDAR_ID + " = ? AND " +
                          CalendarContract.Events.TITLE + " = ? AND " +
                          CalendarContract.Events.DTSTART + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(calID), title, String.valueOf(timestamp)};

        try (Cursor cur = cr.query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null)) {
            return cur != null && cur.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
