package fi.hsl.features.archivedata;

import fi.hsl.AbstractBatchTest;
import fi.hsl.EnableBatchTestConfiguration;
import fi.hsl.common.Date;
import fi.hsl.configuration.AllowScheduling;
import fi.hsl.configuration.EnableBatchProcessing;
import fi.hsl.domain.VehiclePosition;
import fi.hsl.domain.repositories.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.co.jemos.podam.api.PodamFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBatchTest
@SpringBootTest(classes = {EnableBatchProcessing.class, BatchReadersAndWriters.class, EnableBatchTestConfiguration.class, BatchAutoConfiguration.class, AllowScheduling.class, ArchiveHfpCSVDump.class})
@TestPropertySource(locations = "classpath:/application.properties")
class ScheduledArchiveDumpTest extends AbstractBatchTest {

    @Autowired
    private ArchiveHfpCSVDump archiveHfpCSVDump;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PodamFactory testPodamFactory;

    @BeforeEach
    public void init() {
        jobRepositoryTestUtils.removeJobExecutions();
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
    public void testScheduling() throws InterruptedException, IOException {
        //Check scheduling works
        Thread.sleep(2000L);
        List<VehiclePosition> eventsFromCSVFile = getEventsFromCSVFile(VehiclePosition.class);
        assertEquals(2, eventsFromCSVFile.size());
    }
}
