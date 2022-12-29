package exam.test.common.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JobListener {

	@Autowired
	private JobExplorer jobExplorer;
	
	@BeforeJob
	public void beforeJob(JobExecution jobExcution) {
		log.info("beforeJob");

		int runningJobCnt = jobExplorer.findRunningJobExecutions(jobExcution.getJobInstance().getJobName()).size();

		if (runningJobCnt > 1) {
			log.info("running job count : " + runningJobCnt);
			jobExcution.stop();
		}
	}

	@AfterJob
	public void afterJob(JobExecution jobExcution) {
		log.info("afterJob");
	}
}
