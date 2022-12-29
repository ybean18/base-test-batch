package exam.test.common.config;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import exam.test.core.common.annotation.DatabaseEncField;
import exam.test.core.common.utils.AriaUtil;

@Intercepts({
	@Signature(type=Executor.class, method="query",  args={MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	@Signature(type=Executor.class, method="update", args={MappedStatement.class, Object.class})
})
public class DatabaseInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		String queryMethod = invocation.getMethod().getName();
		Object param       = invocation.getArgs()[1];

		if ("update".equals(queryMethod)) {

			enc(param);

		} if ("query".equals(queryMethod)) {

//			enc(param);
			Object result = invocation.proceed();

			if (result != null) {
				dec(result);
			}

			return result;
		}

		return invocation.proceed();
	}

	private void enc(Object param) throws Exception {

		if (param == null) {
			return;
		}

		PropertyDescriptor pd     = null;
		Method             getter = null;
		Method             setter = null;

		// foreach
		if (param instanceof Map) {

			List<?> list = (List<?>) ((Map<?, ?>) param).get("list");

			// SqlParamMap에 대한 예외처리
			if (list == null) {
				return;
			}

			for (Object item : list) {

				Field[] fields = item.getClass().getDeclaredFields();

				for (Field field : fields) {

					if (field.getAnnotation(DatabaseEncField.class) == null) {
						continue;
					}

					pd     = new PropertyDescriptor(field.getName(), item.getClass());
					getter = pd.getReadMethod();
					setter = pd.getWriteMethod();

					// 암호화
					String src = (String) getter.invoke(item);
					String enc = new AriaUtil().encrypt(src);

					setter.invoke(item, enc);
				}
			}

		} else {

			Field[] fields = param.getClass().getDeclaredFields();

			for (Field field : fields) {

				if (field.getAnnotation(DatabaseEncField.class) == null) {
					continue;
				}

				pd     = new PropertyDescriptor(field.getName(), param.getClass());
				getter = pd.getReadMethod();
				setter = pd.getWriteMethod();

				// 암호화
				String src = (String) getter.invoke(param);
				String enc = new AriaUtil().encrypt(src);

				setter.invoke(param, enc);
			}
		}
	}

	private void dec(Object result) throws Exception {

		if (result == null) {
			return;
		}

		PropertyDescriptor pd     = null;
		Method             getter = null;
		Method             setter = null;

		List<?> list = (List<?>) result;

		if (list.size() == 0) {
			return;
		}

		Field[] fields = list.get(0).getClass().getDeclaredFields();

		for (Object item : list) {

			for (Field field : fields) {

				if (field.getAnnotation(DatabaseEncField.class) == null) {
					continue;
				}

				pd     = new PropertyDescriptor(field.getName(), item.getClass());
				getter = pd.getReadMethod();
				setter = pd.getWriteMethod();

				// 복호화
				String src = (String) getter.invoke(item);
				String dec = new AriaUtil().decrypt(src);

				setter.invoke(item, dec);
			}
		}
	}
}