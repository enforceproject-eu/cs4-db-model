package org.n52.project.enforce.cs4.api;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>
 * WebConfig class.
 * </p>
 *
 * @author Benjamin Pross (b.pross@52north.org)
 * @since 1.0.0
 */
@Configuration
public class Cs4WebConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new Cs4MessageConverter());
    }

    @Bean
    GroupedOpenApi cs4ManorbaApis() {
        return GroupedOpenApi.builder().group("cs4_manorba").displayName("cs4_manorba").pathsToMatch("/cs4_manorba_data/**").build();
    }

    @Bean
    GroupedOpenApi cs4MinkaApis() {
        return GroupedOpenApi.builder().group("cs4_minka").displayName("cs4_minka").pathsToMatch("/cs4_minka_data/**").build();
    }
}
