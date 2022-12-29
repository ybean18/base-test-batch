package exam.test.common.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StepListener {

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		log.info("beforeStep");
	}

	@AfterStep
	public ExitStatus afterStep(StepExecution stepExecution) {

		log.info("afterStep");

		String exitCode = stepExecution.getExitStatus().getExitCode();

		if (!exitCode.equals(ExitStatus.FAILED.getExitCode()) && stepExecution.getSkipCount() > 0) {
			return new ExitStatus("StepListener exit.");
		} else {
			return null;
		}
	}
}
