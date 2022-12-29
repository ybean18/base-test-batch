package exam.test.common.mapper;

import java.util.List;

import exam.test.common.dto.CommonCodeDto;

public interface CommonMapper {
	
	public List<CommonCodeDto> selectCatCdList();
	public List<CommonCodeDto> selectCommonCodeList(CommonCodeDto commonCodeDto);
	public List<CommonCodeDto> selectCommonCodeRuleSetList();
	public List<CommonCodeDto> selectComCommitteeList();
	public List<CommonCodeDto> selectComPoliticalPartyList();
	public List<CommonCodeDto> selectComGovDeptList();
}
