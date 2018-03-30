package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;

@EnableRetry
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
    public RetryableService retryableService() {
	    return new RetryableService();
    }

    @Bean
    public RetryableEvaluator retryableEvaluator() {
	    return new RetryableEvaluator();
    }

    public static class RetryableService {
        @Retryable(exceptionExpression = "#{@retryableEvaluator.shouldRetry(#root)}")
        public void retryableMethod(Runnable runnable) {
            runnable.run();
        }
    }

    public static class NonRetryableException extends RuntimeException {
    }

    public static class RetryableEvaluator {
	    public boolean shouldRetry(Throwable t) {
	        // evaluate the throwable to determine if a retry should occur.
	        return !NonRetryableException.class.isInstance(t);
        }
    }
}
