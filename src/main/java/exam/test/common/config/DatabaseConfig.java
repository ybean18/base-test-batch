package exam.test.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import net.sf.log4jdbc.tools.Log4JdbcCustomFormatter;
import net.sf.log4jdbc.tools.LoggingType;

@Configuration
@MapperScan(basePackages={"exam.test"})
public class DatabaseConfig {

	@Value("${test.datasource.url}")
	private String datasourceUrl;

	@Value("${test.datasource.username}")
	private String datasourceUsername;

	@Value("${test.datasource.password}")
	private String datasourcePassword;

	@Bean
	public DataSource getDataSource() {

		HikariDataSource ds = new HikariDataSource();
		ds.setDriverClassName("org.postgresql.Driver");
		ds.setJdbcUrl(datasourceUrl);
		ds.setUsername(datasourceUsername);
		ds.setPassword(datasourcePassword);

		Log4jdbcProxyDataSource proxyDataSource = new Log4jdbcProxyDataSource(ds);
		Log4JdbcCustomFormatter formatter       = new Log4JdbcCustomFormatter();
		formatter.setLoggingType(LoggingType.MULTI_LINE);
		formatter.setSqlPrefix("\n");
		proxyDataSource.setLogFormatter(formatter);

		return (Log4jdbcProxyDataSource)proxyDataSource;
	}

	@Bean
	public DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(getDataSource());
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(getDataSource());
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mapper/**/*Mapper.xml"));
		sqlSessionFactoryBean.setTypeAliasesPackage("exam.test.**.dto");
		sqlSessionFactoryBean.getObject().getConfiguration().addInterceptor(new DatabaseInterceptor());

		return sqlSessionFactoryBean.getObject();
	}
}