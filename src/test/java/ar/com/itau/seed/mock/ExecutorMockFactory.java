package ar.com.itau.seed.mock;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ExecutorMockFactory {

    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 8;
    private static final String ASYNC_PREFIX = "async-";
    private static final boolean WAIT_FOR_TASK_TO_COMPLETE_ON_SHUTDOWN = true;

    public static ThreadPoolTaskExecutor get() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setWaitForTasksToCompleteOnShutdown(WAIT_FOR_TASK_TO_COMPLETE_ON_SHUTDOWN);
        executor.setThreadNamePrefix(ASYNC_PREFIX);
        executor.initialize();
        return executor;
    }

}
