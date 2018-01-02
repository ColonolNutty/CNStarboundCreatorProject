package tests.models.json;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.models.json.ArrayNodeWrapper;
import com.colonolnutty.module.shareddata.models.json.IJsonWrapper;
import com.colonolnutty.module.shareddata.models.json.JsonNodeWrapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.*;

/**
 * User: Jack's Computer
 * Date: 01/02/2018
 * Time: 1:35 PM
 */
public class JsonNodeWrapperTests {
    private NodeProvider _nodeProvider;

    public JsonNodeWrapperTests() {
        _nodeProvider = new NodeProvider();
    }

    @Test
    public void get_should_give_null_when_node_does_not_contain_property() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", 1);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj);
        IJsonWrapper result = wrapper.get("blah2");
        assertNull(result);
    }

    @Test
    public void get_should_give_null_when_node_is_null() {
        JsonNodeWrapper wrapper = new JsonNodeWrapper(null);
        IJsonWrapper result = wrapper.get("blah");
        assertNull(result);
    }

    @Test
    public void get_should_give_null_when_node_is_array() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        JsonNodeWrapper wrapper = new JsonNodeWrapper(arr);
        IJsonWrapper result = wrapper.get("blah");
        assertNull(result);
    }

    @Test
    public void get_should_give_json_node_wrapper_for_value_types() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", 24.0);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj);
        IJsonWrapper result = wrapper.get("blah");
        assertNotNull(result);
        assertTrue(result instanceof JsonNodeWrapper);
    }

    @Test
    public void get_should_give_json_node_wrapper_for_objects() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        ObjectNode subObj = _nodeProvider.createObjectNode();
        obj.put("blah", subObj);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj);
        IJsonWrapper result = wrapper.get("blah");
        assertNotNull(result);
        assertTrue(result instanceof JsonNodeWrapper);
    }

    @Test
    public void get_should_give_array_node_wrapper_for_arrays() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        obj.put("blah", subArr);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj);
        IJsonWrapper result = wrapper.get("blah");
        assertNotNull(result);
        assertTrue(result instanceof ArrayNodeWrapper);
    }


    @Test
    public void fieldNames_should_give_empty_list_when_node_is_a_null_node() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", (String)null);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj.get("blah"));
        ArrayList<String> fieldNames = wrapper.fieldNames();
        assertNotNull(fieldNames);
        assertEquals(0, fieldNames.size());
    }

    @Test
    public void fieldNames_should_give_empty_list_with_null_node() {
        JsonNodeWrapper wrapper = new JsonNodeWrapper(null);
        ArrayList<String> fieldNames = wrapper.fieldNames();
        assertNotNull(fieldNames);
        assertEquals(0, fieldNames.size());
    }

    @Test
    public void fieldNames_retrieves_field_names() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", true);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj);
        ArrayList<String> fieldNames = wrapper.fieldNames();
        assertTrue(fieldNames.contains("blah"));
    }

    @Test
    public void fieldNames_should_be_created_only_once() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", true);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj);
        ArrayList<String> fieldNames = wrapper.fieldNames();
        assertTrue(fieldNames.contains("blah"));
        ArrayList<String> fieldNamesTwo = wrapper.fieldNames();
        assertTrue(fieldNames == fieldNamesTwo);
    }

    @Test
    public void isValueType_true() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", true);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj.get("blah"));
        assertTrue(wrapper.isValueType());
        assertFalse(wrapper.isObject());
        assertFalse(wrapper.isArray());
    }

    @Test
    public void isObject_true() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", "24");
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj);
        assertTrue(wrapper.isObject());
        assertFalse(wrapper.isArray());
        assertFalse(wrapper.isValueType());
    }

    @Test
    public void isArray_true() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(24);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(arr);
        assertTrue(wrapper.isArray());
        assertFalse(wrapper.isObject());
        assertFalse(wrapper.isValueType());
    }


    @Test
    public void toString_should_return_null_when_node_is_null() {
        JsonNodeWrapper wrapper = new JsonNodeWrapper(null);
        String result = wrapper.toString();
        assertNull(result);
    }

    @Test
    public void toString_should_return_null_when_node_is_a_null_node() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", (String)null);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj.get("blah"));
        String result = wrapper.toString();
        assertNull(result);
    }

    @Test
    public void toString_should_return_null_when_node_is_a_null_string() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", "null");
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj.get("blah"));
        String result = wrapper.toString();
        assertNull(result);
    }

    @Test
    public void toString_should_return_object_node_toString() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", "24");
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj);
        String result = wrapper.toString();
        assertEquals(obj.toString(), result);
    }

    @Test
    public void toString_should_return_array_node_toString() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(24);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(arr);
        String result = wrapper.toString();
        assertEquals(arr.toString(), result);
    }

    @Test
    public void toString_should_handle_booleans() {
        String expectedResult = "true";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", true);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj.get("blah"));
        String result = wrapper.toString();
        assertEquals(expectedResult, result);
    }

    @Test
    public void toString_should_handle_integers() {
        String expectedResult = "24";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", 24);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj.get("blah"));
        String result = wrapper.toString();
        assertEquals(expectedResult, result);
    }

    @Test
    public void toString_should_handle_double() {
        String expectedResult = "24.0";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", 24.0);
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj.get("blah"));
        String result = wrapper.toString();
        assertEquals(expectedResult, result);
    }

    @Test
    public void toString_should_handle_strings() {
        String expectedResult = "\"yay\"";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", "yay");
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj.get("blah"));
        String result = wrapper.toString();
        assertEquals(expectedResult, result);
    }

    @Test
    public void toString_should_escape_strings() {
        String expectedResult = "\"\\\"yay\\\"\"";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", "\"yay\"");
        JsonNodeWrapper wrapper = new JsonNodeWrapper(obj.get("blah"));
        String result = wrapper.toString();
        assertEquals(expectedResult, result);
    }
}
