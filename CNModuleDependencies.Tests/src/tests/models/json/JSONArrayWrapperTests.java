package tests.models.json;

import com.colonolnutty.module.shareddata.models.json.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.*;

/**
 * User: Jack's Computer
 * Date: 01/02/2018
 * Time: 1:35 PM
 */
public class JSONArrayWrapperTests {

    @Test
    public void firstItemIsValueType_should_be_false_when_first_item_is_null() {
        JSONArray arr = new JSONArray();
        arr.put((String)null);
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        assertFalse(wrapper.firstItemIsValueType());
    }

    @Test
    public void firstItemIsValueType_should_be_false_when_first_item_is_array() {
        JSONArray arr = new JSONArray();
        JSONArray subArr = new JSONArray();
        arr.put(subArr);
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        assertFalse(wrapper.firstItemIsValueType());
    }

    @Test
    public void firstItemIsValueType_should_be_false_when_first_item_is_object() {
        JSONArray arr = new JSONArray();
        JSONObject obj = new JSONObject();
        arr.put(obj);
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        assertFalse(wrapper.firstItemIsValueType());
    }

    @Test
    public void firstItemIsValueType_should_be_true_when_first_item_is_value_type() {
        JSONArray arr = new JSONArray();
        arr.put(24);
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        assertTrue(wrapper.firstItemIsValueType());
    }

    @Test
    public void get_should_give_null_when_item_is_null() {
        JSONArray arr = new JSONArray();
        arr.put((String)null);
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        IJsonWrapper result = wrapper.get(0);
        assertNull(result);
    }

    @Test
    public void get_should_give_null_when_index_below_zero() {
        JSONArray arr = new JSONArray();
        arr.put(1);
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        IJsonWrapper result = wrapper.get(-1);
        assertNull(result);
    }

    @Test
    public void get_should_give_null_when_index_above_size() {
        JSONArray arr = new JSONArray();
        arr.put(1);
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        IJsonWrapper result = wrapper.get(1);
        assertNull(result);
    }

    @Test
    public void get_should_give_null_when_array_is_null() {
        JSONArrayWrapper wrapper = new JSONArrayWrapper(null);
        IJsonWrapper result = wrapper.get(0);
        assertNull(result);
    }

    @Test
    public void get_should_give_value_type_wrapper_for_value_types() {
        JSONArray arr = new JSONArray();
        arr.put(24.0);
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        IJsonWrapper result = wrapper.get(0);
        assertNotNull(result);
        assertTrue(result instanceof ValueTypeWrapper);
    }

    @Test
    public void get_should_give_json_object_wrapper_for_objects() {
        JSONArray arr = new JSONArray();
        JSONObject subObj = new JSONObject();
        arr.put(subObj);
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        IJsonWrapper result = wrapper.get(0);
        assertNotNull(result);
        assertTrue(result instanceof JSONObjectWrapper);
    }

    @Test
    public void get_should_give_json_array_wrapper_for_arrays() {
        JSONArray arr = new JSONArray();
        JSONArray subArr = new JSONArray();
        arr.put(subArr);
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        IJsonWrapper result = wrapper.get(0);
        assertNotNull(result);
        assertTrue(result instanceof JSONArrayWrapper);
    }

    @Test
    public void size_should_give_zero_with_null_array() {
        JSONArrayWrapper wrapper = new JSONArrayWrapper(null);
        assertEquals(0, wrapper.size());
    }

    @Test
    public void size_should_give_size() {
        JSONArray arr = new JSONArray();
        arr.put("blah");
        arr.put("blah2");
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        assertEquals(2, wrapper.size());
    }

    @Test
    public void isValueType_should_be_false() {
        JSONArrayWrapper wrapper = new JSONArrayWrapper(null);
        assertFalse(wrapper.isValueType());
    }

    @Test
    public void isArray_should_be_true() {
        JSONArrayWrapper wrapper = new JSONArrayWrapper(null);
        assertTrue(wrapper.isArray());
    }

    @Test
    public void toString_should_return_null_when_array_is_null() {
        JSONArrayWrapper wrapper = new JSONArrayWrapper(null);
        String result = wrapper.toString();
        assertNull(result);
    }

    @Test
    public void toString_should_return_array_toString() {
        JSONArray arr = new JSONArray();
        arr.put("24");
        JSONArrayWrapper wrapper = new JSONArrayWrapper(arr);
        String result = wrapper.toString();
        assertEquals(arr.toString(), result);
    }
}
