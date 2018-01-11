package org.sai.predmod.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AppConfig {
    @Value("${workerInstances ?: 8}")
    private int workerInstances;
}
