package com.app.LMS.config;

import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomElasticsearchAnalysisConfigurer implements ElasticsearchAnalysisConfigurer {

    @Override
    public void configure(ElasticsearchAnalysisConfigurationContext context) {
        // Define custom analyzers
        context.analyzer("english").custom()
                .tokenizer("standard")
                .tokenFilters("lowercase", "snowball_english", "asciifolding");

        context.analyzer("name").custom()
                .tokenizer("standard")
                .tokenFilters("lowercase", "asciifolding");

        // Define custom normalizers
        context.normalizer("sort").custom()
                .tokenFilters("lowercase", "asciifolding");
    }
}
