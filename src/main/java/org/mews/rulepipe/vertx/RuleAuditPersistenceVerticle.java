package org.mews.rulepipe.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mews.rulepipe.entity.RuleAudit;
import org.mews.rulepipe.repository.RuleAuditRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by saipkri on 18/08/17.
 */
@Component
@Scope(SCOPE_PROTOTYPE)
public class RuleAuditPersistenceVerticle extends AbstractVerticle {

    private final RuleAuditRepository ruleAuditRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public RuleAuditPersistenceVerticle(final RuleAuditRepository ruleAuditRepository) {
        this.ruleAuditRepository = ruleAuditRepository;
    }

    @Override
    public void start(final Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        getVertx().eventBus().consumer(RuleAuditPersistenceVerticle.class.getName(), this::exec);
    }

    private void exec(final Message<Object> msg) {
        try {
            String payload = msg.body().toString();
            RuleAudit ruleAudit = objectMapper.readValue(payload, RuleAudit.class);
            ruleAuditRepository.save(ruleAudit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
