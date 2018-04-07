package com.partygo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.partygo.common.WxMappingJackson2HttpMessageConverter;

@Configuration
public class RestTemplateConfig {
	@Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
		RestTemplate restTemplate = new RestTemplate();
	    restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
	    return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms
        return factory;
    }
}
