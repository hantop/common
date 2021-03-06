/* 
 * Copyright 2012-2015 the original author or authors. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

package com.common.config;

import com.common.mongo.LongToMoneyConvert;
import com.common.mongo.MoneyToLongConvert;
import com.common.mongo.SaveMongoEventListener;
import com.common.util.StringUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import javax.annotation.PreDestroy;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Mongo.
 */
@Configuration
@Conditional(MongodbCondition.class)
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongodbUrl;

    @Value("${spring.data.mongodb.database}")
    private String database;
    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate(MongoDbFactory dbFactory,MappingMongoConverter converter) throws Exception {
        if (StringUtils.isBlank(mongodbUrl)) {
            throw new Exception("mongodb load error url"+mongodbUrl);
        }
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(dbFactory,converter);
    }
    @Bean
    public CustomConversions customConversions() {
        List list = new ArrayList();
        list.add(new MoneyToLongConvert());
        list.add(new LongToMoneyConvert());
        return new CustomConversions(list);
    }
    @Bean
    public MongoDbFactory dbFactory() throws UnknownHostException {
        return new SimpleMongoDbFactory(mongo(), database);
    }
    @Bean
    public MongoMappingContext mappingContext(){
        return new MongoMappingContext();
    }
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory  factory, MongoMappingContext context,CustomConversions customConversions) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));//去掉默认mapper添加的_class
        mappingConverter.setCustomConversions(customConversions);//添加自定义的转换器
        return mappingConverter;
    }
    private MongoClient mongo;

    @PreDestroy
    public void close() {
        if (this.mongo != null) {
            this.mongo.close();
        }
    }
    @Bean
    public SaveMongoEventListener mongoEventListener(){
        return new SaveMongoEventListener();
    }

    @Bean
    public MongoClient mongo() throws UnknownHostException {
        if (StringUtils.isBlank(mongodbUrl)) {
            return null;
        }
        com.mongodb.MongoClientURI url = new MongoClientURI(mongodbUrl);
        com.mongodb.MongoClient mongo = new MongoClient(url);
        return mongo;
    }

}