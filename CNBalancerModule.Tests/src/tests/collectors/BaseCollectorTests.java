package tests.collectors;

import main.collectors.BaseCollector;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * User: Jack's Computer
 * Date: 02/09/2018
 * Time: 11:22 AM
 */
public class BaseCollectorTests {
    private BaseCollector _collector;

    public BaseCollectorTests() {
        _collector = new BaseCollector();
    }

    //calculateValue
    @Test
    public void should_return_zero_when_increasePercentage_is_null() {
        Double count = 5.0;
        Double value = 1.0;
        Double increasePercentage = null;
        Double expected = 0.0;
        Double result = _collector.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_return_zero_when_value_is_null() {
        Double count = 5.0;
        Double value = null;
        Double increasePercentage = 0.0;
        Double expected = 0.0;
        Double result = _collector.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_return_zero_when_count_is_null() {
        Double count = null;
        Double value = 25.0;
        Double increasePercentage = 0.0;
        Double expected = 0.0;
        Double result = _collector.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_use_count_of_one_when_count_less_than_zero() {
        Double count = -1.0;
        Double value = 25.0;
        Double increasePercentage = 0.0;
        Double expected = 25.0;
        Double result = _collector.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_use_count_of_one_when_count_is_zero() {
        Double count = 0.0;
        Double value = 25.0;
        Double increasePercentage = 0.0;
        Double expected = 25.0;
        Double result = _collector.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_use_value_of_zero_when_value_less_than_zero() {
        Double count = 5.0;
        Double value = -1.0;
        Double increasePercentage = 0.0;
        Double expected = 0.0;
        Double result = _collector.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_use_value_of_zero_when_value_is_zero() {
        Double count = 1.0;
        Double value = 0.0;
        Double increasePercentage = 0.0;
        Double expected = 0.0;
        Double result = _collector.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_calculate_with_increase_percentage() {
        Double count = 10.0;
        Double value = 5.0;
        Double increasePercentage = 0.5;
        Double expected = 52.5;
        Double result = _collector.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }
    //calculateValue
}
