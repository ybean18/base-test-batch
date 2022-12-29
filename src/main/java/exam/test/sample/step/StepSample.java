package exam.test.sample.step;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import exam.test.common.listener.StepListener;
import exam.test.sample.dto.SampleDto;

@Component
public class StepSample {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private StepListener stepListener;
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	private Map<String, String> jobparametersMap = new HashMap<>();
	
	private final String  STEP_NM    = "StepSample";
	private final int     CHUNK_SIZE = 1000;
	
	public Step stepSample(Map<String, String> jobparametersMap) {
		
		this.jobparametersMap = jobparametersMap;

		return stepBuilderFactory.get(STEP_NM).<SampleDto, SampleDto>chunk(CHUNK_SIZE)
				.listener(stepListener)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}
	
	private MyBatisPagingItemReader<SampleDto> reader() {
		Map<String, Object> parameterValues = new HashMap<>();
		
		parameterValues.put("runDt", jobparametersMap.get("runDt"));
		
		return new MyBatisPagingItemReaderBuilder<SampleDto>()
					.pageSize(CHUNK_SIZE)
					.sqlSessionFactory(sqlSessionFactory)
					.queryId("kr.co.ihopper.acro.sample.mapper.SampleMapper.selectSampleList")
					.parameterValues(parameterValues)
					.build();

	}
	
	private ItemProcessor<SampleDto, SampleDto> processor() {
		return new ItemProcessor<SampleDto, SampleDto>() {
			
			@Override
			public SampleDto process(SampleDto item) throws Exception {
				return item;
			}
		};
	}
	
	private MyBatisBatchItemWriter<SampleDto> writer() {
		return new MyBatisBatchItemWriterBuilder<SampleDto>()
					.sqlSessionFactory(sqlSessionFactory)
					.assertUpdates(false)
					.statementId("kr.co.ihopper.acro.sample.mapper.SampleMapper.insertSampleList")
					.build();
	}
}
