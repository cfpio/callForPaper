package io.cfp.config;

import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for sending mail
 */
@Configuration
public class MailConfig {

//    @Bean(name = "mailTemplate")
//    public freemarker.template.Configuration freemarkerConfig() {
//        freemarker.template.Configuration config = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_21);
//        config.setClassForTemplateLoading(MailConfig.class, "/mails/");
//        config.setDefaultEncoding("UTF-8");
//        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//
//        return config;
//    }

}
