package fi.hsl.archivedata;

import fi.hsl.common.CSVMapper;
import fi.hsl.common.Date;
import fi.hsl.common.filename.FileNameGenerator;
import fi.hsl.domain.Event;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;

@Component
class ReadersAndWriters {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Value(value = "${csv.filename}")
    private String csvFilename;

    @Bean
    ItemReader<Event> eventReader() {
        return new JpaPagingItemReaderBuilder<Event>()
                .name("eventReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select e from Event e where e.tst >= \'" + Date.yesterdaymidnight() + "\' and e.tst < \'" + Date.todaymidnight() + "'")
                .pageSize(1000)
                .saveState(true)
                .build();
    }


    ItemProcessor<Event, String> csvProcessor() {
        return new CSVMapper();
    }

    ItemWriter<? super String> fileWriter() {
        String filepath = new FileNameGenerator(csvFilename).generateCSVFileName();
        DelimitedLineAggregator<String> commaSeparatedLineAggregator = new DelimitedLineAggregator<>() {{
            setDelimiter(",");
        }};
        return new FlatFileItemWriterBuilder<String>()
                .name("fileWriter")
                .resource(new FileSystemResource(filepath))
                .lineAggregator(commaSeparatedLineAggregator)
                .lineSeparator("")
                .shouldDeleteIfExists(true).build();
    }
}
