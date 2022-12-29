package exam.test.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import exam.test.common.dto.CommonCodeDto;

@Component
public class CommonCodeConverter {

	@Autowired
	private CacheUtils cacheUtils;

	private Map<String, ?> cachComCdMap = new HashMap<>();

	private Map<String, ?> cashRuleMap  = new HashMap<>();

	private Map<String, Map<String, String>> cvtComCdMap  = new HashMap<>();

	private Map<String, CommonCodeDto>       cvtRuleMap   = new HashMap<>();

	public void convertData(List<?> paramList, String... detlCd) {

		setCashMap();

		for (String catCdkey : cvtRuleMap.keySet()) {

			for (Object obj : paramList) {
				convertCode(obj, cvtRuleMap.get(catCdkey));
			}
		}
	}

	public void convertData(Object paramObj, String... detlCd) {

		setCashMap();

		for (String catCdkey : cvtRuleMap.keySet()) {
			convertCode(paramObj, cvtRuleMap.get(catCdkey));
		}
	}

	@SuppressWarnings("unchecked")
	private void setCashMap(String... detlCd) {

		cachComCdMap = cacheUtils.getCacheMap("commonCodeCache");
		cashRuleMap  = cacheUtils.getCacheMap("commonCodeRuleSetCache");
		
		if (detlCd != null && detlCd.length > 0) {

			for (int i = 0; i < detlCd.length; i++) {

				cvtComCdMap.put(detlCd[i], (Map<String, String>) cachComCdMap.get(detlCd[i]));
				cvtRuleMap.put(detlCd[i], (CommonCodeDto) cashRuleMap.get(detlCd[i]));
			}

		} else {
			cvtComCdMap = (Map<String, Map<String, String>>) cachComCdMap;
			cvtRuleMap  = (Map<String, CommonCodeDto>) cashRuleMap;
		}
	}

