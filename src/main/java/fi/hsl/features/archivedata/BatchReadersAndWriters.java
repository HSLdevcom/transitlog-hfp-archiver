package fi.hsl.features.archivedata;

import fi.hsl.common.BlobStorage;
import fi.hsl.common.CSVMapper;
import fi.hsl.common.Date;
import fi.hsl.common.FileNameGenerator;
import fi.hsl.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;

import static fi.hsl.common.FileNameGenerator.TypedFilenameGenerator;

@Component
class BatchReadersAndWriters {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private BlobStorage blobStorage;
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

    public <T> Writer<T> fileWriter() {
        FileNameGenerator fileNameGenerator = new TypedFilenameGenerator(csvFilename);
        String filepath = fileNameGenerator.generateCSVFileName();
        DelimitedLineAggregator<T> commaSeparatedLineAggregator = new DelimitedLineAggregator<>() {{
            setDelimiter(",");
        }};
        FlatFileItemWriter<T> fileWriter = createFlatFIleItemReader(filepath, commaSeparatedLineAggregator);
        return new Writer<T>(filepath, fileWriter);
    }

    private <T> FlatFileItemWriter<T> createFlatFIleItemReader(String filepath, DelimitedLineAggregator<T> commaSeparatedLineAggregator) {
        return new FlatFileItemWriterBuilder<T>()
                .name("fileWriter")
                .resource(new FileSystemResource(filepath))
                .lineAggregator(commaSeparatedLineAggregator)
                .lineSeparator("")
                .shouldDeleteIfExists(true).build();
    }

    Tasklet storageUpload(String filePath) {
        return new StorageUpload(filePath);
    }

    @Data
    @AllArgsConstructor
    static class Writer<T> {
        private final String filePath;
        private final FlatFileItemWriter<T> fileWriter;
    }

    private class StorageUpload implements Tasklet {
        private String filePath;

        StorageUpload(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
            blobStorage.uploadBlob(filePath);
            return RepeatStatus.FINISHED;
        }
    }
}
