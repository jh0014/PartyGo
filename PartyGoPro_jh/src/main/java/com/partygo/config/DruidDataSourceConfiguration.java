package com.partygo.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration  
public class DruidDataSourceConfiguration {  
  
	@Value("${spring.datasource.url}")    
    private String dbUrl;
	
	@Value("${spring.datasource.username}")    
    private String username;    
        
    @Value("${spring.datasource.password}")    
    private String password;
    
    @Value("${spring.datasource.driverClassName}")    
    private String driverClassName;
    
    @Value("${spring.datasource.initialSize}")    
    private int initialSize;
    
    @Value("${spring.datasource.minIdle}")    
    private int minIdle;
    
    @Value("${spring.datasource.testWhileIdle}")    
    private boolean testWhileIdle;
    
    @Value("${spring.datasource.testOnBorrow}")    
    private boolean testOnBorrow;    
        
    @Value("${spring.datasource.testOnReturn}")    
    private boolean testOnReturn;
	
    @Value("${spring.datasource.maxActive}")    
    private int maxActive;    
        
    @Value("${spring.datasource.maxWait}")    
    private int maxWait;    
        
    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")    
    private int timeBetweenEvictionRunsMillis;
    
    @Value("${spring.datasource.poolPreparedStatements}")    
    private boolean poolPreparedStatements;    
        
    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")    
    private int maxPoolPreparedStatementPerConnectionSize;    
        
    @Value("{spring.datasource.connectionProperties}")    
    private String connectionProperties;
    
    @Value("${spring.datasource.validationQuery}")    
    private String validationQuery; 
    
    @Value("${spring.datasource.minEvictableIdleTimeMillis}")    
    private int minEvictableIdleTimeMillis;
    
    @Bean  
    @ConfigurationProperties(prefix = "spring.datasource")  
    public DataSource druidDataSource() {  
        DruidDataSource druidDataSource = new DruidDataSource();  
        druidDataSource.setUrl(dbUrl);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName(driverClassName);
        
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        druidDataSource.setValidationQuery(validationQuery);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        
        return druidDataSource;  
    }  
}  