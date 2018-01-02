package tests.models.json;

import com.colonolnutty.module.shareddata.models.json.ValueTypeWrapper;
import org.junit.Test;

import static junit.framework.TestCase.*;

/**
 * User: Jack's Computer
 * Date: 01/02/2018
 * Time: 1:35 PM
 */
public class ValueTypeWrapperTests {
    @Test
    public void isValueType_should_return_true() {
        ValueTypeWrapper wrapper = new ValueTypeWrapper("24");
        assertTrue(wrapper.isValueType());
    }

    @Test
    public void isObject_should_return_false() {
        ValueTypeWrapper wrapper = new ValueTypeWrapper("24");
        assertFalse(wrapper.isObject());
    }

    @Test
    public void isArray_should_return_false() {
        ValueTypeWrapper wrapper = new ValueTypeWrapper("24");
        assertFalse(wrapper.isArray());
    }

    @Test
    public void toString_should_return_null_when_value_is_null() {
        ValueTypeWrapper wrapper = new ValueTypeWrapper(null);
        String result = wrapper.toString();
        assertNull(result);
    }

    @Test
    public void toString_should_return_null_when_value_is_string_null() {
        ValueTypeWrapper wrapper = new ValueTypeWrapper("null");
        String result = wrapper.toString();
        assertNull(result);
    }

    @Test
    public void toString_should_handle_booleans() {
        String expectedResult = "true";
        ValueTypeWrapper wrapper = new ValueTypeWrapper(true);
        String result = wrapper.toString();
        assertEquals(expectedResult, result);
    }

    @Test
    public void toString_should_handle_integers() {
        String expectedResult = "24";
        ValueTypeWrapper wrapper = new ValueTypeWrapper(24);
        String result = wrapper.toString();
        assertEquals(expectedResult, result);
    }

    @Test
    public void toString_should_handle_double() {
        String expectedResult = "24.0";
        ValueTypeWrapper wrapper = new ValueTypeWrapper(24.0);
        String result = wrapper.toString();
        assertEquals(expectedResult, result);
    }

    @Test
    public void toString_should_handle_strings() {
        String expectedResult = "\"yay\"";
        ValueTypeWrapper wrapper = new ValueTypeWrapper("yay");
        String result = wrapper.toString();
        assertEquals(expectedResult, result);
    }

    @Test
    public void toString_should_escape_strings() {
        String expectedResult = "\"\\\"yay\\\"\"";
        ValueTypeWrapper wrapper = new ValueTypeWrapper("\"yay\"");
        String result = wrapper.toString();
        assertEquals(expectedResult, result);
    }
}
