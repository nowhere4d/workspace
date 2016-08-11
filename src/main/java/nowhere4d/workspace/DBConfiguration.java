package nowhere4d.workspace;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.sf.log4jdbc.Log4jdbcProxyDataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = { "nowhere4d.workspace.**.mapper" })
public class DBConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public DataSource dataSource() throws IOException {
//		org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
//		dataSource.setDriverClassName("org.postgresql.Driver");
//		dataSource.setUrl("jdbc:postgresql://localhost:5432/nowhere4d");
//		dataSource.setUsername("nowhere4d");
//		dataSource.setPassword("nowhere4d");
//		dataSource.setMaxActive(10);
//		dataSource.setMaxIdle(10);
//		dataSource.setMinIdle(10);
//		dataSource.setMaxWait(3000);
//		dataSource.setTestOnBorrow(true);
//		dataSource.setValidationQuery("SELECT 1");
//		dataSource.setTestOnReturn(false);
//		return new Log4jdbcProxyDataSource(dataSource);
		
		// no need shutdown, EmbeddedDatabaseFactoryBean will take care of this
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase db = builder
			.setType(EmbeddedDatabaseType.HSQL) //.H2 or .DERBY
			.addScript("META-INF/create-table.sql")
			.addScript("META-INF/insert-data.sql")
			.build();
		return new Log4jdbcProxyDataSource(db);
	}

	@Bean
	public PlatformTransactionManager transactionManager() throws Exception {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager((DataSource) applicationContext.getBean("dataSource"));
		transactionManager.setGlobalRollbackOnParticipationFailure(false);
		return transactionManager;
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource((DataSource) applicationContext.getBean("dataSource"));
		sessionFactoryBean.setTypeAliasesPackage("nowhere4d.**.model");
		sessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:META-INF/mybatis/mybatis-config.xml"));
		sessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:META-INF/mybatis/mapper/**/*.xml"));
		return sessionFactoryBean.getObject();
	}
}