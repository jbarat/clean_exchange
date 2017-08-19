package barat.jozsef.revolutexchange.presentation.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DoubleUtilTest {

    @Test
    public void shouldReturnZero_whenEmptyCharSequence(){
        assertThat(DoubleUtil.parseDouble("")).isEqualTo(0.0);
    }

    @Test
    public void shouldReturnZero_whenNullCharSequence(){
        assertThat(DoubleUtil.parseDouble(null)).isEqualTo(0.0);
    }

    @Test
    public void shouldReturnZero_whenMalformedCharSequence(){
        assertThat(DoubleUtil.parseDouble("sdf345")).isEqualTo(0.0);
    }

    @Test
    public void shouldCorrectNumber_whenCorrectCharSequence(){
        assertThat(DoubleUtil.parseDouble("345.45")).isEqualTo(345.45);
    }
}