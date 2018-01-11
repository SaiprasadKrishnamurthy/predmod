package org.sai.predmod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.sai.predmod.entity.*;
import org.sai.predmod.model.PredictiveAnalyticsService;
import org.sai.predmod.repository.PredictiveModelRepository;
import org.sai.predmod.vertx.Bootstrap;
import org.sai.predmod.vertx.TrainingVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by saipkri on 18/08/17.
 */
@EnableAutoConfiguration
@SpringBootApplication
@EnableSwagger2
@EnableJpaRepositories(basePackages = {"org.sai.predmod.repository"})
@EnableTransactionManagement
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    public CommandLineRunner loadData(final PredictiveModelRepository predictiveModelRepository,
                                      final PredictiveAnalyticsService predictiveAnalyticsService,
                                      final Bootstrap bootstrap) {
        return (args) -> {
            irisClassification(predictiveModelRepository, predictiveAnalyticsService);
            bootstrap.getVertx().eventBus().send(TrainingVerticle.class.getName(), "iris-classification");
        };
    }

    private void irisClassification(PredictiveModelRepository predictiveModelRepository, PredictiveAnalyticsService predictiveAnalyticsService) throws JsonProcessingException {
        PredictiveModelDef def = new PredictiveModelDef();
        def.setId("iris-classification");
        def.setDescription("Iris classification");
        def.setDatasourceType(DatasourceType.CSV);
        def.setDatasourceValue("iris2.csv");
        List<Column> cols = Arrays.asList("sepal-length", "sepal-width", "petal-length", "petal-width")
                .stream()
                .map(col -> {
                    Column in = new Column();
                    in.setName(col);
                    in.setKind(ColumnType.continuous);
                    return in;
                }).collect(toList());
        def.setInputColumns(cols);
        Column out = new Column();
        out.setName("species");
        out.setKind(ColumnType.nominal);
        def.setPredictedColumn(out);
        def.setProblemType(ProblemType.classification);
        def.setModelType(ModelType.feedforward);
        def.setTrainingIterations(2);
        String json = new ObjectMapper().writeValueAsString(def);
        PredictiveModel model = new PredictiveModel();
        model.setPredModelDefJson(json.getBytes());
        model.setPredModelDefId(def.getId());
        predictiveModelRepository.save(model);

        /*System.out.println(new ObjectMapper().writeValueAsString(def));
        System.out.println("Start training ");
        predictiveAnalyticsService.train(model);

        System.out.println(predictiveModelRepository.findAll());

        System.out.println("Start Prediction ");

        Map<String, Object> inputs = ImmutableMap.of("sepal-length", 5.7, "sepal-width", 3.0, "petal-length", 4.2, "petal-width", 1.2);
        System.out.println(predictiveAnalyticsService.predict(model, inputs));

        inputs = ImmutableMap.of("sepal-length", 5.7, "sepal-width", 3.0, "petal-length", 4.2, "petal-width", 1.2);
        System.out.println(predictiveAnalyticsService.predict(model, inputs));

        inputs = ImmutableMap.of("sepal-length", 5.9, "sepal-width", 3.0, "petal-length", 5.1, "petal-width", 1.8);
        System.out.println(predictiveAnalyticsService.predict(model, inputs));

        inputs = ImmutableMap.of("sepal-length", 5.1, "sepal-width", 3.5, "petal-length", 1.4, "petal-width", 0.3);
        System.out.println(predictiveAnalyticsService.predict(model, inputs));

        System.out.println(" ----- ");*/
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
