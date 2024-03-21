package com.example.demovisitcounter;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoVisitCounterApplication implements CommandLineRunner {

	@Autowired
	private VisitService visitService;

	public static void main(String[] args) {
		SpringApplication.run(DemoVisitCounterApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (args.length != 3) {
			System.err.println(
					"Usage: java -jar demo-load-test-pg-update.jar <visitId> <threadPoolSize> <counterIterations>");
			System.exit(1);
		}

		int visitId = Integer.parseInt(args[0]);
		int threadPoolSize = Integer.parseInt(args[1]);
		int counterIterations = Integer.parseInt(args[2]);

		System.out.println("Incremeting counter for visitId: " + visitId + ", threadPoolSize: " + threadPoolSize
				+ ", counterIterations: " + counterIterations + "...");

		long startTime = System.nanoTime();
		visitService.incrementCounterConcurrently(visitId, threadPoolSize, counterIterations);

		long endTime = System.nanoTime();
		long executionTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
		int incrementsPerSecond = (int) (threadPoolSize * counterIterations / (double) (executionTime / 1000));

		System.out.println("All counters finished. Total time: " + executionTime + " ms, counters rate: "
				+ incrementsPerSecond + "/s");
	}
}
