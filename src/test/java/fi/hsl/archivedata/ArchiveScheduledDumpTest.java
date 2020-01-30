package fi.hsl.archivedata;

import fi.hsl.AbstractBatchTest;
import fi.hsl.EnableBatchTestConfiguration;
import fi.hsl.common.Date;
import fi.hsl.configuration.EnableBatchProcessing;
import fi.hsl.domain.VehiclePosition;
import fi.hsl.domain.repositories.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

@SpringBatchTest
@SpringBootTest(classes = {ArchiveScheduledDump.class, EnableBatchProcessing.class, ReadersAndWriters.class, EnableBatchTestConfiguration.class, BatchAutoConfiguration.class})
@TestPropertySource(locations = "classpath:/application.properties")
class ArchiveScheduledDumpTest extends AbstractBatchTest {

    @Autowired
    private ArchiveScheduledDump archiveScheduledDump;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PodamFactory testPodamFactory;

    @BeforeEach
    void init() {
        VehiclePosition vehiclePosition = testPodamFactory.manufacturePojo(VehiclePosition.class);
        vehiclePosition.setTst(Date.yesterdayHour(12));

        VehiclePosition vehiclePosition1 = testPodamFactory.manufacturePojo(VehiclePosition.class);
        vehiclePosition1.setTst(Date.yesterdayHour(11));

        eventRepository.save(vehiclePosition);
        eventRepository.save(vehiclePosition1);
    }

    @AfterEach
    public void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
        eventRepository.deleteAll();
    }

    @Test
    void archiveOldEvents() throws Exception {
        JobParametersBuilder jobParametersBuilder = createDefaultJobParams();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParametersBuilder.toJobParameters());
        while (jobExecution.getStatus() != BatchStatus.COMPLETED) {
        }
        List<VehiclePosition> events = getEventsFromCSVFile(VehiclePosition.class);
        assertEquals(events.size(), 2);
    }

    private JobParametersBuilder createDefaultJobParams() {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("jobLaunched", String.valueOf(System.currentTimeMillis()));
        return jobParametersBuilder;
    }
}