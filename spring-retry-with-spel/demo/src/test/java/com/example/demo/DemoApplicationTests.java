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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

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

    @Test
	public void retryExhausted() {
        Throwable thrown = catchThrowable(() -> service.retryableMethod(() -> {
            invocations.increment();
            throw new RuntimeException();
        }));

        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(invocations.intValue()).isEqualTo(3);
    }

    @Test
    public void noRetry() {
	    service.retryableMethod(() -> invocations.increment());

	    assertThat(invocations.intValue()).isEqualTo(1);
    }

    @Test
    public void retryWithNonRetryableException() {
        Throwable thrown = catchThrowable(() -> service.retryableMethod(() -> {
            invocations.increment();
            if (invocations.intValue() > 1) {
                throw new NonRetryableException();
            }

            throw new RuntimeException();
        }));

        assertThat(thrown).isInstanceOf(NonRetryableException.class);
	    assertThat(invocations.intValue()).isEqualTo(2);
    }
}
