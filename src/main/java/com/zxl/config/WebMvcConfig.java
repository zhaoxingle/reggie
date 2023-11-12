package com.zxl.config;

import com.zxl.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j//使用Lombok库的日志注解。
@Configuration //表明这是一个Spring配置类。
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射，因为springboot默认资源路径是static和template下面。但是这个文件resource下面的是backend和front文件夹
     * 因此，需要下面操作。这样就可以通过http://localhost:8080/backend/index.html去访问到资源了。
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //用于配置静态资源映射，以确保能够通过HTTP访问特定路径的静态资源。
        //在这个示例中，/backend/**和/front/**路径下的静态资源将被映射到classpath:/backend/和classpath:/front/的位置。
        //通过http://localhost:8080/backend/index.html可以访问classpath:/backend/index.html。
        log.info("开始静态资源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 在修改状态时，扩展消息转换器
     * 在 Spring MVC 中，当请求被处理并要生成响应时，Spring 会选择合适的消息转换器来将 Java 对象转换为响应的数据格式，
     * 或将请求的数据格式转换为 Java 对象。消息转换器是一个重要的组件，用于处理不同数据类型的互相转换。
     *
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //用于扩展消息转换器，允许您自定义JSON消息的转换方式。
        //在这个示例中，它创建了一个MappingJackson2HttpMessageConverter对象，并设置了自定义的JacksonObjectMapper对象作为对象映射器。
        //通过将自定义的消息转换器添加到converters列表中，并设置索引为0，确保自定义消息转换器优先于默认的Jackson转换器。
        log.info("扩展消息转换器");
        //创建消息转换器
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //设置具体的对象映射器
        converter.setObjectMapper(new JacksonObjectMapper());
        //通过设置索引，让自己的转换器放在最前面，否则会使用默认的jackson转换器会在前面。通过这个转换器，由于js代码会丢失精度，可以将long型的id转换为字符串。从而不丢失精度。
        converters.add(0,converter);
    }


}
/*
`WebMvcConfigurationSupport` 类是 Spring Boot 中用于扩展和自定义 Spring MVC 配置的类，通常需要重写以下方法以进行自定义配置：

1. `addResourceHandlers(ResourceHandlerRegistry registry)`：
   - 用于配置静态资源处理，例如 CSS、JavaScript 和图片文件。您可以使用该方法添加自定义的静态资源映射规则。

2. `configurePathMatch(PathMatchConfigurer configurer)`：
   - 用于配置 URL 路径匹配选项，例如是否启用后缀模式匹配，是否区分大小写等。

3. `addInterceptors(InterceptorRegistry registry)`：
   - 用于注册拦截器，以添加请求预处理和后处理逻辑。您可以在该方法中注册自定义拦截器，并配置它们的拦截路径。

4. `extendMessageConverters(List<HttpMessageConverter<?>> converters)`：
   - 用于扩展或自定义消息转换器。消息转换器用于将 Java 对象转换为 HTTP 请求或响应的内容，您可以在该方法中添加自定义消息转换器。

5. `addCorsMappings(CorsRegistry registry)`：
   - 用于配置跨域资源共享（CORS）规则，以允许或拒绝跨域请求。您可以在该方法中定义允许访问的来源、HTTP方法和头部。

6. `configureViewResolvers(ViewResolverRegistry registry)`：
   - 用于配置视图解析器，以确定如何解析视图名称。您可以在该方法中注册自定义的视图解析器。

7. `configureContentNegotiation(ContentNegotiationConfigurer configurer)`：
   - 用于配置内容协商选项，包括如何确定请求的响应内容类型。您可以在该方法中配置内容协商的策略。

8. `configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer)`：
   - 用于配置默认的 Servlet 处理，以处理静态资源的请求。如果您使用了默认的 Servlet 处理来处理静态资源，可以在该方法中启用它。

9. `addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers)` 和 `addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers)`：
   - 用于配置自定义的方法参数解析器和返回值处理器，以自定义请求处理方法的参数和返回值的处理方式。

10. `configureViewResolvers(ViewResolverRegistry registry)`：
    - 用于配置视图解析器，以确定如何解析视图名称。您可以在该方法中注册自定义的视图解析器。

这些方法提供了广泛的自定义选项，以适应不同应用程序的需求。您可以根据您的具体需求选择重写其中的一个或多个方法，以实现自定义的 Spring MVC 配置。
 */
