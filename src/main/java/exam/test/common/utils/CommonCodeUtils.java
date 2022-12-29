package exam.test.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonCodeUtils {

	@Autowired
	private CacheUtils cacheUtils;

	@SuppressWarnings("unchecked")
	public String getNameByCd(String paramCatCd, String paramDetlCd) {

		Map<String, ?>      cashComCdMap = new HashMap<>();
		Map<String, String> comCdValMap  = new HashMap<>();

		String detlCdNm = "";

		if (paramCatCd == null || paramCatCd.isBlank()) {
			return "";
		}

		if (paramDetlCd == null || paramDetlCd.isBlank()) {
			return "";
		}

		cashComCdMap = cacheUtils.getCacheMap("commonCodeCache");
		comCdValMap  = (Map<String, String>) cashComCdMap.get(paramCatCd);

		detlCdNm = comCdValMap.get(paramDetlCd);

		if (detlCdNm == null) {
			detlCdNm = "";
		}

		return detlCdNm;
	}

	@SuppressWarnings("unchecked")
	public String getCodeByNm(String paramCatCd, String paramDetlCdNm) {

		Map<String, ?>      cashComCdMap = new HashMap<>();
		Map<String, String> comCdValMap  = new HashMap<>();
		List<String>        splitList    = new ArrayList<>();

		String detlCd    = "";

		if (paramCatCd == null || paramCatCd.isBlank()) {
			return "";
		}

		if (paramDetlCdNm == null || paramDetlCdNm.isBlank()) {
			return "";
		}

		cashComCdMap = cacheUtils.getCacheMap("commonCodeCache");
		comCdValMap  = (Map<String, String>) cashComCdMap.get(paramCatCd);

		for (String key : comCdValMap.keySet()) {

			splitList = Arrays.asList(String.valueOf(comCdValMap.get(key)).split(","));

			for (String val : splitList) {

				if (val != null && !"".equals(val) && (val.replaceAll(" ", "")).equals(paramDetlCdNm.replaceAll(" ", ""))) {

					detlCd = key;
					break;
				}
			}
		}

		return detlCd;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getCodeCat(String paramCatCd) {
		Map<String, ?>      cashComCdMap = new HashMap<>();
		Map<String, String> comCdValMap  = new HashMap<>();

		if (paramCatCd == null || paramCatCd.isBlank()) {
			return comCdValMap;
		}

		cashComCdMap = cacheUtils.getCacheMap("commonCodeCache");
		comCdValMap  = (Map<String, String>) cashComCdMap.get(paramCatCd);
		
		return comCdValMap;
	}
	
	@SuppressWarnings("unchecked")
	public String getCodeByIncludeNm(String paramCatCd, String paramDetlCdNm) {

		Map<String, ?>      cashComCdMap = new HashMap<>();
		Map<String, String> comCdValMap  = new HashMap<>();
		List<String>        splitList    = new ArrayList<>();

		String detlCd    = "";

		if (paramCatCd == null || paramCatCd.isBlank()) {
			return "";
		}

		if (paramDetlCdNm == null || paramDetlCdNm.isBlank()) {
			return "";
		}

		cashComCdMap = cacheUtils.getCacheMap("commonCodeCache");
		comCdValMap  = (Map<String, String>) cashComCdMap.get(paramCatCd);

		for (String key : comCdValMap.keySet()) {

			splitList = Arrays.asList(String.valueOf(comCdValMap.get(key)).split(","));

			for (String val : splitList) {

				if(paramDetlCdNm.indexOf(val) > -1) {
					detlCd = key;
					break;
				} else if(val.indexOf(paramDetlCdNm) > -1) {
					detlCd = key;
					break;
				}
			}
		}

		return detlCd;
	}
}
