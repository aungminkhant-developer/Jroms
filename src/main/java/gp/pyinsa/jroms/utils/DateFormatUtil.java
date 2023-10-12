package gp.pyinsa.jroms.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
    /**
     * This will convert the java.util.Date to String with format '2022-10-22 05:30 PM'
     * @param date - date to be formatted
     * @return the formatted date
     */
    public static String formatDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        return sdf.format(date);
    }

    public static String formatDateToStringExcelFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(date);
    }

    /**
     * This will convert the java.util.Date to String with the format passed as argument.
     * @param date - date to be formatted
     * @param pattern - Format patter, for example, 'yyyy-MM-dd'
     * @return the formatted date
     */
    public static String formatDateToString(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date parseStringToDatePrefixed(String dateString) {
        return parseStringToDate(dateString, "yyyy-MM-dd hh:mm a");
    }

    public static Date parseStringToDate(String dateString, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
