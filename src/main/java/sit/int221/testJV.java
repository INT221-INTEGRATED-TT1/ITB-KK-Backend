package sit.int221;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class testJV {
    public static void main(String[] args) throws ParseException {
        System.out.println("".trim().length() == 0);
        String string1 = "using equals method";
        String string2 = "using equals method";

        String string3 = "using EQUALS method";
        String string4 = new String("using equals method");

        System.out.println(string1.equals(string3));

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);

    }
}
