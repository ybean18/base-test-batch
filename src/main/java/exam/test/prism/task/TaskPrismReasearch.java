package exam.test.prism.task;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import exam.test.core.common.AcroCommonApiRes;
import exam.test.core.common.AcroCommonApiResData;
import exam.test.core.godata.api.GodataApiLists;
import exam.test.core.godata.api.service.GodataApi;
import exam.test.prism.mapper.PrismMapper;

@Component
public class TaskPrismReasearch {
	
	@Autowired
	private GodataApi godataApi;
	
	@Autowired
	private PrismMapper prismMapper;
	
	@SuppressWarnings("unused")
	private Map<String, String> jobparametersMap = new HashMap<>();
	
	public Tasklet taskPrismReasearch(Map<String, String> jobparametersMap) {

		this.jobparametersMap = jobparametersMap;

		return new Tasklet() {

		@Override
		public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

				Map<String, String> paramMap      = new HashMap<>();

				Boolean hasNext = true;

				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

//				// 한달 후 날짜
//				Calendar calp = Calendar.getInstance();
//				calp.add(Calendar.MONTH, +1);
//				Date plus = calp.getTime();
//				String todayp = format.format(plus);
//
//				// 한달 전 날짜
//				Calendar calm = Calendar.getInstance();
//				calm.add(Calendar.MONTH, -1);
//				Date minus = calm.getTime();
//				String todaym = format.format(minus);

				// 페이지 , 시작날짜 , 마지막날짜
				paramMap.put("pageNo", "1");
				paramMap.put("start_date", "10000101");
				paramMap.put("end_date", "29990101");

				while (hasNext) {

					AcroCommonApiRes acroCommonApiRes = godataApi.get(GodataApiLists.RESEARCHLIST.apiCode(), paramMap);
					AcroCommonApiResData resList = acroCommonApiRes.getData();

					// API 호출 저장
					List<?> item = resList.getResearchList();

					if (acroCommonApiRes.getError() != null) {
						break;
					}
		
					if (resList == null || resList.getResearchList().size() == 0) {
						break;
					}

					// DB 저장
					prismMapper.insertPrismResearchList(item);

					// 페이징 처리
					paramMap.put("pageNo", String.valueOf(acroCommonApiRes.getHeader().getNextPIndex()));
					hasNext = acroCommonApiRes.getHeader().isHasNextPIndex();
				}
				return RepeatStatus.FINISHED;
			}
		};
	}
}