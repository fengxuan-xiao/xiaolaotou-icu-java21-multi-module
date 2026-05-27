//package com.example.config;
//
//
//import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class DataSourceConfig {
//
//    @Primary
//    @Bean(name = "dataSource")
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource dataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "primaryDataSource")
//    public DataSource primaryDataSource(@Qualifier("dataSource") DataSource dataSource) {
//        return dataSource;
//    }
//
//    @Primary
//    @Bean(name = "primaryTransactionManager")
//    public PlatformTransactionManager primaryTransactionManager(
//            @Qualifier("dataSource") DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//    @Primary
//    @Bean(name = "primarySqlSessionFactory")
//    public SqlSessionFactory primarySqlSessionFactory(
//            @Qualifier("dataSource") DataSource dataSource) throws Exception {
//        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
//        sessionFactoryBean.setDataSource(dataSource);
//        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
//                .getResources("classpath:mapper/**/*.xml"));
//        return sessionFactoryBean.getObject();
//    }
//
//    @Bean(name = "jimureportDataSource")
//    @ConfigurationProperties(prefix = "jimureport.datasource")
//    public DataSource jimureportDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "jimureportTransactionManager")
//    public PlatformTransactionManager jimureportTransactionManager(
//            @Qualifier("jimureportDataSource") DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//    @Bean(name = "jimureportSqlSessionFactory")
//    public SqlSessionFactory jimureportSqlSessionFactory(
//            @Qualifier("jimureportDataSource") DataSource dataSource) throws Exception {
//        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
//        sessionFactoryBean.setDataSource(dataSource);
//        return sessionFactoryBean.getObject();
//    }
//}
