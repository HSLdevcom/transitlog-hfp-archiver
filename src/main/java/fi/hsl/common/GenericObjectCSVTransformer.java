package fi.hsl.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class GenericObjectCSVTransformer<T> {

    private final CsvMapper csvMapper;

    public GenericObjectCSVTransformer() {
        this.csvMapper = new CsvMapper();
    }

    public String turnIntoCSVString(T object) throws JsonProcessingException {
        CsvSchema schema = csvMapper.schemaFor(object.getClass());
        ObjectWriter writer = csvMapper.writer(schema);
        return
                writer.writeValueAsString(object);
    }
}
