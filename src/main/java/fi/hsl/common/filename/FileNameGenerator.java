package fi.hsl.common.filename;

import fi.hsl.common.Date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameGenerator {
    private static final String NON_WHITESPACE_FILENAME = "\\S+(?=[\\S])\\.csv$";
    private String csvFileName;

    public FileNameGenerator(String csvFileName) {
        this.csvFileName = csvFileName;
    }


    public String generateCSVFileName() {
        Pattern containsNoWhitespace = Pattern.compile(NON_WHITESPACE_FILENAME);
        Matcher matcher = containsNoWhitespace.matcher(csvFileName);
        if (matcher.matches()) {
            int csvIndexPosition = csvFileName.indexOf(".csv");
            StringBuilder stringBuilder = new StringBuilder(csvFileName);
            stringBuilder.insert(csvIndexPosition, "-" + Date.today_year_month_day());
            return stringBuilder.toString();
        } else {
            throw new IllegalArgumentException("CSV filename contains a whitespace!");
        }
    }
}
