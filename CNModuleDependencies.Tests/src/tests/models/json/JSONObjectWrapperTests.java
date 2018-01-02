package tests.models.json;

import com.colonolnutty.module.shareddata.models.json.JSONArrayWrapper;
import com.colonolnutty.module.shareddata.models.json.IJsonWrapper;
import com.colonolnutty.module.shareddata.models.json.JSONObjectWrapper;
import com.colonolnutty.module.shareddata.models.json.ValueTypeWrapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * User: Jack's Computer
 * Date: 01/02/2018
 * Time: 1:35 PM
 */
public class JSONObjectWrapperTests {

    @Test
    public void get_should_give_null_when_property_is_null() {
        JSONObject obj = new JSONObject();
        obj.put("blah", (String)null);
        JSONObjectWrapper wrapper = new JSONObjectWrapper(obj);
        IJsonWrapper result = wrapper.get("blah");
        assertNull(result);
    }

    @Test
    public void get_should_give_null_when_object_does_not_contain_property() {
        JSONObject obj = new JSONObject();
        obj.put("blah", 1);
        JSONObjectWrapper wrapper = new JSONObjectWrapper(obj);
        IJsonWrapper result = wrapper.get("blah2");
        assertNull(result);
    }

    @Test
    public void get_should_give_null_when_object_is_null() {
        JSONObjectWrapper wrapper = new JSONObjectWrapper(null);
        IJsonWrapper result = wrapper.get("blah");
        assertNull(result);
    }

    @Test
    public void get_should_give_value_type_wrapper_for_value_types() {
        JSONObject obj = new JSONObject();
        obj.put("blah", 24.0);
        JSONObjectWrapper wrapper = new JSONObjectWrapper(obj);
        IJsonWrapper result = wrapper.get("blah");
        assertNotNull(result);
        assertTrue(result instanceof ValueTypeWrapper);
    }

    @Test
    public void get_should_give_json_object_wrapper_for_objects() {
        JSONObject obj = new JSONObject();
        JSONObject subObj = new JSONObject();
        obj.put("blah", subObj);
        JSONObjectWrapper wrapper = new JSONObjectWrapper(obj);
        IJsonWrapper result = wrapper.get("blah");
        assertNotNull(result);
        assertTrue(result instanceof JSONObjectWrapper);
    }

    @Test
    public void get_should_give_json_array_wrapper_for_arrays() {
        JSONObject obj = new JSONObject();
        JSONArray subArr = new JSONArray();
        obj.put("blah", subArr);
        JSONObjectWrapper wrapper = new JSONObjectWrapper(obj);
        IJsonWrapper result = wrapper.get("blah");
        assertNotNull(result);
        assertTrue(result instanceof JSONArrayWrapper);
    }

    @Test
    public void fieldNames_should_give_empty_list_with_null_object() {
        JSONObjectWrapper wrapper = new JSONObjectWrapper(null);
        ArrayList<String> fieldNames = wrapper.fieldNames();
        assertNotNull(fieldNames);
        assertEquals(0, fieldNames.size());
    }

    @Test
    public void fieldNames_retrieves_field_names() {
        JSONObject obj = new JSONObject();
        obj.put("blah", true);
        JSONObjectWrapper wrapper = new JSONObjectWrapper(obj);
        ArrayList<String> fieldNames = wrapper.fieldNames();
        assertTrue(fieldNames.contains("blah"));
    }

    @Test
    public void fieldNames_should_be_created_only_once() {
        JSONObject obj = new JSONObject();
        obj.put("blah", true);
        JSONObjectWrapper wrapper = new JSONObjectWrapper(obj);
        ArrayList<String> fieldNames = wrapper.fieldNames();
        assertTrue(fieldNames.contains("blah"));
        ArrayList<String> fieldNamesTwo = wrapper.fieldNames();
        assertTrue(fieldNames == fieldNamesTwo);
    }

    @Test
    public void isValueType_should_be_false() {
        JSONObjectWrapper wrapper = new JSONObjectWrapper(null);
        assertFalse(wrapper.isValueType());
    }

    @Test
    public void isObject_true() {
        JSONObject obj = new JSONObject();
        obj.put("blah", "24");
        JSONObjectWrapper wrapper = new JSONObjectWrapper(obj);
        assertTrue(wrapper.isObject());
        assertFalse(wrapper.isArray());
        assertFalse(wrapper.isValueType());
    }

    @Test
    public void isArray_should_be_false() {
        JSONObjectWrapper wrapper = new JSONObjectWrapper(null);
        assertFalse(wrapper.isArray());
    }

    @Test
    public void toString_should_return_null_when_object_is_null() {
        JSONObjectWrapper wrapper = new JSONObjectWrapper(null);
        String result = wrapper.toString();
        assertNull(result);
    }

    @Test
    public void toString_should_return_object_toString() {
        JSONObject obj = new JSONObject();
        obj.put("blah", "24");
        JSONObjectWrapper wrapper = new JSONObjectWrapper(obj);
        String result = wrapper.toString();
        assertEquals(obj.toString(), result);
    }
}
