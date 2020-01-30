package fi.hsl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import fi.hsl.common.FileNameGenerator;
import fi.hsl.domain.Event;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public abstract class AbstractBatchTest {

    @Value(value = "${csv.filename}")
    private String csvfilename;

    protected <T extends Event> List<T> getEventsFromCSVFile(Class<T> eventType) throws java.io.IOException {
        //Check that the new file exists
        String fileWritten = new FileNameGenerator(csvfilename).generateCSVFileName();
        File file = new File(fileWritten);
        assertTrue(file.exists());
        //Check file contains a valid csv row

        MappingIterator<T> csvIterator = new CsvMapper().readerWithSchemaFor(eventType).readValues(file);
        return csvIterator.readAll();
    }
}
