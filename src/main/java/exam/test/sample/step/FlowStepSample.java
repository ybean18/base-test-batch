package exam.test.sample.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import exam.test.common.listener.StepListener;
import exam.test.sample.dto.SampleDto;

@Component
public class FlowStepSample {
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private StepListener stepListener;

	private Map<String, String> jobparametersMap = new HashMap<>();
	
	private final String STEP_NM    = "FlowStepSample";
	private final int    CHUNK_SIZE = 1000;
	
	public Step flowStepSample(Map<String, String> jobparametersMap, int parallelNum) {
		
		this.jobparametersMap = jobparametersMap;

		return stepBuilderFactory.get(STEP_NM + "_" + parallelNum).<SampleDto, SampleDto>chunk(CHUNK_SIZE)
				.listener(stepListener)
				.reader(reader(parallelNum))
				.processor(processor())
				.writer(writer())
				.build();
	}
	
	private ListItemReader<SampleDto> reader(int parallelNum) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("runDt"      , jobparametersMap.get("runDt"));
		paramMap.put("parallelNum", parallelNum);
		
		List<SampleDto> list = new ArrayList<>();

		return new ListItemReader<SampleDto>(list);
		
	}
	
	private ItemProcessor<SampleDto, SampleDto> processor() {
		return new ItemProcessor<SampleDto, SampleDto>() {
			
			@Override
			public SampleDto process(SampleDto item) throws Exception {

				return item;
			}
		};
	}
	
	private ItemWriter<SampleDto> writer() {
		return new ItemWriter<SampleDto>() {
			
			@Override
			public void write(List<? extends SampleDto> items) throws Exception {
				
			}
		};
	}
}
