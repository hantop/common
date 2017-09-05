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

import com.common.util.StringUtils;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PreDestroy;
import java.net.UnknownHostException;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Mongo.
 */
@Configuration
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnMissingBean(type = "org.springframework.data.mongodb.MongoDbFactory")
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongodbUrl;

    @Bean(name = "mongoTemplate")
    @RefreshScope
    public MongoTemplate mongoTemplate() throws Exception {
        if (StringUtils.isBlank(mongodbUrl)) {
            return null;
        }
        return new MongoTemplate(mongo(), properties.getDatabase());
    }

    @Autowired(required = false)
    private MongoProperties properties;

    @Autowired(required = false)
    private MongoClientOptions options;

    @Autowired(required = false)
    private Environment environment;

    private MongoClient mongo;

    @PreDestroy
    public void close() {
        if (this.mongo != null) {
            this.mongo.close();
        }
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean
    public MongoClient mongo() throws UnknownHostException {
        if (StringUtils.isBlank(mongodbUrl)) {
            return null;
        }
        com.mongodb.MongoClientURI url = new MongoClientURI(mongodbUrl);
        com.mongodb.MongoClient mongo = new MongoClient(url);
        return mongo;
    }

} 