package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.atomic.LongAdder;

import static com.example.demo.DemoApplication.NonRetryableException;
import static com.example.demo.DemoApplication.RetryableService;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Resource
    RetryableService service;

	LongAdder invocations;

	@Before
    public void setup() {
	    invocations = new LongAdder();
    }

	@Test
	public void contextLoads() {
	}

    private void executeServiceWithCatch(Runnable runnable) {
        try {
            service.retryableMethod(runnable);
        }
        catch (Exception e) {
        }
    }

    @Test
	public void retryExhausted() {
        executeServiceWithCatch(() -> {
            invocations.increment();
            throw new RuntimeException();
        });

        assertThat(invocations.intValue()).isEqualTo(3);
    }

    @Test
    public void noRetry() {
	    executeServiceWithCatch(() -> invocations.increment());

	    assertThat(invocations.intValue()).isEqualTo(1);
    }

    @Test
    public void retryWithNonRetryableException() {
	    executeServiceWithCatch(() -> {
	        invocations.increment();
	        if (invocations.intValue() > 1) {
	            throw new NonRetryableException();
            }

            throw new RuntimeException();
        });

	    assertThat(invocations.intValue()).isEqualTo(2);
    }
}
