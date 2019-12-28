package com.poscoict.assets.config;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.sql.DataSource;

import com.poscoict.assets.chaincode.EERC721;
import com.poscoict.assets.chaincode.ERC721;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import com.poscoict.posledger.chain.wallet.chaincode.ERC20ChaincodeService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chain.ca.FabricCaService;
import com.poscoict.posledger.chain.ca.MembershipService;
import com.poscoict.posledger.chain.ca.usercontext.UserContextResolver;
import com.poscoict.posledger.chain.ca.usercontext.UserContextResolverRedis;
import com.poscoict.posledger.chain.chaincode.executor.ChaincodeExecutor;
import com.poscoict.posledger.chain.chaincode.executor.ChaincodeProxy;
import com.poscoict.posledger.chain.config.PosledgerConfig;
import com.poscoict.posledger.chain.config.YamlConfigurer;
import com.poscoict.posledger.chain.crypto.CryptoSuiteBuilder;


import com.poscoict.posledger.chain.fabric.FabricService;
import com.poscoict.posledger.chain.fabric.FabricServiceBuilder;
import com.poscoict.posledger.chain.model.UserContext;
import com.poscoict.posledger.chain.sign.AesSymmetryCipher;
import com.poscoict.posledger.chain.sign.SymmetryCipher;
import com.poscoict.posledger.chain.sign.certificate.ECDSA;
import com.poscoict.posledger.chain.sign.certificate.ECDSACertificateService;
import com.poscoict.posledger.chain.sign.certificate.PosCertificateService;
import com.poscoict.posledger.chain.sign.certificate.PosSign;
import com.poscoict.posledger.chain.sign.challenge.BasicChallengeService;
import com.poscoict.posledger.chain.sign.challenge.ChallengeService;
import com.poscoict.posledger.chain.sign.client.SignClient;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.poscoict.assets.service", "com.poscoict.assets.persistence"})
@PropertySource(value = {"classpath:application.properties", "classpath:datasource.properties"})
public class SpringConfig {
	
	private static final Logger logger = LogManager.getLogger(SpringConfig.class);

	@Autowired
	private Environment env;
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getRequiredProperty("datasource.jdbc.driver"));
		dataSource.setUrl(env.getRequiredProperty("datasource.jdbc.url"));
		dataSource.setUsername(env.getRequiredProperty("datasource.jdbc.username"));
		dataSource.setPassword(env.getRequiredProperty("datasource.jdbc.password"));
		dataSource.setSchema(env.getRequiredProperty("datasource.jdbc.schema"));
		return dataSource;
	}

	@Bean
	public DataSourceTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
		transactionManager.setGlobalRollbackOnParticipationFailure(false);
		return transactionManager;
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {

		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setConfigLocation(resolver.getResource("classpath:/mybatis/mybatis-config.xml"));

		return (SqlSessionFactory) sqlSessionFactoryBean.getObject();
	}

	@Bean
	public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@Bean(name="messageSource")
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:application");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}
	
	@Bean(name="message")
	public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
		return new MessageSourceAccessor(messageSource);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
	@Bean
	public ChaincodeProxy chaincodeProxy() throws Exception {
		return new ChaincodeExecutor(fabricService());
	}
	
	@Bean
	public PosledgerConfig config() throws Exception {
		return (new YamlConfigurer(env.getProperty("application.posledger.config"))).getPosledgerConfig();
	}
	
	@Bean
	public SignClient signClient() throws Exception {
		return new SignClient(env.getProperty("application.posledger.sign.private.domain"), env.getProperty("application.posledger.sign.client.id"), env.getProperty("application.posledger.sign.client.secret"), env.getProperty("application.posledger.sign.client.domain"));
	}
	
	@Bean
	public PosCertificateService posCertificateService() throws Exception {
		return new ECDSACertificateService(posSign(), symmetryCipher(), objectMapper(), signClient(), challengeService());
	}
	
	@Bean
	public SymmetryCipher symmetryCipher() throws Exception {
		return new AesSymmetryCipher();
	}
	
	@Bean
	public PosSign posSign() throws Exception {
		return new ECDSA();
	}
	
	@Bean
	public ChallengeService challengeService() throws Exception {
		ChallengeService challengeService = new BasicChallengeService();
		challengeService.changeCertificate(env.getProperty("application.posledger.challenge.public.key"));
		return challengeService;
	}
	
	@Bean(destroyMethod = "shutdownChannel")
	public FabricService fabricService() throws Exception {
		
		PosledgerConfig config = config();
		
		UserContextResolver userContextResolver = new UserContextResolverRedis(jedisPool());
		
		UserContext adminUserContext = new UserContext();
		adminUserContext.setName(config.getAdminName());
		adminUserContext.setAffiliation(config.getOrgName());
		adminUserContext.setMspId(config.getOrgMsp());
		
		CryptoSuiteBuilder cryptoSuiteBuilder = new CryptoSuiteBuilder();
		
		MembershipService membershipService = new FabricCaService(cryptoSuiteBuilder, config.getCaUrl(), null, adminUserContext, userContextResolver);
		
		UserContext userContext = membershipService.getUserContext(config);
		
		logger.info("fabric user : " + userContext);
		
		// fabric 서비스 생성
		FabricService fabricService = new FabricServiceBuilder(cryptoSuiteBuilder, userContext, config);
		
		return fabricService;
	}
	
	@Bean(destroyMethod = "close")
	public JedisPool jedisPool() throws Exception {
		
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxWaitMillis(3000);
		jedisPoolConfig.setMaxTotal(10);
		jedisPoolConfig.setMaxIdle(5);
		jedisPoolConfig.setMinIdle(1);
		jedisPoolConfig.setTestOnBorrow(true);
		jedisPoolConfig.setTestOnReturn(true);
		jedisPoolConfig.setTestWhileIdle(true);
		jedisPoolConfig.setNumTestsPerEvictionRun(10);
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(60000);

		// Jedis Pool
		JedisPool jedisPool = new JedisPool(jedisPoolConfig, env.getProperty("application.redis.host"), Integer.parseInt(env.getProperty("application.redis.port")), 1000);
		
		logger.info("JedisPool is initialized.");
		
		return jedisPool;
	}
	
	@Bean
	public StringHttpMessageConverter stringHttpMessageConverter() {
		return new StringHttpMessageConverter();
	}
	
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
		return new MappingJackson2HttpMessageConverter(objectMapper);
	}
	
	@Bean
	public FormHttpMessageConverter formHttpMessageConverter() {
		FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
		formHttpMessageConverter.setCharset(Charset.forName("UTF-8"));
		return formHttpMessageConverter;
	}
	
	@Bean
	public MultipartResolver multipartResolver() throws IOException {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setDefaultEncoding("UTF-8");
		multipartResolver.setUploadTempDir(new FileSystemResource(env.getProperty("application.upload.temp.path")));
		multipartResolver.setMaxUploadSize(10000000);
		return multipartResolver;
	}
	
	@Bean
	public ERC20ChaincodeService erc20ChaincodeService() throws Exception {
		return new ERC20ChaincodeService(chaincodeProxy(), objectMapper(), fabricService());
	}

	@Bean
	public ERC721 erc721() throws Exception {
		return new ERC721(chaincodeProxy(), objectMapper(), fabricService());
	}

	@Bean
	public EERC721 eerc721() throws Exception {
		return new EERC721(chaincodeProxy(), objectMapper(), fabricService());
	}
}
