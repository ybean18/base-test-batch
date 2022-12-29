package exam.test.prism.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import exam.test.common.listener.JobListener;
import exam.test.prism.task.TaskPrismReasearch;
import exam.test.sample.step.StepTest;
import exam.test.sample.task.TaskJobParameterConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBatchProcessing
@SuppressWarnings("unused")
public class JobPrism01 {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	 
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobListener jobListener;
	
	@Autowired
	private TaskJobParameterConfig taskJobParameterConfig;
	
	@Autowired
	private TaskPrismReasearch taskPrismReasearch;
	
	@Autowired
	private StepTest stepTest;
	
	private Map<String, String> jobparametersMap = new HashMap<>();
	private int PARALLEL_CNT = 10;
	
	private final String JOB_ID     = "JobPrism01";
	private final String STEP_ID_01 = "taskJobParameterConfig";
	private final String STEP_ID_02 = "taskPrismReasearch";

	@Bean(JOB_ID)
	public Job doJob() {
		
		return jobBuilderFactory.get(JOB_ID)
				.listener(jobListener)
				.incrementer(new RunIdIncrementer())
				.preventRestart()
				.start(taskJobParameterConfig(null))
//				.next(stepSample())
				.next(taskPrismReasearch())
				.build();
	}
	
	/*
	 * Job Parameter 설정 Default Tasklet
	 * 
	 */
	@Bean(JOB_ID + STEP_ID_01)
	@JobScope
	public Step taskJobParameterConfig(@Value("#{jobParameters[runDt]}") String runDt) {
		jobparametersMap.put("runDt", runDt);
		return stepBuilderFactory.get(STEP_ID_01)
				.tasklet(taskJobParameterConfig.taskJobParameterConfig(jobparametersMap))
				.build();
	}

	@Bean(JOB_ID + STEP_ID_02)
	@JobScope
	public Step taskPrismReasearch() {
		return stepBuilderFactory.get(STEP_ID_02)
				.tasklet(taskPrismReasearch.taskPrismReasearch(jobparametersMap))
				.build();
	}
	
}
