package exam.test.sample.task;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskJobParameterConfig {

	@SuppressWarnings("unused")
	private Map<String, String> jobparametersMap = new HashMap<>();

	public Tasklet taskJobParameterConfig(Map<String, String> jobparametersMap) {
		
		this.jobparametersMap = jobparametersMap;

		return new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
				return RepeatStatus.FINISHED;
			}
		};
	}
}
