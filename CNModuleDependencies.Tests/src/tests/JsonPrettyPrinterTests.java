package tests;

import com.colonolnutty.module.shareddata.JsonPrettyPrinter;
import com.colonolnutty.module.shareddata.NodeProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

    //nonpatch
    //formatAsIntended

    @Test
    public void formatAsIntended_should_return_double() {
        String result = _printer.formatAsIntended(24.0, 0);
        assertEquals("24.0", result);
    }

    @Test
    public void formatAsIntended_should_return_integer() {
        String result = _printer.formatAsIntended(24, 0);
        assertEquals("24", result);
    }

    @Test
    public void formatAsIntended_should_return_boolean() {
        String result = _printer.formatAsIntended(true, 0);
        assertEquals("true", result);
    }

    @Test
    public void formatAsIntended_should_return_string() {
        String result = _printer.formatAsIntended("yay", 0);
        assertEquals("\"yay\"", result);
    }

    //formatAsIntended_should_return_array
    //formatAsIntended_should_return_object

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

    //nonpatch

    //patch

    @Test
    public void formatAsIntendedJsonNode_should_return_null() {
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

    //formatAsIntendedJsonNode_should_return_array
    //formatAsIntendedJsonNode_should_return_object

    @Test
    public void makePrettyJsonNode_should_return_empty_array() {
        String expectedResult = "[ ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePrettyJsonNode_should_return_empty_sub_array() {
        String expectedResult = "[[ ]]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        arr.add(subArr);
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePrettyJsonNode_should_return_formatted_array() {
        String expectedResult = "[ 24.0, 25.0, 26.0 ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(24.0);
        arr.add(25.0);
        arr.add(26.0);
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePrettyJsonNode_should_return_formatted_sub_array_of_values() {
        String expectedResult = "["
                     + "\r\n" + "  [ 24.0 ],"
                     + "\r\n" + "  [ 25.0 ],"
                     + "\r\n" + "  [ 26.0 ]"
                     + "\r\n" + "]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArrayOne = _nodeProvider.createArrayNode();
        subArrayOne.add(24.0);
        ArrayNode subArrayTwo = _nodeProvider.createArrayNode();
        subArrayTwo.add(25.0);
        ArrayNode subArrayThree = _nodeProvider.createArrayNode();
        subArrayThree.add(26.0);
        arr.add(subArrayOne);
        arr.add(subArrayTwo);
        arr.add(subArrayThree);
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePrettyJsonNode_should_return_formatted_sub_array_of_objects() {
        String expectedResult = "["
                     + "\r\n" + "  ["
                     + "\r\n" + "    {"
                     + "\r\n" + "      \"one\" : 24.0"
                     + "\r\n" + "    }"
                     + "\r\n" + "  ],"
                     + "\r\n" + "  ["
                     + "\r\n" + "    {"
                     + "\r\n" + "      \"two\" : 25.0"
                     + "\r\n" + "    }"
                     + "\r\n" + "  ],"
                     + "\r\n" + "  ["
                     + "\r\n" + "    {"
                     + "\r\n" + "      \"three\" : 26.0"
                     + "\r\n" + "    }"
                     + "\r\n" + "  ]"
                     + "\r\n" + "]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArrayOne = _nodeProvider.createArrayNode();
        ObjectNode objOne = _nodeProvider.createObjectNode();
        objOne.put("one", 24.0);
        subArrayOne.add(objOne);
        ArrayNode subArrayTwo = _nodeProvider.createArrayNode();
        ObjectNode objTwo = _nodeProvider.createObjectNode();
        objTwo.put("two", 25.0);
        subArrayTwo.add(objTwo);
        ArrayNode subArrayThree = _nodeProvider.createArrayNode();
        ObjectNode objThree = _nodeProvider.createObjectNode();
        objThree.put("three", 26.0);
        subArrayThree.add(objThree);
        arr.add(subArrayOne);
        arr.add(subArrayTwo);
        arr.add(subArrayThree);
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePrettyJsonNode_should_return_formatted_sub_object() {
        String expectedResult = "["
                     + "\r\n" + "  {"
                     + "\r\n" + "    \"one\" : 24.0"
                     + "\r\n" + "  },"
                     + "\r\n" + "  {"
                     + "\r\n" + "    \"two\" : 25.0"
                     + "\r\n" + "  },"
                     + "\r\n" + "  {"
                     + "\r\n" + "    \"three\" : 26.0"
                     + "\r\n" + "  }"
                     + "\r\n" + "]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ObjectNode objOne = _nodeProvider.createObjectNode();
        objOne.put("one", 24.0);
        ObjectNode objTwo = _nodeProvider.createObjectNode();
        objTwo.put("two", 25.0);
        ObjectNode objThree = _nodeProvider.createObjectNode();
        objThree.put("three", 26.0);
        arr.add(objOne);
        arr.add(objTwo);
        arr.add(objThree);
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_empty_object() {
        String expectedResult = "{ }";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        String result = _printer.formatObject(objNode, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object() {
        String expectedResult = "{"
                + "\r\n" + "  \"one\" : 24.0,"
                + "\r\n" + "  \"two\" : 25.0"
                + "\r\n" + "}";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        objNode.put("one", 24.0);
        objNode.put("two", 25.0);
        String result = _printer.formatObject(objNode, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_array() {
        String expectedResult = "{"
                + "\r\n" + "  \"one\" : [ 24.0 ],"
                + "\r\n" + "  \"two\" : 25.0"
                + "\r\n" + "}";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        ArrayNode arrNodeOne = _nodeProvider.createArrayNode();
        arrNodeOne.add(24.0);
        objNode.put("one",  arrNodeOne);
        objNode.put("two", 25.0);
        String result = _printer.formatObject(objNode, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_double_array() {
        String expectedResult = "{"
                + "\r\n" + "  \"one\" : ["
                + "\r\n" + "    [ 24.0 ]"
                + "\r\n" + "  ],"
                + "\r\n" + "  \"two\" : 25.0"
                + "\r\n" + "}";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        ArrayNode subNodeArr = _nodeProvider.createArrayNode();
        ArrayNode arrNodeOne = _nodeProvider.createArrayNode();
        arrNodeOne.add(24.0);
        subNodeArr.add(arrNodeOne);

        objNode.put("one",  subNodeArr);
        objNode.put("two", 25.0);
        String result = _printer.formatObject(objNode, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_double_array_of_objects() {
        String expectedResult = "{"
                     + "\r\n" + "  \"one\" : ["
                     + "\r\n" + "    ["
                     + "\r\n" + "      {"
                     + "\r\n" + "        \"three\" : 26.0,"
                     + "\r\n" + "        \"four\" : 27.0"
                     + "\r\n" + "      },"
                     + "\r\n" + "      {"
                     + "\r\n" + "        \"five\" : 28.0,"
                     + "\r\n" + "        \"six\" : 29.0"
                     + "\r\n" + "      }"
                     + "\r\n" + "    ]"
                     + "\r\n" + "  ],"
                     + "\r\n" + "  \"two\" : 25.0"
                     + "\r\n" + "}";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        ArrayNode subNodeArr = _nodeProvider.createArrayNode();
        ArrayNode arrNodeOne = _nodeProvider.createArrayNode();
        ObjectNode subObjNode = _nodeProvider.createObjectNode();
        subObjNode.put("three", 26.0);
        subObjNode.put("four", 27.0);
        ObjectNode subObjNodeTwo = _nodeProvider.createObjectNode();
        subObjNodeTwo.put("five", 28.0);
        subObjNodeTwo.put("six", 29.0);
        arrNodeOne.add(subObjNode);
        arrNodeOne.add(subObjNodeTwo);
        subNodeArr.add(arrNodeOne);

        objNode.put("one",  subNodeArr);
        objNode.put("two", 25.0);
        String result = _printer.formatObject(objNode, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_empty_double_array() {
        String expectedResult = "{"
                + "\r\n" + "  \"one\" : [[ ]]"
                + "\r\n" + "}";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        ArrayNode arrNodeOne = _nodeProvider.createArrayNode();
        ArrayNode subNodeArr = _nodeProvider.createArrayNode();
        arrNodeOne.add(subNodeArr);
        objNode.put("one",  arrNodeOne);
        String result = _printer.formatObject(objNode, 0);
        assertEquals(expectedResult, result);
    }


    @Test
    public void formatAsIntended_should_return_formatted_object() {
        String expectedResult = "{"
                + "\r\n" + "  \"one\" : [[ ]]"
                + "\r\n" + "}";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        ArrayNode arrNodeOne = _nodeProvider.createArrayNode();
        ArrayNode subNodeArr = _nodeProvider.createArrayNode();
        arrNodeOne.add(subNodeArr);
        objNode.put("one",  arrNodeOne);
        String asIntendedResult = _printer.formatAsIntended(objNode, 0);
        assertEquals(expectedResult, asIntendedResult);
        String result = _printer.formatObject(objNode, 0);
        assertEquals(expectedResult, result);
        assertEquals(result, asIntendedResult);
    }

    //patch
}
