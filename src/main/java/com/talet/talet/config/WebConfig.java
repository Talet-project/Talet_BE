package com.talet.talet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${storage.images-root-dir}")
    private String imagesRootDir;  // 예: /Users/.../Talet/images

    @Value("${storage.voices-root-dir}")
    private String voicesRootDir;  // 예: /Users/.../Talet/voices

    @Value("${storage.tts-root-dir}")
    private String ttsRootDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/** -> file:/.../images/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + ensureTrailingSlash(imagesRootDir));

        // /voices/** -> file:/.../voices/
        registry.addResourceHandler("/voices/**")
                .addResourceLocations("file:" + ensureTrailingSlash(voicesRootDir));

        registry.addResourceHandler("/tts/**")
                .addResourceLocations("file:" + ensureTrailingSlash(ttsRootDir));
    }

    private String ensureTrailingSlash(String p) {
        return p.endsWith("/") ? p : p + "/";
    }
}
