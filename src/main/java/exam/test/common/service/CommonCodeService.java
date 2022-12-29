package exam.test.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import exam.test.common.dto.CommonCodeDto;
import exam.test.common.mapper.CommonMapper;

@Component
public class CommonCodeService {

	@Autowired
	private CommonMapper commonMapper;

	@Cacheable(value="commonCodeCache")
	public Map<String, Map<String, String>> initCommonCode(String commonCode) {

		Map<String, String>              comCdMap  = new HashMap<>();
		Map<String, Map<String, String>> resultMap = new HashMap<>();
		List<CommonCodeDto>              comCdList = new ArrayList<>();

		List<CommonCodeDto> catCdList = commonMapper.selectCatCdList();

		for (CommonCodeDto dto : catCdList) {
			comCdList = new ArrayList<>();
			comCdList = commonMapper.selectCommonCodeList(dto);

			comCdMap = new HashMap<>();
			comCdMap = comCdList.stream()
						.collect(Collectors.toMap(CommonCodeDto::getDetl_cd, CommonCodeDto::getDetl_cd_alias_nm));

			resultMap.put(dto.getCat_cd(), comCdMap);
		}

		/* 소관위원회 정보 */
		comCdList = new ArrayList<>();
		comCdList = commonMapper.selectComCommitteeList();

//		if (comCdList != null && comCdList.size() > 0) {
//			
//			comCdMap = new HashMap<>();
//			comCdMap = comCdList.stream()
//						.collect(Collectors.toMap(CommonCodeDto::getDetl_cd, CommonCodeDto::getDetl_cd_alias_nm));
//
//			resultMap.put("cmte_no", comCdMap);
//		}
//
//		/* 정당정보 */
//		comCdList = new ArrayList<>();
//		comCdList = commonMapper.selectComPoliticalPartyList();
//
//		if (comCdList != null && comCdList.size() > 0) {
//			
//			comCdMap = new HashMap<>();
//			comCdMap = comCdList.stream()
//						.collect(Collectors.toMap(CommonCodeDto::getDetl_cd, CommonCodeDto::getDetl_cd_alias_nm));
//
//			resultMap.put("pparty_cd", comCdMap);
//		}
//		
//		/* 부처정보 */
//		comCdList = new ArrayList<>();
//		comCdList = commonMapper.selectComGovDeptList();
//
//		if (comCdList != null && comCdList.size() > 0) {
//			
//			comCdMap = new HashMap<>();
//			comCdMap = comCdList.stream()
//						.collect(Collectors.toMap(CommonCodeDto::getDetl_cd, CommonCodeDto::getDetl_cd_alias_nm));
//
//			resultMap.put("dept_no", comCdMap);
//		}

		return resultMap;
	}

	@Cacheable(value="commonCodeRuleSetCache")
	public Map<String, CommonCodeDto> initCommonCodeRuleSet(String commCodeRuleSet) {

		Map<String, CommonCodeDto> resultMap = new HashMap<>();

//		List<CommonCodeDto> detlCdList = commonMapper.selectCommonCodeRuleSetList();
//
//		for (CommonCodeDto dto : detlCdList) {
//			resultMap.put(dto.getDetl_cd(), dto);
//		}

		return resultMap;
	}
}
