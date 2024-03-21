package com.example.demovisitcounter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class VisitService {

    private VisitRepository visitRepository;

    private VisitCounterObserver visitCounterObserver;

    private VisitServiceConfig visitServiceConfig;

    private final int COUNTER_ITERATION_WAIT_TIME = 100;

    public VisitService(ApplicationContext context, VisitServiceConfig visitServiceConfig,
            VisitCounterObserver visitCounterObserver) {
        this.visitServiceConfig = visitServiceConfig;
        this.visitRepository = (VisitRepository) context.getBean(this.visitServiceConfig.getRepositoryType());
        this.visitCounterObserver = visitCounterObserver;
    }

    public void incrementCounterConcurrently(int visitId, int threadPoolSize, int counterIterations) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < threadPoolSize; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<Long> executionTimes = new ArrayList<>();

                for (int j = 0; j < counterIterations; j++) {
                    long startTime = System.nanoTime();
                    visitRepository.incrementCounter(visitId);

                    long endTime = System.nanoTime();
                    long executionTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                    executionTimes.add(executionTime);

                    try {
                        Thread.sleep(COUNTER_ITERATION_WAIT_TIME);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }

                visitCounterObserver.onOperationFinished(executionTimes);
            }, executorService);
            futures.add(future);
        }

        CompletableFuture<?>[] futuresArray = futures.toArray(new CompletableFuture<?>[0]);
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futuresArray);
        try {
            allOf.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}
