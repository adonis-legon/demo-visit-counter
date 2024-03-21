package com.example.demovisitcounter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ConsoleVisitCounterObserver implements VisitCounterObserver{

	@Override
	public void onOperationFinished(List<Long> executionTimes) {
		System.out.println("Statisitcs for Thread " + Thread.currentThread().getName() + ":");
		printExecutionStatistics(executionTimes);
	}

	private void printExecutionStatistics(List<Long> executionTimes) {
		if (executionTimes.isEmpty()) {
			System.out.println("No execution times recorded.");
			return;
		}

		long min = executionTimes.stream().min(Long::compareTo).orElse(0L);
		long max = executionTimes.stream().max(Long::compareTo).orElse(0L);
		double ave = executionTimes.stream().mapToLong(Long::valueOf).average().orElse(0.0);
		double p50 = percentile(executionTimes, 50);
		double p90 = percentile(executionTimes, 90);
		double p95 = percentile(executionTimes, 95);
		double p99 = percentile(executionTimes, 99);

		System.out.println("Min: " + min + " ms, Max: " + max + " ms, Average: " + ave + " ms, P50: " + p50
				+ " ms, P90: " + p90 + " ms P95: " + p95 + " ms P99: " + p99 + " ms");
	}

	private double percentile(List<Long> executionTimes, int percentile) {
		List<Long> sortedExecutionTimes = executionTimes.stream().sorted().collect(Collectors.toList());
		int index = (int) Math.ceil(percentile / 100.0 * sortedExecutionTimes.size());
		return sortedExecutionTimes.get(index - 1);
	}
    
}
