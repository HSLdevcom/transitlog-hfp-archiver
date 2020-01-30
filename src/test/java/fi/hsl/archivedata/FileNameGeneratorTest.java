package fi.hsl.archivedata;

import fi.hsl.common.filename.FileNameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

class FileNameGeneratorTest {

    private static final String yyyy_MM_dd_dot_csv = "\\S*-\\d+-\\d{2}-\\d{2}\\.csv";
    private FileNameGenerator fileNameGenerator;

    @BeforeEach
    void setUp() {
        this.fileNameGenerator = new FileNameGenerator("/csv/dump.csv");
    }

    @Test
    void generateCSVFileName() {
        String fileNameWithDate = this.fileNameGenerator.generateCSVFileName();
        Pattern pattern = Pattern.compile(yyyy_MM_dd_dot_csv);
        Matcher matcher = pattern.matcher(fileNameWithDate);
        assertTrue(matcher.matches());
    }
}