	private void convertCode(Object obj, CommonCodeDto item) {

		List<String>        splitList    = new ArrayList<>();
		Map<String, String> comCdValMap  = new HashMap<>();

		Field[] fields  = null;

		String fieldNm  = "";
		String fieldVal = "";
		String codeVal  = "";

		if (item.getRef_cd_1() == null || item.getRef_cd_1().isBlank() || item.getRef_cd_2() == null || item.getRef_cd_2().isBlank()) {
			return;
		}

		try {

			fields = obj.getClass().getDeclaredFields();

			// Filed For Start
			for (Field field : fields) {

				field.setAccessible(true);
				fieldNm  = field.getName();

				// 코드변환룰 컬럼과 동일한 필드인 경우
				if (item.getRef_cd_1().equals(fieldNm)) {

					comCdValMap = cvtComCdMap.get(item.getDetl_cd());
					fieldVal    = (String) field.get(obj);

					// 코드변환 시작
					for (String key : comCdValMap.keySet()) {

						// 소위원회 변환규칙
						if ("cmte_no".equals(item.getDetl_cd())) {

							// 임시로 특수문자 제거(추후 정규식 적용)
							codeVal = comCdValMap.get(key).trim();
							codeVal = codeVal.replaceAll("\\(", "");
							codeVal = codeVal.replaceAll("\\)", "");
							codeVal = codeVal.replaceAll(" ", "");
							codeVal = codeVal.replaceAll("\\·", "");
							codeVal = codeVal.replaceAll("\\,", "");
							codeVal = codeVal.replaceAll("\\.", "");

							if(codeVal.length() > 2 && "특위".equals(codeVal.substring(codeVal.length() - 2))) {
								codeVal = codeVal.replaceAll("특위", "특별위원회");
							}

							if(codeVal.length() > 4 && "특위원회".equals(codeVal.substring(codeVal.length() - 4))) {
								codeVal = codeVal.replaceAll("특위원회", "특별위원회");
							}

							if("위".equals(codeVal.substring(codeVal.length() - 1))) {
								codeVal = codeVal + "원회";
							}

							if (fieldVal != null && !fieldVal.isBlank()) {

								fieldVal = fieldVal.replaceAll("\\(", "");
								fieldVal = fieldVal.replaceAll("\\)", "");
								fieldVal = fieldVal.replaceAll(" ", "");
								fieldVal = fieldVal.replaceAll("\\·", "");
								fieldVal = fieldVal.replaceAll("\\,", "");
								fieldVal = fieldVal.replaceAll("\\.", "");
								fieldVal = fieldVal.replaceAll("기업경쟁력특별", "기업경쟁력강화특별"); //일자리창출및중소기업경쟁력특별위원회

								if(fieldVal.length() > 2 && "특위".equals(fieldVal.substring(fieldVal.length() - 2))) {
									fieldVal = fieldVal.replaceAll("특위", "특별위원회");
								}

								if(fieldVal.length() > 4 && "특위원회".equals(fieldVal.substring(fieldVal.length() - 4))) {
									fieldVal = fieldVal.replaceAll("특위원회", "특별위원회");
								}

								if("위".equals(fieldVal.substring(fieldVal.length() - 1))) {
									fieldVal = fieldVal + "원회";
								}
							}

							if (codeVal.equals(fieldVal)) {

								for (Field resultField : fields) {

									if(item.getRef_cd_2().equals(resultField.getName())) {

										resultField.setAccessible(true);
										resultField.set(obj, key);
										break;
									}
								}

								break;
							}

						// 부처 변환규칙
						} else if ("dept_no".equals(item.getDetl_cd())) {

							codeVal = comCdValMap.get(key).trim();

							if (fieldVal != null && !fieldVal.isBlank()) {
								fieldVal = fieldVal.replaceAll("부마민주항쟁진상규명및관련지명예회복심의위원회", "부마민주항쟁진상규명및관련자명예회복심의위원회");
							}

							if (codeVal.equals(fieldVal)) {

								for (Field resultField : fields) {

									if(item.getRef_cd_2().equals(resultField.getName())) {

										resultField.setAccessible(true);
										resultField.set(obj, key);
										break;
									}
								}

								break;
							}

						} else {

							splitList = Arrays.asList(String.valueOf(comCdValMap.get(key)).split(","));

							for (String val : splitList) {

								if (val != null && !"".equals(val) && val.equals(fieldVal)) {

									for (Field resultField : fields) {

										if(item.getRef_cd_2().equals(resultField.getName())) {

											resultField.setAccessible(true);
											resultField.set(obj, key);
											break;
										}
									}

									break;
								}
							}
						}

//						if (!"cmte_no".equals(item.getDetl_cd())) {
//
//							splitList = Arrays.asList(String.valueOf(comCdValMap.get(key)).split(","));
//
//							for (String val : splitList) {
//
//								if (val != null && !"".equals(val) && val.equals(fieldVal)) {
//
//									for (Field resultField : fields) {
//
//										if(item.getRef_cd_2().equals(resultField.getName())) {
//
//											resultField.setAccessible(true);
//											resultField.set(obj, key);
//											break;
//										}
//									}
//
//									break;
//								}
//							}
//						} else {
//
//							if (comCdValMap.get(key) != null && !"".equals(comCdValMap.get(key))) {
//
//								// 임시로 특수문자 제거(추후 정규식 적용)
//								codeVal = comCdValMap.get(key).trim();
//								codeVal = codeVal.replaceAll("\\(", "");
//								codeVal = codeVal.replaceAll("\\)", "");
//								codeVal = codeVal.replaceAll(" ", "");
//								codeVal = codeVal.replaceAll("\\·", "");
//								codeVal = codeVal.replaceAll("\\,", "");
//								codeVal = codeVal.replaceAll("\\.", "");
//
//								if (fieldVal != null && !fieldVal.isBlank()) {
//
//									fieldVal = fieldVal.replaceAll("\\(", "");
//									fieldVal = fieldVal.replaceAll("\\)", "");
//									fieldVal = fieldVal.replaceAll(" ", "");
//									fieldVal = fieldVal.replaceAll("\\·", "");
//									fieldVal = fieldVal.replaceAll("\\,", "");
//									fieldVal = fieldVal.replaceAll("\\.", "");
//									fieldVal = fieldVal.replaceAll("특위", "특별위원회");
//									fieldVal = fieldVal.replaceAll("기업경쟁력특별", "기업경쟁력강화특별"); //일자리창출및중소기업경쟁력특별위원회
//
//									if("위".equals(fieldVal.substring(fieldVal.length() - 1))) {
//										fieldVal = fieldVal + "원회";
//									}
//								}
//
//								if (codeVal.equals(fieldVal)) {
//
//									for (Field resultField : fields) {
//
//										if(item.getRef_cd_2().equals(resultField.getName())) {
//
//											resultField.setAccessible(true);
//											resultField.set(obj, key);
//											break;
//										}
//									}
//
//									break;
//								}
//							}
//						}
					}
				}
			}

		} catch (IllegalAccessException e) {

		}

	}
}
