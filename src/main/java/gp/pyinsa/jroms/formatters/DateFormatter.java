package gp.pyinsa.jroms.formatters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.format.Formatter;

public class DateFormatter implements Formatter<Date> {
    private static SimpleDateFormat sdf;

    static {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public String print(Date object, Locale locale) {
        return sdf.format(object);
    }

    @Override
    public Date parse(String text, Locale locale) throws ParseException {
        return sdf.parse(text);
    }

}
