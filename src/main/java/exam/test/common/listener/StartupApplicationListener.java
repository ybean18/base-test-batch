package exam.test.common.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import exam.test.common.service.CommonCodeService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private CommonCodeService commonCodeService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		log.info("onApplicationEvent");

		// 공통코드 캐싱처리
		commonCodeService.initCommonCode("commonCode");
		commonCodeService.initCommonCodeRuleSet("commonCodeRuleSet");
	}
}
