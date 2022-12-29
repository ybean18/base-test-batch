package exam.test.prism.mapper;

import java.util.List;
import java.util.Map;

import exam.test.prism.dto.PrismResearchDto;

public interface PrismMapper {


	public int selectResearchId(Object obj);
	
	public List<PrismResearchDto> selectResearchId();
	public List<PrismResearchDto> selectPrismFileDownload(Map<String, Object> sqlParamMap);
	
	public int insertPrismResearchList(List<?> list);
	public int insertPrismResearchList(Object obj);
	public int insertPrismResearchDetail(List<?> list);
	public int insertPrismResearchDetail(Object obj);
	public int insertPrismResearchFile(List<?> obj);
	public int insertPrismResearchFile(Object obj);
	public int insertPrismResearchMeta(List<?> list);
	
	public int updatePrismFile(Object obj);
}