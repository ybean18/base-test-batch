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
import exam.test.sample.dto.TestDto;

@Component
public class StepTest {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private StepListener stepListener;
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	private Map<String, String> jobparametersMap = new HashMap<>();
	
	private final String  STEP_NM    = "StepTest";
	private final int     CHUNK_SIZE = 10;
	
	public Step stepTest(Map<String, String> jobparametersMap) {
		
		this.jobparametersMap = jobparametersMap;

		return stepBuilderFactory.get(STEP_NM).<TestDto, TestDto>chunk(CHUNK_SIZE)
				.listener(stepListener)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}
	
	private MyBatisPagingItemReader<TestDto> reader() {
		Map<String, Object> parameterValues = new HashMap<>();
		
		parameterValues.put("runDt", jobparametersMap.get("runDt"));
		
		return new MyBatisPagingItemReaderBuilder<TestDto>()
					.pageSize(CHUNK_SIZE)
					.sqlSessionFactory(sqlSessionFactory)
					.queryId("kr.co.ihopper.acro.sample.mapper.TestMapper.selectTestList")
					.parameterValues(parameterValues)
					.build();

	}
	
	private ItemProcessor<TestDto, TestDto> processor() {
		return new ItemProcessor<TestDto, TestDto>() {
			
			@Override
			public TestDto process(TestDto item) throws Exception {
				return item;
			}
		};
	}
	
	private MyBatisBatchItemWriter<TestDto> writer() {
		return new MyBatisBatchItemWriterBuilder<TestDto>()
					.sqlSessionFactory(sqlSessionFactory)
					.assertUpdates(false)
					.statementId("kr.co.ihopper.acro.sample.mapper.TestMapper.insertTestList")
					.build();
	}
}
