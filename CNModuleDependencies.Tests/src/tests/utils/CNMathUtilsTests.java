package tests.utils;

import com.colonolnutty.module.shareddata.utils.CNMathUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * User: Jack's Computer
 * Date: 12/14/2017
 * Time: 12:37 PM
 */
public class CNMathUtilsTests {

    //roundTwoDecimalPlaces
    @Test
    public void should_round_to_two_decimal_places() {
        Double value = 200.564555;
        Double expected = 200.56;
        Double result = CNMathUtils.roundTwoDecimalPlaces(value);
        assertEquals(expected, result);
    }

    @Test
    public void should_round_to_two_decimal_places_without_decimals() {
        Double value = 200.0;
        Double expected = 200.0;
        Double result = CNMathUtils.roundTwoDecimalPlaces(value);
        assertEquals(expected, result);
    }

    @Test
    public void should_round_to_two_decimal_places_rounding_up() {
        Double value = 200.056;
        Double expected = 200.06;
        Double result = CNMathUtils.roundTwoDecimalPlaces(value);
        assertEquals(expected, result);
    }
    //roundTwoDecimalPlaces
}
