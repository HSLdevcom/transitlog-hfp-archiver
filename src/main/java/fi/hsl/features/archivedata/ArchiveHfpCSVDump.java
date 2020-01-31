package fi.hsl.features.archivedata;

import fi.hsl.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class ArchiveHfpCSVDump {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private BatchReadersAndWriters batchReadersAndWriters;
    @Autowired
    private JobLauncher jobLauncher;


    private Set<Class<? extends Event>> events = Set.of(StopEvent.class, OtherEvent.class, LightPriorityEvent.class, UnsignedEvent.class, VehiclePosition.class);


    @Scheduled(cron = "${schedule.archiveoldevents}")
    public void startHfpArchival() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("jobStartDate", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(createHfpArchivalJob(), jobParameters);
    }

    @Bean
    Job createHfpArchivalJob() {
        return new HfpArchiver("archiveOldEventsJob")
                .createBatchJobInstance();
    }


    private class HfpArchiver {

        private final String jobName;

        HfpArchiver(String jobName) {
            this.jobName = jobName;
        }

        Job createBatchJobInstance() {
            BatchReadersAndWriters.Writer<String> writer = batchReadersAndWriters.fileWriter();
            return jobBuilderFactory.get("archiveOldEventsJob")
                    .incrementer(new RunIdIncrementer())
                    .flow(transformIntoCSV(writer.getFileWriter()))
                    .next(uploadToStorage(writer.getFilePath()))
                    .end()
                    .build();
        }

        private Step transformIntoCSV(ItemWriter<? super String> fileWriter) {
            return stepBuilderFactory
                    .get("transformIntoCSVStep")
                    .<Event, String>chunk(1000)
                    .reader(batchReadersAndWriters.eventReader())
                    .faultTolerant()
                    .retryPolicy(new AlwaysRetryPolicy())
                    .processor(batchReadersAndWriters.csvProcessor())
                    .writer(fileWriter)
                    .build();
        }

        private Step uploadToStorage(String filePath) {
            return stepBuilderFactory
                    .get("uploadToStroage")
                    .tasklet(batchReadersAndWriters.storageUpload(filePath))
                    .build();
        }
    }
}
