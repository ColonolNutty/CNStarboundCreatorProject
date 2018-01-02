package tests.models.json;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.models.json.IJsonWrapper;
import com.colonolnutty.module.shareddata.models.json.ArrayNodeWrapper;
import com.colonolnutty.module.shareddata.models.json.JsonNodeWrapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static junit.framework.TestCase.*;

/**
 * User: Jack's Computer
 * Date: 01/02/2018
 * Time: 1:35 PM
 */
public class ArrayNodeWrapperTests {
    private NodeProvider _nodeProvider;

    public ArrayNodeWrapperTests() {
        _nodeProvider = new NodeProvider();
    }

    @Test
    public void firstItemIsValueType_should_be_false_when_first_item_is_null() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add((String)null);
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        assertFalse(wrapper.firstItemIsValueType());
    }

    @Test
    public void firstItemIsValueType_should_be_false_when_first_item_is_array() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        arr.add(subArr);
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        assertFalse(wrapper.firstItemIsValueType());
    }

    @Test
    public void firstItemIsValueType_should_be_false_when_first_item_is_object() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ObjectNode obj = _nodeProvider.createObjectNode();
        arr.add(obj);
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        assertFalse(wrapper.firstItemIsValueType());
    }

    @Test
    public void firstItemIsValueType_should_be_true_when_first_item_is_value_type() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(24);
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        assertTrue(wrapper.firstItemIsValueType());
    }

    @Test
    public void get_should_give_null_when_item_is_null() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add((String)null);
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        IJsonWrapper result = wrapper.get(0);
        assertNull(result);
    }

    @Test
    public void get_should_give_null_when_index_below_zero() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(1);
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        IJsonWrapper result = wrapper.get(-1);
        assertNull(result);
    }

    @Test
    public void get_should_give_null_when_index_above_size() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(1);
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        IJsonWrapper result = wrapper.get(1);
        assertNull(result);
    }

    @Test
    public void get_should_give_null_when_array_is_null() {
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(null);
        IJsonWrapper result = wrapper.get(0);
        assertNull(result);
    }

    @Test
    public void get_should_give_json_node_wrapper_for_value_types() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(24.0);
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        IJsonWrapper result = wrapper.get(0);
        assertNotNull(result);
        assertTrue(result instanceof JsonNodeWrapper);
    }

    @Test
    public void get_should_give_json_node_wrapper_for_objects() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ObjectNode subObj = _nodeProvider.createObjectNode();
        arr.add(subObj);
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        IJsonWrapper result = wrapper.get(0);
        assertNotNull(result);
        assertTrue(result instanceof JsonNodeWrapper);
    }

    @Test
    public void get_should_give_array_node_wrapper_for_arrays() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        arr.add(subArr);
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        IJsonWrapper result = wrapper.get(0);
        assertNotNull(result);
        assertTrue(result instanceof ArrayNodeWrapper);
    }

    @Test
    public void size_should_give_zero_with_null_array() {
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(null);
        assertEquals(0, wrapper.size());
    }

    @Test
    public void size_should_give_size() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add("blah");
        arr.add("blah2");
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        assertEquals(2, wrapper.size());
    }

    @Test
    public void isValueType_should_be_false() {
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(null);
        assertFalse(wrapper.isValueType());
    }

    @Test
    public void isArray_should_be_true() {
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(null);
        assertTrue(wrapper.isArray());
    }

    @Test
    public void toString_should_return_null_when_array_is_null() {
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(null);
        String result = wrapper.toString();
        assertNull(result);
    }

    @Test
    public void toString_should_return_array_toString() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add("24");
        ArrayNodeWrapper wrapper = new ArrayNodeWrapper(arr);
        String result = wrapper.toString();
        assertEquals(arr.toString(), result);
    }
}
