package tests.prettyprinters;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.prettyprinters.BasePrettyPrinter;
import com.colonolnutty.module.shareddata.prettyprinters.JsonNodePrettyPrinter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * User: Jack's Computer
 * Date: 12/31/2017
 * Time: 12:46 PM
 */
public class JsonNodePrettyPrinterTests {

    private JsonNodePrettyPrinter _printer;
    private NodeProvider _nodeProvider;

    public JsonNodePrettyPrinterTests() {
        _printer = new JsonNodePrettyPrinter();
        _nodeProvider = new NodeProvider();
    }

    //formatArray

    @Test
    public void formatArray_should_return_empty_array_no_indent() {
        String expectedResult = "[ ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        String result = _printer.formatArray(arr, 3, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_empty_array_specifying_indent_with_no_indent() {
        String expectedResult = "[ ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        String result = _printer.formatArray(arr, 3, true);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_empty_double_array() {
        String expectedResult = "[[ ]]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        arr.add(subArr);
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_formatted_array() {
        String expectedResult = "[ 24.0, 25.0, 26.0 ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(24.0);
        arr.add(25.0);
        arr.add(26.0);
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_single_object_array() {
        String expectedResult = "[{ \"one\" : 24.0 }]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        arr.add(obj);
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_single_object_multiple_properties_array() {
        String expectedResult = "[{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        arr.add(obj);
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_formatted_sub_array_of_values() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  [ 24.0 ],"
                + BasePrettyPrinter.NEW_LINE + "  [ 25.0 ],"
                + BasePrettyPrinter.NEW_LINE + "  [ 26.0 ]"
                + BasePrettyPrinter.NEW_LINE + "]";
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
    public void formatArray_should_return_formatted_sub_array_of_objects() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  [{ \"one\" : 24.0 }],"
                + BasePrettyPrinter.NEW_LINE + "  [{ \"two\" : 25.0 }],"
                + BasePrettyPrinter.NEW_LINE + "  [{ \"three\" : 26.0 }]"
                + BasePrettyPrinter.NEW_LINE + "]";
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
    public void formatArray_should_return_formatted_sub_object() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  { \"one\" : 24.0 },"
                + BasePrettyPrinter.NEW_LINE + "  { \"two\" : 25.0 },"
                + BasePrettyPrinter.NEW_LINE + "  { \"three\" : 26.0 }"
                + BasePrettyPrinter.NEW_LINE + "]";
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
        String result = _printer.formatArray(arr, 0, true);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_combination_single_property_object_array() {
        String expectedResult = "[ \"one\", 2, { \"three\" : 24 } ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add("one");
        arr.add(2);
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("three", 24);
        arr.add(obj);
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    //formatObject

    @Test
    public void formatObject_should_return_empty_object() {
        String expectedResult = "{ }";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        String result = _printer.formatObject(objNode, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_null_property() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"three\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        objNode.put("one", 24.0);
        objNode.put("two", "null");
        objNode.put("three", 25.0);
        String result = _printer.formatObject(objNode, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_single_property_object_with_null_property() {
        String expectedResult = "{ }";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        objNode.put("one", "null");
        String result = _printer.formatObject(objNode, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        objNode.put("one", 24.0);
        objNode.put("two", 25.0);
        String result = _printer.formatObject(objNode, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_single_property_object_with_object_property() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : {"
                + BasePrettyPrinter.NEW_LINE + "    \"two\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "    \"three\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "  }"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode obj = _nodeProvider.createObjectNode();
        ObjectNode subObj = _nodeProvider.createObjectNode();
        subObj.put("two", 24.0);
        subObj.put("three", 25.0);
        obj.put("one", subObj);
        String result = _printer.formatObject(obj, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_array() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : [ 24.0 ],"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        ArrayNode arrNodeOne = _nodeProvider.createArrayNode();
        arrNodeOne.add(24.0);
        objNode.put("one",  arrNodeOne);
        objNode.put("two", 25.0);
        String result = _printer.formatObject(objNode, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_double_array() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : ["
                + BasePrettyPrinter.NEW_LINE + "    [ 24.0 ]"
                + BasePrettyPrinter.NEW_LINE + "  ],"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        ArrayNode subNodeArr = _nodeProvider.createArrayNode();
        ArrayNode arrNodeOne = _nodeProvider.createArrayNode();
        arrNodeOne.add(24.0);
        subNodeArr.add(arrNodeOne);

        objNode.put("one",  subNodeArr);
        objNode.put("two", 25.0);
        String result = _printer.formatObject(objNode, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_double_array_of_objects() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : ["
                + BasePrettyPrinter.NEW_LINE + "    ["
                + BasePrettyPrinter.NEW_LINE + "      {"
                + BasePrettyPrinter.NEW_LINE + "        \"three\" : 26.0,"
                + BasePrettyPrinter.NEW_LINE + "        \"four\" : 27.0"
                + BasePrettyPrinter.NEW_LINE + "      },"
                + BasePrettyPrinter.NEW_LINE + "      {"
                + BasePrettyPrinter.NEW_LINE + "        \"five\" : 28.0,"
                + BasePrettyPrinter.NEW_LINE + "        \"six\" : 29.0"
                + BasePrettyPrinter.NEW_LINE + "      }"
                + BasePrettyPrinter.NEW_LINE + "    ]"
                + BasePrettyPrinter.NEW_LINE + "  ],"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
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
        String result = _printer.formatObject(objNode, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_empty_double_array() {
        String expectedResult = "{ \"one\" : [[ ]] }";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        ArrayNode arrNodeOne = _nodeProvider.createArrayNode();
        ArrayNode subNodeArr = _nodeProvider.createArrayNode();
        arrNodeOne.add(subNodeArr);
        objNode.put("one",  arrNodeOne);
        String result = _printer.formatObject(objNode, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_combination_single_property_object_array_sub_object() {
        String expectedResult = "{ \"zero\" : [ \"one\", 2, { \"three\" : 24 } ] }";
        ObjectNode objOne = _nodeProvider.createObjectNode();
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add("one");
        arr.add(2);
        ObjectNode subObj = _nodeProvider.createObjectNode();
        subObj.put("three", 24);
        arr.add(subObj);
        objOne.put("zero", arr);
        String result = _printer.formatObject(objOne, 0, false);
        assertEquals(expectedResult, result);
    }

    //formatAsIntended

    @Test
    public void formatAsIntended_should_return_null() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("blah", (String)null);
        String result = _printer.formatAsIntended(node.get("blah"), 0, false);
        assertNull(result);
    }

    @Test
    public void formatAsIntended_should_return_double() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("blah", 24.0);
        String result = _printer.formatAsIntended(node.get("blah"), 0, false);
        assertEquals("24.0", result);
    }

    @Test
    public void formatAsIntended_should_return_integer() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("blah", 24);
        String result = _printer.formatAsIntended(node.get("blah"), 0, false);
        assertEquals("24", result);
    }

    @Test
    public void formatAsIntended_should_return_boolean() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("blah", true);
        String result = _printer.formatAsIntended(node.get("blah"), 0, false);
        assertEquals("true", result);
    }

    @Test
    public void formatAsIntended_should_return_string() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("blah", "yay");
        String result = _printer.formatAsIntended(node.get("blah"), 0, false);
        assertEquals("\"yay\"", result);
    }

    @Test
    public void formatAsIntended_should_return_array() {
        String expectedResult = "[ \"yay\" ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add("yay");
        String result = _printer.formatAsIntended(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatAsIntended_should_return_single_property_object() {
        String expectedResult = "{ \"one\" : 24.0 }";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        objNode.put("one", 24.0);
        String result = _printer.formatAsIntended(objNode, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatAsIntended_should_return_object() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        objNode.put("one", 24.0);
        objNode.put("two", 25.0);
        String result = _printer.formatAsIntended(objNode, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatAsIntended_should_return_formatted_object() {
        String expectedResult = "{ \"one\" : [[ ]] }";
        ObjectNode objNode = _nodeProvider.createObjectNode();
        ArrayNode arrNodeOne = _nodeProvider.createArrayNode();
        ArrayNode subNodeArr = _nodeProvider.createArrayNode();
        arrNodeOne.add(subNodeArr);
        objNode.put("one",  arrNodeOne);
        String asIntendedResult = _printer.formatAsIntended(objNode, 0, false);
        assertEquals(expectedResult, asIntendedResult);
        String result = _printer.formatObject(objNode, 0, false);
        assertEquals(expectedResult, result);
        assertEquals(result, asIntendedResult);
    }
}
