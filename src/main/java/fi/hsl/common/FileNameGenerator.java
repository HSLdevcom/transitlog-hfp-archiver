package fi.hsl.common;

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
            return insertDateIntoFilename();
        } else {
            throw new IllegalArgumentException("CSV filename contains a whitespace!");
        }
    }

    private String insertDateIntoFilename() {
        int csvIndexPosition = csvFileName.indexOf(".csv");
        StringBuilder stringBuilder = new StringBuilder(csvFileName);
        stringBuilder.insert(csvIndexPosition, "-" + Date.today_year_month_day());
        return stringBuilder.toString();
    }

    public static class TypedFilenameGenerator extends FileNameGenerator {
        public TypedFilenameGenerator(String csvFileName) {
            super(csvFileName);
        }

        public String generateCSVFileName(Class<?> fileType) {
            String fileName = super.generateCSVFileName();
            //Add class name to fileName
            int firstDashIndex = fileName.indexOf("-");
            StringBuilder builder = new StringBuilder(fileName);
            builder.insert(firstDashIndex, "-" + fileType.getCanonicalName() + "-");
            return builder.toString();
        }
    }
}
