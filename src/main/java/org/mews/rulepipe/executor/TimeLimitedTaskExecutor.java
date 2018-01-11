package org.mews.rulepipe.executor;

import org.mews.rulepipe.config.RulebaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;

@Component
public class TimeLimitedTaskExecutor {

    private final ExecutorService executor;
    private static final Logger LOG = LoggerFactory.getLogger(TimeLimitedTaskExecutor.class);

    @Autowired
    public TimeLimitedTaskExecutor(final RulebaseConfig rulebaseConfig) {
        this.executor = Executors.newFixedThreadPool(rulebaseConfig.getRuleExecutorInstances());
    }

    public <T> T run(
            final Callable<T> callable,
            final long timeout,
            final TimeUnit timeUnit)
            throws Exception {

        final Future<T> future = this.executor.submit(callable);
        try {
            return future.get(timeout, timeUnit);
        } catch (final TimeoutException e) {
            future.cancel(true);
            throw e;
        }
    }

    @PreDestroy
    public void shutdown() throws Exception {
        LOG.info("Shutting down the executor service of: TimeLimitedTaskExecutor");
        this.executor.shutdownNow();
    }

}