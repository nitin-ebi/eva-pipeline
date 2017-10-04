package uk.ac.ebi.eva.t2d.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import uk.ac.ebi.eva.pipeline.Application;
import uk.ac.ebi.eva.t2d.jobs.steps.ReleaseDatasetStep;
import uk.ac.ebi.eva.t2d.parameters.validation.job.ReleaseDatasetJobValidator;

import static uk.ac.ebi.eva.t2d.BeanNames.T2D_RELEASE_DATASET_JOB;
import static uk.ac.ebi.eva.t2d.BeanNames.T2D_RELEASE_DATASET_STEP;

@Configuration
@Profile(Application.T2D_PROFILE)
@EnableBatchProcessing
@Import({ReleaseDatasetStep.class})
public class ReleaseDataset {

    private static Logger logger = LoggerFactory.getLogger(LoadSamplesDataJob.class);

    @Bean(T2D_RELEASE_DATASET_JOB)
    public Job loadSampleDataJob(JobBuilderFactory jobBuilderFactory,
                                 @Qualifier(T2D_RELEASE_DATASET_STEP) Step releaseDatasetStep) {
        logger.debug("Building '" + T2D_RELEASE_DATASET_JOB + "'");

        JobBuilder jobBuilder = jobBuilderFactory
                .get(T2D_RELEASE_DATASET_JOB)
                .incrementer(new RunIdIncrementer())
                .validator(new ReleaseDatasetJobValidator());
        return jobBuilder.start(releaseDatasetStep)
                .build();
    }

}
