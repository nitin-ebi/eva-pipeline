package uk.ac.ebi.eva.pipeline;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import uk.ac.ebi.eva.pipeline.jobs.GenotypedVcfJob;

import java.util.List;

/**
 * The purpose of this test is to imitate an execution made by an user through the CLI.
 * This is needed because all the other tests just instantiate what they need (just one step, or just one job) and
 * sometimes we have errors due to collisions instantiating several jobs. This test should instantiate everything
 * Spring instantiates in a real execution.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"integrationTest,test,mongo"})
public class ApplicationTest {

    @Autowired
    JobExplorer jobExplorer;

    @Test
    public void main() throws Exception {
        Assert.assertEquals(1, jobExplorer.getJobNames().size());
        Assert.assertEquals(GenotypedVcfJob.jobName, jobExplorer.getJobNames().get(0));
        
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(GenotypedVcfJob.jobName, 0, 100);
        Assert.assertEquals(1, jobInstances.size());
        
        JobExecution jobExecution = jobExplorer.getJobExecution(jobInstances.get(0).getInstanceId());
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }

}
