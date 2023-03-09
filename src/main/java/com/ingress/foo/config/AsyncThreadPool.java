package com.ingress.foo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Spring Asynchronous Task Pool Configuration
 * <p>
 * As requests come in threads will be created up to "corePoolSize", then tasks
 * will be added to the queue until it reaches "queueCapacity". When the queue
 * is full new threads will be created up to maxPoolSize. Once all the threads
 * are in use and the queue is full tasks will be rejected. As the queue reduces
 * so does the number of active threads.
 * </p>
 * 
 * <p>
 * If the number of threads is less than the corePoolSize, create a new Thread
 * to run a new task. If the number of threads is equal (or greater than) the
 * corePoolSize, put the task into the queue. If the queue is full, and the
 * number of threads is less than the maxPoolSize, create a new thread to run
 * tasks in. If the queue is full, and the number of threads is greater than or
 * equal to maxPoolSize, reject the task.
 * </p>
 *
 * @author anirudh patel
 */

@Configuration
@Slf4j
public class AsyncThreadPool {

	public static final String ASYNC_WRITE_TASK_EXECUTOR = "asyncWritePool-";
	public static final String ASYNC_READ_TASK_EXECUTOR = "asyncReadPool-";

	@Value("${async.core.write-pool-size:64}")
	private int writeCorePoolSize;

	@Value("${async.max.write-pool-size:128}")
	private int writeMaxPoolSize;

	@Value("${async.core.read-pool-size:64}")
	private int readCorePoolSize;

	@Value("${async.max.read-pool-size:128}")
	private int readMaxPoolSize;

	@Value("${async.pool.queue.capacity:256}")
	private int queueCapacity;

	/*
	 * As requests come in, threads will be created up to corePoolSize, then tasks
	 * will be added to the queue until it reaches 100. When the queue is full new
	 * threads will be created up to maxPoolSize. Once all the threads are in use
	 * and the queue is full tasks will be rejected. As the queue reduces so does
	 * the number of active threads.
	 *
	 * If the number of threads is less than the corePoolSize, create a new Thread
	 * to run a new task. If the number of threads is equal (or greater than) the
	 * corePoolSize, put the task into the queue. If the queue is full, and the
	 * number of threads is less than the maxPoolSize, create a new thread to run
	 * tasks in. If the queue is full, and the number of threads is greater than or
	 * equal to maxPoolSize, reject the task.
	 */

	@Bean(name = "asyncWriteTaskPool")
	public Executor getAsyncWriteTaskPool() {
		log.info("AsyncWriteTask : CorePoolSize {} MaxPoolSize {} QueueCapacity {}", writeCorePoolSize, writeMaxPoolSize,
				queueCapacity);
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(writeCorePoolSize);
		executor.setMaxPoolSize(writeMaxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix(ASYNC_WRITE_TASK_EXECUTOR);
//		executor.setAllowCoreThreadTimeOut(true);
		executor.initialize();
		return executor;
	}

	@Bean(name = "asyncReadTaskPool")
	public Executor getAsyncReadTaskPool() {
		log.info("AsyncReadTask : CorePoolSize {} MaxPoolSize {} QueueCapacity {}", readCorePoolSize, readMaxPoolSize,
				queueCapacity);
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(readCorePoolSize);
		executor.setMaxPoolSize(readMaxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix(ASYNC_READ_TASK_EXECUTOR);
//		executor.setAllowCoreThreadTimeOut(true);
		executor.initialize();
		return executor;
	}
}
