package exam.test.prism.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import exam.test.core.common.AcroCommonFileRes;
import exam.test.core.common.utils.FileUtils;
import exam.test.core.godata.api.service.GodataApi;
import exam.test.prism.dto.PrismResearchDto;
import exam.test.prism.mapper.PrismMapper;

@Component
public class TaskPrismReasearchFileData {
	
	@Autowired
	private GodataApi godataApi;
	
	@Autowired
	private PrismMapper prismMapper;
	
	@SuppressWarnings("unused")
	private Map<String, String> jobparametersMap = new HashMap<>();
	
	public Tasklet taskPrismReasearchFileData(Map<String, String> jobparametersMap) {

		this.jobparametersMap = jobparametersMap;

		return new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
				
				Map<String, Object> sqlParamMap = new HashMap<>();
				List<PrismResearchDto> idList     = new ArrayList<>();
				
				// resarch_id 조회
				idList = prismMapper.selectResearchId();

				if (idList == null) {
					return RepeatStatus.FINISHED;
				}
				
				sqlParamMap.put("research_id", idList.get(0).getResearch_id());
				
				// 회의록파일정보 다운로드 대상 조회
				List<PrismResearchDto> FileList = prismMapper.selectPrismFileDownload(sqlParamMap);

				for (PrismResearchDto item : FileList) {

					try {
						downloadFile(item);
					} catch (Exception e) {
						// ignore
					}
				}
					
					return RepeatStatus.FINISHED;
				}
			};
		}
	private void downloadFile(PrismResearchDto item) throws Exception {

			final String DIRECTORY = "testPrism"; // 회의록

			String dir = DIRECTORY + "/" ;

			String fileKey = "";
			fileKey = item.getResearch_id();

			List<AcroCommonFileRes> acroCommonFileResList = FileUtils.fileDownLoadFromUrl(item.getFile_sply_path(), dir, item.getFile_div(), item.getFile_ono(), fileKey);

			if (acroCommonFileResList == null) {
				return;
			}

			AcroCommonFileRes acroCommonFileRes = acroCommonFileResList.get(0);

			item.setFile_save_path(acroCommonFileRes.getFile_save_path());
			item.setOrgn_file_nm(acroCommonFileRes.getOrgn_file_nm());
			item.setSave_file_nm(acroCommonFileRes.getSave_file_nm());
			item.setExt(acroCommonFileRes.getExt());

			prismMapper.updatePrismFile(item);
		}
	}

