package tests;

import com.colonolnutty.module.shareddata.JsonPrettyPrinter;
import com.colonolnutty.module.shareddata.NodeProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.TestCase.*;

/**
 * User: Jack's Computer
 * Date: 12/23/2017
 * Time: 12:19 PM
 */
public class JsonPrettyPrinterTests {

    private JsonPrettyPrinter _printer;
    private NodeProvider _nodeProvider;

    public JsonPrettyPrinterTests() {
        _printer = new JsonPrettyPrinter(new String[0]);
        _nodeProvider = new NodeProvider();
    }

    //formatAsIntended

    @Test
    public void formatAsIntendedJsonNode_should_return_null_for_null_value_type() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("blah", (String)null);
        String result = _printer.formatAsIntended(node.get("blah"), 0);
        assertNull(result);
    }

    @Test
    public void formatAsIntendedJsonNode_should_return_double() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("blah", 24.0);
        String result = _printer.formatAsIntended(node.get("blah"), 0);
        assertEquals("24.0", result);
    }

    @Test
    public void formatAsIntendedJsonNode_should_return_integer() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("blah", 24);
        String result = _printer.formatAsIntended(node.get("blah"), 0);
        assertEquals("24", result);
    }

    @Test
    public void formatAsIntendedJsonNode_should_return_boolean() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("blah", true);
        String result = _printer.formatAsIntended(node.get("blah"), 0);
        assertEquals("true", result);
    }

    @Test
    public void formatAsIntendedJsonNode_should_return_string() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("blah", "yay");
        String result = _printer.formatAsIntended(node.get("blah"), 0);
        assertEquals("\"yay\"", result);
    }

    @Test
    public void formatAsIntendedJsonNode_should_return_with_indent() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("blah", "yay");
        String result = _printer.formatAsIntended(node.get("blah"), 2);
        assertEquals("  \"yay\"", result);
    }

    //formatAsIntendedJsonNode_should_return_array
    //formatAsIntendedJsonNode_should_return_object

    @Test
    public void formatAsIntendedJsonArray_should_return_double() {
        String result = _printer.formatAsIntended(24.0, 0);
        assertEquals("24.0", result);
    }

    @Test
    public void formatAsIntendedJsonArray_should_return_integer() {
        String result = _printer.formatAsIntended(24, 0);
        assertEquals("24", result);
    }

    @Test
    public void formatAsIntendedJsonArray_should_return_boolean() {
        String result = _printer.formatAsIntended(true, 0);
        assertEquals("true", result);
    }

    @Test
    public void formatAsIntendedJsonArray_should_return_string() {
        String result = _printer.formatAsIntended("yay", 0);
        assertEquals("\"yay\"", result);
    }

    //formatAsIntendedJsonArray_should_return_array
    //formatAsIntendedJsonArray_should_return_object

    //formatAsIntended

    @Test
    public void formatArray_should_return_empty_array() {
        String expectedResult = "[ ]";
        JSONArray arr = new JSONArray();
        String result = _printer.formatArray(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_double_array() {
        String expectedResult = "[ 24.0, 25.0, 26.0 ]";
        JSONArray arr = new JSONArray();
        arr.put(0, 24.0);
        arr.put(1, 25.0);
        arr.put(2, 26.0);
        String result = _printer.formatArray(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_string_array() {
        String expectedResult = "[ \"one\", \"two\", \"three\" ]";
        JSONArray arr = new JSONArray();
        arr.put(0, "one");
        arr.put(1, "two");
        arr.put(2, "three");
        String result = _printer.formatArray(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_exclude_null_entries() {
        String expectedResult = "[ \"one\", \"two\", \"three\" ]";
        JSONArray arr = new JSONArray();
        arr.put(0, "one");
        arr.put(1, "two");
        arr.put(2, (String)null);
        arr.put(3, "three");
        String result = _printer.formatArray(arr, 0);
        assertEquals(expectedResult, result);
    }
}
