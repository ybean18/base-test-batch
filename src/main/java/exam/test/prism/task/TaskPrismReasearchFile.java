package exam.test.prism.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import exam.test.core.common.AcroCommonApiRes;
import exam.test.core.common.AcroCommonApiResData;
import exam.test.core.common.AcroCommonFileRes;
import exam.test.core.common.utils.FileUtils;
import exam.test.core.godata.api.GodataApiLists;
import exam.test.core.godata.api.dto.ResearchDetailDto;
import exam.test.core.godata.api.dto.ResearchDetailDto.ResearchFileList;
import exam.test.core.godata.api.service.GodataApi;
import exam.test.prism.dto.PrismResearchDto;
import exam.test.prism.mapper.PrismMapper;

@Component
public class TaskPrismReasearchFile {
	
	@Autowired
	private GodataApi godataApi;
	
	@Autowired
	private PrismMapper prismMapper;
	
	@SuppressWarnings("unused")
	private Map<String, String> jobparametersMap = new HashMap<>();
	
	public Tasklet taskPrismReasearchFile(Map<String, String> jobparametersMap) {

		this.jobparametersMap = jobparametersMap;

		return new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

					Map<String, String> paramMap      = new HashMap<>();
					List<PrismResearchDto> idList     = new ArrayList<>();
					
					ResearchDetailDto item;
					
					idList = prismMapper.selectResearchId();
					
					for (PrismResearchDto dto : idList) {

					paramMap.put("pageNo", "1");

					paramMap.put("research_id",dto.getResearch_id());
					
					Boolean hasNext = true;

					while (hasNext) {

						AcroCommonApiRes acroCommonApiRes = godataApi.get(GodataApiLists.RESEARCHDETAIL.apiCode(), paramMap);
						AcroCommonApiResData result = acroCommonApiRes.getData();

						// API 호출 값 저장
						List<?>  resList = result.getResearchFileList();

						// DB 저장
						if(resList != null) {
							for(Object file : resList) {
								item = (ResearchDetailDto)file;
								if(result.getResearch_id() != null) {
								item.setResearch_id(result.getResearch_id());
								prismMapper.insertPrismResearchFile(item);
								}
							}
						}
						// 페이징 처리
						paramMap.put("pageNo", String.valueOf(acroCommonApiRes.getHeader().getNextPIndex()));
						hasNext = acroCommonApiRes.getHeader().isHasNextPIndex();

						}

						// rowNum 까지 반복
//						if(dto.rowNum == 19) {
//							break;
//						}
					}
					
					return RepeatStatus.FINISHED;
				}
			};
		}
	}

