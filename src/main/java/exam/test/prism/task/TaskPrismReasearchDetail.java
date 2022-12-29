package exam.test.prism.task;

import java.util.ArrayList;
import java.util.Arrays;
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
import exam.test.core.godata.api.dto.ResearchDetailDto;
import exam.test.core.godata.api.service.GodataApi;
import exam.test.prism.dto.PrismResearchDto;
import exam.test.prism.mapper.PrismMapper;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class TaskPrismReasearchDetail {
	
	@Autowired
	private GodataApi godataApi;
	
	@Autowired
	private PrismMapper prismMapper;
	
	@SuppressWarnings("unused")
	private Map<String, String> jobparametersMap = new HashMap<>();
	
	public Tasklet taskPrismReasearchDetail(Map<String, String> jobparametersMap) {

		this.jobparametersMap = jobparametersMap;

		return new Tasklet() {

		@Override
		public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

				Map<String, String>  paramMap   = new HashMap<>();
				List<PrismResearchDto> idList   = new ArrayList<>();
				
				ResearchDetailDto item;
				
				idList = prismMapper.selectResearchId();

				for (PrismResearchDto dto : idList) {

//					if(dto.rowNum > 9000) {
					
					paramMap.put("research_id","1310000-200800015");

				Boolean hasNext = true;

				while (hasNext) {

					AcroCommonApiRes acroCommonApiRes = godataApi.get(GodataApiLists.RESEARCHDETAIL.apiCode(), paramMap);
					AcroCommonApiResData result = acroCommonApiRes.getData();

					// API 호출 값 저장
					ResearchDetailDto  resList = result.getResearchDetailDto();
					List<?> fileList = result.getResearchFileList();

					// resList arrayList로 변환
					List<ResearchDetailDto> array = Arrays.asList(resList);

					if (acroCommonApiRes.getError() != null) {

						log.error(acroCommonApiRes.getError().toString());

						// LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR
						if ("22".equals(acroCommonApiRes.getError().getCode())) {
							stepContribution.getStepExecution().getJobExecution().stop();
							break;
						}
					}

					// research_id 값 세팅
					if(resList != null) {
						resList.setResearch_id(result.getResearch_id());
						}else {
							break;
						}

					// DB DetailList 저장
					for(Object detail : array) {
						item = (ResearchDetailDto)detail;
						prismMapper.insertPrismResearchDetail(item);
					}
					
					// DB fileList 저장
					if(fileList != null) {
						int noCPR = 1;
						int noCPV = 1;
						int noUSR = 1;
						for(Object file : fileList) {
							item = (ResearchDetailDto)file;
							String seq = "&seqNo=00";
							String path = item.getFile_sply_path();
							
							
							if(result.getResearch_id() != null) {

							item.setResearch_id(result.getResearch_id());

//								if(item.getOrgn_file_nm().contains("pdf")) {
								
									if(item.getFile_sply_path().contains("CPR")) {
										item.setFile_sply_path(path.concat(seq+noCPR));
										noCPR++;
									}
		
									if(item.getFile_sply_path().contains("CPV")) {
										item.setFile_sply_path(path.concat(seq+noCPV));
										noCPV++;
									}
		
									if(item.getFile_sply_path().contains("USR")) {
										item.setFile_sply_path(path.concat(seq+noUSR));
										noUSR++;
									}
//								}else {
//									continue;
//								}
							
							prismMapper.insertPrismResearchFile(item);
							}
						}
					}

					// 페이징 처리
					paramMap.put("pageNo", String.valueOf(acroCommonApiRes.getHeader().getNextPIndex()));
					hasNext = acroCommonApiRes.getHeader().isHasNextPIndex();
					}
					System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+dto.rowNum);
					// rowNum 까지 반복
//					if(dto.research_id == "1371000-200800039") {
//						break;
//						}
					break;
						}
					
//					}
				return RepeatStatus.FINISHED;
			}
		};
	}
}