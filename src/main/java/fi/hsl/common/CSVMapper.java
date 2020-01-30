package fi.hsl.common;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fi.hsl.domain.Event;
import org.springframework.batch.item.ItemProcessor;

public class CSVMapper implements ItemProcessor<Event, String> {
    @Override
    public String process(Event event) throws Exception {
        CsvMapper schemaMapper = new CsvMapper();
        CsvSchema schema = schemaMapper.typedSchemaFor(event.getClass());
        ObjectWriter writer = schemaMapper.writer(schema);
        return
                writer.writeValueAsString(event);
    }
}
