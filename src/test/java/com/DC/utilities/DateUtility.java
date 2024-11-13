package com.DC.utilities;

import org.apache.poi.ss.usermodel.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtility {

    public static String getYesterday() {
        return LocalDate.now().minusDays(1).toString();
    }

    public static String getFirstDayOfLastThirtyDays() {
        return LocalDate.now().minusDays(30).toString();
    }

    public static String getFirstDayOfLastSevenDays() {
        return LocalDate.now().minusWeeks(1).toString();
    }

    public static String getFirstDayOfLastFourteenDays() {
        return LocalDate.now().minusWeeks(2).toString();
    }

    public static String getFirstDayOfLastMonth() {
        return LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).toString();
    }

    public static String getLastDayOfLastMonth() {
        return LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).toString();
    }

    public static String getFirstDayOfLastSixMonths() {
        return LocalDate.now().minusMonths(6).with(TemporalAdjusters.firstDayOfMonth()).toString();
    }

    public static String getLastDayOfLastFourWeeks() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)).toString();
    }

    public static String getFirstDayOfLastFourWeeks() {
        return LocalDate.now().minusWeeks(4).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).toString();
    }

    public static String getFirstDayOfLastThirteenWeeks() {
        return LocalDate.now().minusWeeks(13).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).toString();
    }

    public static String getFirstDayOfLastWeek() {
        return LocalDate.now().minusWeeks(1).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).toString();
    }

    public static String getLastDayOfLastWeek() {
        LocalDate current = LocalDate.now();
        LocalDate lastWeek = current.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).minusWeeks(1);
        LocalDate lastDayOfLastWeek = lastWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return lastDayOfLastWeek.toString();
    }

    public static String getFirstDayOfThisMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString();
    }

    public static String getFirstDayOfTheYear() {
        return LocalDate.now().withDayOfYear(1).toString();
    }

    public static String formatDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        return localDate.format(df);
    }

    public static String getLastDayOfLastThirteenWeeks() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)).toString();
    }

    public static String getLastDayOfLastSixMonths() {
        return LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).toString();
    }

    public static String getFirstDayOfLastFiftyTwoWeeks() {
        return LocalDate.now().minusWeeks(52).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).toString();
    }

    public static String getLastDayOfLastFiftyTwoWeeks() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)).toString();
    }

    public static String getCurrentDateTime() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy:HH.mm.ss");
        return currentDate.format(formatter);
    }

    public static String getCurrentDateTime(String format) {
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return currentDate.format(formatter);
    }

    public static String getCurrentDate(String format) {
        LocalDate localDate = LocalDate.parse(LocalDate.now().toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDate.format(formatter);
    }

    public static LocalDate getDateFromSequentialSerialNumber(double number) {
        return LocalDate.from(ZonedDateTime.ofInstant(Instant.ofEpochMilli(DateUtil.getJavaDate(number).getTime()), ZoneId.systemDefault()));
    }

    public static String formattingDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return localDate.format(df);
    }

    public static String getFirstDayOfLastYear() {
        return LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear()).toString();
    }

    public static String getLastDayOfLastThreeWeeks() {
        return LocalDate.now().minusWeeks(2).with(TemporalAdjusters.previous(DayOfWeek.SATURDAY)).toString();
    }

    public static String getFirstDayOfLastThreeWeeks() {
        return LocalDate.now().minusWeeks(3).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).toString();
    }

    public static String getFirstDayOfLastFifteenWeeks() {
        return LocalDate.now().minusWeeks(15).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).toString();
    }

    public static String getFirstDayOfLastTwelveMonths() {
        return LocalDate.now().minusMonths(12).with(TemporalAdjusters.firstDayOfMonth()).toString();
    }

    public static String getLastDayOfLastTwelveMonths() {
        return LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).toString();
    }

    public static String getTodayDate() {
        return LocalDate.now().toString();
    }

    public static String calculateWeekRange(String selectedDate) {
        LocalDate chosenDate = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        LocalDate closestSunday = chosenDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate closestSaturday = chosenDate.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));

        return closestSunday.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " - " + closestSaturday.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    public static String getFirstDayOfThisMonthForWeeklyInterval() {
        LocalDate firstDayOfThisMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        return firstDayOfThisMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).toString();
    }

    public static String getLastDayOfThisMonthForWeeklyInterval() {
        LocalDate lastDayOfThisMonth = LocalDate.parse(LocalDate.now().toString());
        return lastDayOfThisMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)).toString();
    }

    public static String getFirstDayOfLastMonthForWeeklyInterval() {
        LocalDate firstDayOfLastMonth = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        return firstDayOfLastMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).toString();
    }

    public static String getLastDayOfLastMonthForWeeklyInterval() {
        LocalDate lastDayOfLastMonth = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfLastMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).toString();
    }

    public static String getFirstDayOfLastSixMonthsForWeeklyInterval() {
        LocalDate firstDayOfLastSixMonths = LocalDate.now().minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
        return firstDayOfLastSixMonths.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).toString();
    }

    public static String getLastDayOfLastSixMonthsForWeeklyInterval() {
        LocalDate lastDayOfLastSixMonths = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfLastSixMonths.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).toString();
    }

    public static String getFirstDayOfLastTwelveMonthsForWeeklyInterval() {
        LocalDate firstDayOfLastTwelveMonths = LocalDate.now().minusMonths(12).with(TemporalAdjusters.firstDayOfMonth());
        return firstDayOfLastTwelveMonths.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).toString();
    }

    public static String getLastDayOfLastTwelveMonthsForWeeklyInterval() {
        LocalDate lastDayOfLastTwelveMonths = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfLastTwelveMonths.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).toString();
    }

    public static String getPreviousSaturday() {
        return LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.SATURDAY)).toString();
    }

    public static String getDayBeforeToday(int day) {
        return LocalDate.now().minusDays(day).toString();
    }

    public static int convertDateToInt(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Integer.parseInt(formatter.format(LocalDate.parse(date)));
    }

    public static String convertDateToString(int date) throws ParseException {
        String dateStr = String.valueOf(date);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFormatted = inputFormat.parse(dateStr);
        return outputFormat.format(dateFormatted);
    }

    public static int addDaysToDate(int date, int days) {
        LocalDate formattedDate = LocalDate.parse(Integer.toString(date), DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate resultDate = formattedDate.plusDays(days);
        return Integer.parseInt(resultDate.format(DateTimeFormatter.BASIC_ISO_DATE));
    }

    public static String createDate(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static String getLastDayOfThisMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString();
    }

    public static String getFirstDayOfNextMonth() {
        return LocalDate.now().plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).toString();
    }

    public static String getLastDayOfNextMonth() {
        return LocalDate.now().plusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).toString();
    }

    public static int calculateNumberOfMonthsInRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Period period = Period.between(LocalDate.parse(startDate, formatter), LocalDate.parse(endDate, formatter));
        return period.getYears() * 12 + period.getMonths();
    }

    public static LocalDate convertToLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateStr, formatter);
    }

    public static String getFirstDayOfNextYear() {
        return LocalDate.now().plusYears(1).withDayOfYear(1).toString();
    }

    public static String getLastDayOfNextYear() {
        return LocalDate.now().plusYears(1).with(TemporalAdjusters.lastDayOfYear()).toString();
    }

    public static String getFirstDayOfLastTwoWeeks() {
        return LocalDate.now().minusDays(13).toString();
    }

    public static String subtractDaysFromDate(String date, int days) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate resultDate = LocalDate.parse(date, formatter).minusDays(days);
        return resultDate.format(formatter);
    }

    public static String calculateDate(int year, int dayOfYear) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.ofYearDay(year, dayOfYear);
        return date.format(formatter);
    }

    public static int getDayOfDate(String date) {
        return LocalDate.parse(date).getDayOfMonth();
    }

    public static String getFollowingWeekFromDate(int inputDate, int numOfWeeks) throws ParseException {
        String date = convertDateToString(inputDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate resultDate = LocalDate.parse(date, formatter).plusWeeks(numOfWeeks);
        return resultDate.format(formatter);
    }

    public static String getFollowingMonthFromDate(int inputDate, int numOfMonths) throws ParseException {
        String date = convertDateToString(inputDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate resultDate = LocalDate.parse(date, formatter).plusMonths(numOfMonths);
        return resultDate.format(formatter);
    }

    public static String getFollowingDayFromDate(int inputDate, int numOfDays) throws ParseException {
        String date = convertDateToString(inputDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate resultDate = LocalDate.parse(date, formatter).plusDays(numOfDays);
        return resultDate.format(formatter);
    }

    public static int subtractDaysFromDate(int date, int days) {
        LocalDate formattedDate = LocalDate.parse(Integer.toString(date), DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate resultDate = formattedDate.minusDays(days);
        return Integer.parseInt(resultDate.format(DateTimeFormatter.BASIC_ISO_DATE));
    }

    public static int extractMonth(int fullDate) {
        String date = Integer.toString(fullDate);
        return Integer.parseInt(date.substring(4, 6).replaceFirst("^0+", ""));
    }

    public static int extractYear(int fullDate) {
        String date = Integer.toString(fullDate);
        return Integer.parseInt(date.substring(0, 4));
    }

    public static int extractDay(int fullDate) {
        String date = Integer.toString(fullDate);
        return Integer.parseInt(date.substring(6, 8).replaceFirst("^0+", ""));
    }

    public static int getLastDayOfMonth(int inputDate) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.parse(convertDateToString(inputDate));
        LocalDate lastDayOfSameMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        return convertDateToInt(lastDayOfSameMonth.format(formatter));
    }

    public static String getLastDayOfAnyMonth(String inputDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.parse(inputDate);
        LocalDate lastDayOfSameMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        return lastDayOfSameMonth.format(formatter);
    }

    public static List<String> getEverySundayInDateRange(String dateRange) {
        List<String> sundays = new ArrayList<>();
        String[] dates = dateRange.split(" - ");

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate startDate = LocalDate.parse(dates[0], inputFormatter);
        LocalDate endDate = LocalDate.parse(dates[1], inputFormatter);

        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                sundays.add(currentDate.format(outputFormatter));
            }
            currentDate = currentDate.plusDays(1);
        }
        return sundays;
    }

    public static List<String> getDatesInDateRange(String operand, String operand2) {
        List<String> dates = new ArrayList<>();

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate startDate = LocalDate.parse(operand, outputFormatter);
        LocalDate endDate = LocalDate.parse(operand2, outputFormatter);

        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate.format(outputFormatter));
            currentDate = currentDate.plusDays(1);
        }
        return dates;
    }

    public static List<String> getFirstDayOfEveryMonthInDateRange(String dateRange) {
        List<String> firstDaysOfMonths = new ArrayList<>();
        String[] dates = dateRange.split(" - ");

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate startDate = LocalDate.parse(dates[0], inputFormatter);
        LocalDate endDate = LocalDate.parse(dates[1], inputFormatter);

        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfMonth() == 1) {
                firstDaysOfMonths.add(currentDate.format(outputFormatter));
            }
            currentDate = currentDate.plusDays(1);
        }
        return firstDaysOfMonths;
    }

    public static String formattingDateStartWithYear(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(df);
    }

    public static String getLastDayOfLastYear() {
        return LocalDate.now().minusYears(1).with(TemporalAdjusters.lastDayOfYear()).toString();
    }

    public static int getDaysBetweenDates(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        return (int) ChronoUnit.DAYS.between(start, end);
    }

    public static String formatDateNonStandard(String date) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate localDate = LocalDate.parse(date, inputFormat);
            return localDate.format(outputFormat);
        } catch (Exception e) {
            return date;
        }
    }

    public static LocalDate convertStringToDate(String date) {
        return LocalDate.parse(date);
    }

    public static String getPreviousPeriodStartDate(LocalDate startDate, LocalDate endDate) {
        int dailySpan = (int) (ChronoUnit.DAYS.between(startDate, endDate));

        LocalDate previousStartDate = startDate.minusDays(dailySpan + 1L);
        return previousStartDate.toString();
    }

    public static String getPreviousPeriodEndDate(LocalDate startDate, LocalDate endDate) {
        LocalDate previousEndDate = startDate.minusDays(1L);
        return previousEndDate.toString();
    }
    public static String retrieveMonth(int date) {
        int monthNumber = extractMonth(date);
        String monthName = Month.of(monthNumber).name().substring(0, 3);
        return monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
    }
}