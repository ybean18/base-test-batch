package exam.test.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheUtils {
	
	@Autowired
	private CacheManager cacheManager;

	@SuppressWarnings("unchecked")
	public Map<String, ?> getCacheMap(String cacheKey) {

		ConcurrentHashMap<String, Map<String, ?>> cacheMap    = new ConcurrentHashMap<>();
		Map<String, ?> cacheDataMap = new HashMap<>();

		for (String key : cacheManager.getCacheNames()) {
			if (cacheKey.equals(key)) {
				cacheMap = (ConcurrentHashMap<String, Map<String, ?>>)cacheManager.getCache(cacheKey).getNativeCache();

				for (String dataKey : cacheMap.keySet()) {
					cacheDataMap = cacheMap.get(dataKey);
					break;
				}

				break;
			}
		}

		return cacheDataMap;
	}

	/**
	 * Evict All Caches
	 */
	public void evictAllCaches() {

		List<String> cacheNameList = new ArrayList<>(cacheManager.getCacheNames());

		for (String cacheName : cacheNameList) {

			// 메뉴 관련 캐시는 제거하지 않음
			if ("commonCodeRuleSetCache".equals(cacheName)) {
				continue;
			}

			evictCache(cacheName);
		}
	}

	/**
	 * Evict Cache
	 * @param cacheName
	 */
	public void evictCache(String cacheName) {
		cacheManager.getCache(cacheName).clear();
	}
}
