package dev.aftermath.sdk.transport;

import dev.aftermath.sdk.model.IncidentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncEventDispatcher implements EventTransport {

    private static final Logger log = LoggerFactory.getLogger(AsyncEventDispatcher.class);
    private final EventTransport delegate;
    private final ExecutorService executor;

    public AsyncEventDispatcher(EventTransport delegate) {
        this.delegate = delegate;
        // Bounded thread pool with bounded queue (100 capacity max) for fail-open safety
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
        this.executor = new ThreadPoolExecutor(
                2, 4, 60L, TimeUnit.SECONDS, queue,
                new ThreadFactory() {
                    // Fix BUG-005: Use AtomicInteger for thread safety in ThreadFactory
                    private final AtomicInteger count = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "aftermath-dispatcher-" + count.incrementAndGet());
                        t.setDaemon(true);
                        return t;
                    }
                },
                new ThreadPoolExecutor.DiscardOldestPolicy() // Drop oldest event if buffer is full
        );
    }

    @Override
    public void send(IncidentEvent event) {
        try {
            executor.submit(() -> delegate.send(event));
        } catch (Exception e) {
            log.warn("Aftermath: Dispatcher queue full or rejected event {}", event.getIncidentId());
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
