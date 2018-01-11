package org.sai.predmod.vertx;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.VerticleFactory;
import lombok.Data;
import org.sai.predmod.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Configuration
@Data
public class Bootstrap {

    @Autowired
    private final ApplicationContext applicationContext;

    @Autowired
    private AppConfig appConfig;

    private Vertx vertx;

    @PostConstruct
    public void onstartup() throws Exception {
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setBlockedThreadCheckInterval(Integer.MAX_VALUE);
        vertx = Vertx.vertx(vertxOptions);
        VerticleFactory verticleFactory = applicationContext.getBean(SpringVerticleFactory.class);
        vertx.registerVerticleFactory(verticleFactory);
        vertx.deployVerticle(verticleFactory.prefix() + ":" + TrainingVerticle.class.getName(), new DeploymentOptions().setInstances(appConfig.getWorkerInstances()).setWorker(true));
        vertx.deployVerticle(verticleFactory.prefix() + ":" + TrainingFinishedVerticle.class.getName(), new DeploymentOptions().setInstances(appConfig.getWorkerInstances()).setWorker(true));
    }
}
