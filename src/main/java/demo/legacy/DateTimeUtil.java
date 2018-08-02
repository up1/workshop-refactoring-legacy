package demo.legacy;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateTimeUtil {

    private static final String DATE_FORMATTER_SLASH = "/";
    private static final DateTimeFormatter FORMATTER_YMD = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATTER_YMD_NO_SLASHES = DateTimeFormat.forPattern("yyyyMMdd");
    private static final DateTimeFormatter FORMATTER_MDY = DateTimeFormat.forPattern("MM/dd/yyyy");
    private static final DateTimeFormatter FORMATTER_MDY_NO_SLASHES = DateTimeFormat.forPattern("MMddyyyy");
    private static final DateTimeFormatter FORMATTER_MDY_TIME = DateTimeFormat.forPattern("MM/dd/yyyy h:mm a");

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int MILLISECONDS_PER_MINUTE = 60 * MILLISECONDS_PER_SECOND;
    private static final int MILLISECONDS_PER_HOUR = 60 * MILLISECONDS_PER_MINUTE;
    private static final int MILLISECONDS_PER_DAY = 24 * MILLISECONDS_PER_HOUR;

    private static final String JODA_TIME_PARSED_DEFAULT_YEAR = "0000-12-30 00:03:58.000";
    private static final String DEFAULT_MIN_YEAR = "0001-01-01 00:00:00.000";
    public static final Map<Integer, Integer> FINANCIAL_YEAR_MAP = new HashMap<Integer, Integer>() {

        private static final long serialVersionUID = 1L;

        {
            put(2011, 14);
            put(2012, 15);
            put(2013, 16);
            put(2014, 17);
            put(2015, 18);
            put(2016, 19);
            put(2017, 20);
            put(2018, 21);
            put(2019, 22);
            put(2020, 23);
            put(2021, 24);
            put(2022, 25);
            put(2023, 26);
            put(2024, 27);
            put(2025, 28);
            put(2026, 29);
            put(2027, 30);
            put(2028, 31);
            put(2029, 32);
            put(2030, 33);
            put(2031, 34);
            put(2032, 35);
            put(2033, 36);
            put(2034, 37);
            put(2035, 38);
            put(2036, 39);
        }
    };

    private DateTimeUtil() {
    }

    public static DateTime getCurrentDateTime() {
        return new DateTime(DateTimeUtils.currentTimeMillis());
    }

    public static Date convertToSqlDate(final LocalDate inputDate) {
        if (inputDate == null) {
            return null;
        }

        return new Date(inputDate.toDate().getTime());
    }

    public static LocalDate convertToLocalDate(final java.util.Date inputDate) {
        if (inputDate == null) {
            return null;
        }

        return new LocalDate(inputDate);
    }

    public static DateTime convertToDateTime(final java.util.Date inputDate) {
        if (inputDate == null) {
            return null;
        }

        return new DateTime(inputDate);
    }

    private static Date parseSqlDateFromString(final String inputDate, final DateTimeFormatter format) {
        if (inputDate != null) {
            final LocalDate localDate = LocalDate.parse(inputDate, format);

            return new Date(localDate.toDate().getTime());
        }
        return null;
    }

    public static Date parseSQLDateFromyyyyMMdd(final String dateAsString) {
        return parseSqlDateFromString(dateAsString, FORMATTER_YMD_NO_SLASHES);
    }

    public static boolean isBusinessDate(final LocalDate inputDate, final List<LocalDate> holidays) {
        return !isWeekend(inputDate) && !isHoliday(inputDate, holidays);
    }

    private static boolean isHoliday(final LocalDate inputDate, final List<LocalDate> holidays) {
        for (final LocalDate holiday : holidays) {
            if (inputDate.isEqual(holiday)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWeekend(final LocalDate date) {
        final int dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY;
    }

    public static LocalDate getNextBusinessDay(final LocalDate inputDate, final List<LocalDate> holidays) {
        LocalDate returnDate = inputDate.plusDays(1);

        while (!isBusinessDate(returnDate, holidays)) {
            returnDate = returnDate.plusDays(1);
        }

        return returnDate;
    }

    public static String convertDateFromMdyToYmd(final String dateAsString) {
        final String input = dateAsString.replaceAll(DATE_FORMATTER_SLASH, "");
        final DateTime dateTime = FORMATTER_MDY_NO_SLASHES.parseDateTime(input);
        return FORMATTER_YMD_NO_SLASHES.print(dateTime);
    }

    public static String convertDateFromYmdToMdy(final String dateAsString) {
        final String input = dateAsString.replaceAll("-", "");
        final DateTime dateTime = FORMATTER_YMD_NO_SLASHES.parseDateTime(input);
        return FORMATTER_MDY.print(dateTime);
    }

    public static String convertDateFromMdyToSqlDateYmdString(final String dateAsString) {
        final String input = dateAsString.replaceAll(DATE_FORMATTER_SLASH, "");
        final DateTime dateTime = FORMATTER_MDY_NO_SLASHES.parseDateTime(input);
        return FORMATTER_YMD.print(dateTime);
    }

    public static String formatDateMdy(final java.util.Date date) {
        return FORMATTER_MDY.print(new DateTime(date));
    }

    public static String formatDateMdy(final DateTime dateTime) {
        return FORMATTER_MDY.print(dateTime);
    }

    public static String formatDateMdyNoSlashes(final java.util.Date date) {
        return FORMATTER_MDY_NO_SLASHES.print(new DateTime(date));
    }

    public static String formatDateMdy(final LocalDate date) {
        return FORMATTER_MDY.print(date);
    }

    public static String formatDateTimeForBulkLoader(final DateTime input) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return dateTimeFormatter.print(input).equals(JODA_TIME_PARSED_DEFAULT_YEAR) ? DEFAULT_MIN_YEAR
                : dateTimeFormatter.print(input);
    }

    public static String formatDateYmdNoDashes(final LocalDate input) {
        return FORMATTER_YMD_NO_SLASHES.print(input);
    }

    public static boolean isFutureDate(final java.util.Date givenDate) {
        return new LocalDate(givenDate).isAfter(LocalDate.now());
    }

    public static boolean isFutureDate(final LocalDate givenDate) {
        return givenDate.isAfter(LocalDate.now());
    }

    public static DateTime parseDateTimeFrom(final String input) {
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").parseDateTime(input);
    }

    public static LocalDate parseLocalDateFrom(final String input) {
        return DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(input);
    }

    public static LocalDate parseLocalDateFromMDY(final String input) {
        return FORMATTER_MDY.parseLocalDate(input);
    }

    public static DateTime getDateTimeRelativeToCurrentDate(final int days) {
        return new DateTime(DateTimeUtils.currentTimeMillis()).plusDays(days);
    }

    public static LocalDate getCurrentLocalDate() {
        return new LocalDate(DateTimeUtils.currentTimeMillis());
    }

    public static LocalDate parseLocalDateFromMMddyyyy(final String date) {
        return FORMATTER_MDY.parseLocalDate(date);
    }

    public static LocalDate parseLocalDateFromyyyyMMdd(final String date) {
        return date == null ? null : FORMATTER_YMD_NO_SLASHES.parseLocalDate(date);
    }

    public static String convertDateFromPatternToPattern(final String dateString, final String inputFormat,
            final String outputFormat) {
        final LocalDate localDate = parseLocalDateWithPattern(dateString, inputFormat);
        return formatDateWithPattern(localDate, outputFormat);
    }

    public static String formatDateWithPattern(final LocalDate date, final String pattern) {
        return DateTimeFormat.forPattern(pattern).print(date);
    }

    public static String formatDateWithPattern(final DateTime date, final String pattern) {
        return DateTimeFormat.forPattern(pattern).print(date);
    }

    public static LocalDate parseLocalDateWithPattern(final String date, final String pattern) {
        return date == null ? null : DateTimeFormat.forPattern(pattern).parseLocalDate(date);
    }

    public static String formattedTimeFromMilliseconds(long milliseconds) {
        final StringBuilder formattedTime = new StringBuilder("");
        if (milliseconds >= MILLISECONDS_PER_DAY) {
            formattedTime.append(milliseconds / MILLISECONDS_PER_DAY).append(" day(s) ");
            milliseconds %= MILLISECONDS_PER_DAY;
        }
        if (milliseconds >= MILLISECONDS_PER_HOUR) {
            formattedTime.append(milliseconds / MILLISECONDS_PER_HOUR).append(" hour(s) ");
            milliseconds %= MILLISECONDS_PER_HOUR;
        }
        if (milliseconds >= MILLISECONDS_PER_MINUTE) {
            formattedTime.append(milliseconds / MILLISECONDS_PER_MINUTE).append(" minute(s) ");
            milliseconds %= MILLISECONDS_PER_MINUTE;
        }
        if (milliseconds >= MILLISECONDS_PER_SECOND) {
            formattedTime.append(milliseconds / MILLISECONDS_PER_SECOND).append(" second(s) ");
            milliseconds %= MILLISECONDS_PER_SECOND;
        }
        if (milliseconds > 0) {
            formattedTime.append(milliseconds).append(" millisecond(s)");
        }
        return formattedTime.toString().trim();

    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(DateTimeUtils.currentTimeMillis());
    }

    public static Timestamp getDateTimeAsTimestamp(final DateTime inputTime) {
        return new Timestamp(inputTime.getMillis());
    }

    public static String getProcessingTimeDescription(final DateTime createTime, final DateTime updateTime) {

        if (updateTime == null) {
            return "N/A";
        }

        final long endTime = updateTime.getMillis();
        final long startTime = createTime.getMillis();

        final long processingTimeInMinutes = (endTime - startTime) / 60000L;
        if (processingTimeInMinutes == 1L) {
            return processingTimeInMinutes + " minute";
        }
        if (processingTimeInMinutes > 1L) {
            return processingTimeInMinutes + " minutes";
        }
        return "< 1 minute";
    }

    public static LocalDate firstDayOfYear(final int startYear) {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, startYear);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        return new LocalDate(cal.getTime());
    }

    public static LocalDate lastDayOfYear(final int startYear) {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, startYear);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 31);

        return new LocalDate(cal.getTime());
    }

    public static int getYearFromDate(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.YEAR);

    }

}
