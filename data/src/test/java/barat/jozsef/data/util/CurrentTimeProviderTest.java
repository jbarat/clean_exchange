package barat.jozsef.data.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrentTimeProviderTest {

    @Test
    public void shouldReturnCorrectTimeFormat_whenRequested() {
        CurrentTimeProvider currentTimeProvider = givenACurrentTimeProvider();

        assertThat(currentTimeProvider.getCurrentTime()).isInstanceOf(Long.class);
    }

    private CurrentTimeProvider givenACurrentTimeProvider() {
        return new CurrentTimeProvider();
    }
}