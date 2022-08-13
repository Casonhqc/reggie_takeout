package com.Cason.reggie.config;

import com.Cason.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 为了让页面请求不走mvc，而是直接请求指定的地址
 */
@Configuration
@Slf4j
public class WebMvcConfig  extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        log.info("映射开始");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");

    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展转换器");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，把java对象转换成json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将设置好的转换器放到集合里
        converters.add(0,messageConverter);
    }
}
