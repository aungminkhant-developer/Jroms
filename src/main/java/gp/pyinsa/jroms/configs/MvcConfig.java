package gp.pyinsa.jroms.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import gp.pyinsa.jroms.formatters.DateFormatter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path productUploadDir = Paths.get("./product-photos");
        String productUploadPath = productUploadDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/product-photos/**").addResourceLocations("file:/" + productUploadPath + "/");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DateFormatter());
    }

}
