package tests.prettyprinters;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.models.json.*;
import com.colonolnutty.module.shareddata.prettyprinters.BasePrettyPrinter;
import com.colonolnutty.module.shareddata.prettyprinters.JsonWrapperPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Hashtable;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

/**
 * User: Jack's Computer
 * Date: 01/01/2018
 * Time: 3:29 PM
 */
public class JsonWrapperPrettyPrinterTests {
    private JsonWrapperPrettyPrinter _printer;
    private NodeProvider _nodeProvider;

    public JsonWrapperPrettyPrinterTests() {
        _printer = new JsonWrapperPrettyPrinter();
        _nodeProvider = new NodeProvider();
    }

    @Test
    public void makePretty_formats_null_as_null() {
        String result = _printer.makePretty(null, 0);
        assertNull(result);
    }

    @Test
    public void makePretty_formats_unknown_as_null() {
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        String result = _printer.makePretty(obj, 0);
        assertNull(result);
    }

    @Test
    public void makePretty_formats_JSONObject() {
        String expectedResult = "{ }";
        JSONObject obj = new JSONObject();
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePretty_formats_JSONArray() {
        String expectedResult = "[ ]";
        JSONArray arr = new JSONArray();
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePretty_formats_ObjectNode() {
        String expectedResult = "{ }";
        ObjectNode obj = _nodeProvider.createObjectNode();
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePretty_formats_ArrayNode() {
        String expectedResult = "[ ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        String result = _printer.makePretty(arr, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePretty_formats_JsonNode_value_type() {
        String expectedResult = "24.0";
        String pathName = "blah";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put(pathName, 24.0);
        JsonNode toSend = obj.get(pathName);
        String result = _printer.makePretty(toSend, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePretty_formats_JsonNode_string() {
        String expectedResult = "\"I am string\"";
        String pathName = "blah";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put(pathName, "I am string");
        JsonNode toSend = obj.get(pathName);
        String result = _printer.makePretty(toSend, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePretty_formats_JsonNode_null_string_as_null() {
        String pathName = "blah";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put(pathName, (String)null);
        JsonNode toSend = obj.get(pathName);
        String result = _printer.makePretty(toSend, 0);
        assertNull(result);
    }

    @Test
    public void makePretty_formats_JsonNode_string_with_escaped_characters() {
        String expectedResult = "\"\\\"I am string\\\"\"";
        String pathName = "blah";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put(pathName, "\"I am string\"");
        JsonNode toSend = obj.get(pathName);
        String result = _printer.makePretty(toSend, 0);
        assertEquals(expectedResult, result);
    }

    //formatObject

    @Test
    public void formatObject_should_return_formatted_object() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        assertFormatObject(obj, expectedResult);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_null_property() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"three\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        obj.put("two", "null");
        obj.put("three", 25.0);
        assertFormatObject(obj, expectedResult);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_sub_double_array() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"three\" : ["
                + BasePrettyPrinter.NEW_LINE + "    [ 26.0 ],"
                + BasePrettyPrinter.NEW_LINE + "    [ 27.0 ]"
                + BasePrettyPrinter.NEW_LINE + "  ]"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArrOne = _nodeProvider.createArrayNode();
        subArrOne.add(26.0);
        ArrayNode subArrTwo = _nodeProvider.createArrayNode();
        subArrTwo.add(27.0);
        arr.add(subArrOne);
        arr.add(subArrTwo);
        obj.put("three", arr);
        assertFormatObject(obj, expectedResult);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_sub_objects() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"three\" : {"
                + BasePrettyPrinter.NEW_LINE + "    \"four\" : 26.0,"
                + BasePrettyPrinter.NEW_LINE + "    \"five\" : 27.0"
                + BasePrettyPrinter.NEW_LINE + "  }"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        ObjectNode subObj = _nodeProvider.createObjectNode();
        subObj.put("four", 26.0);
        subObj.put("five", 27.0);
        obj.put("three", subObj);
        assertFormatObject(obj, expectedResult);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_sub_array_of_objects() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"three\" : [["
                + BasePrettyPrinter.NEW_LINE + "    {"
                + BasePrettyPrinter.NEW_LINE + "      \"four\" : 26.0,"
                + BasePrettyPrinter.NEW_LINE + "      \"five\" : 27.0"
                + BasePrettyPrinter.NEW_LINE + "    },"
                + BasePrettyPrinter.NEW_LINE + "    {"
                + BasePrettyPrinter.NEW_LINE + "      \"six\" : 28.0,"
                + BasePrettyPrinter.NEW_LINE + "      \"seven\" : 29.0"
                + BasePrettyPrinter.NEW_LINE + "    }"
                + BasePrettyPrinter.NEW_LINE + "  ]]"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        ObjectNode subObj = _nodeProvider.createObjectNode();
        subObj.put("four", 26.0);
        subObj.put("five", 27.0);
        ObjectNode subObjTwo = _nodeProvider.createObjectNode();
        subObjTwo.put("six", 28.0);
        subObjTwo.put("seven", 29.0);
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        subArr.add(subObj);
        subArr.add(subObjTwo);
        arr.add(subArr);
        obj.put("three", arr);
        assertFormatObject(obj, expectedResult);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_priority_properties() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"three\" : 26.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        obj.put("three", 26.0);
        String[] propertyOrder = new String[2];
        propertyOrder[0] = "three";
        propertyOrder[1] = "one";
        _printer.setPropertyOrder(propertyOrder);
        assertFormatObject(obj, expectedResult);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_double_array() {
        String expectedResult = "{ \"one\" : [[ ]] }";
        ObjectNode obj = _nodeProvider.createObjectNode();
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        arr.add(subArr);
        obj.put("one",  arr);
        String result = assertFormatObject(obj, expectedResult);
        String asIntendedResult = _printer.formatAsIntended(new JsonNodeWrapper(obj), 0, false);
        assertEquals(expectedResult, asIntendedResult);
        assertEquals(result, asIntendedResult);
    }

    @Test
    public void formatObject_should_return_single_property_object_with_null_property() {
        String expectedResult = "{ }";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", "null");
        assertFormatObject(obj, expectedResult);
    }

    @Test
    public void formatObject_should_return_single_property_object() {
        String expectedResult = "{ \"one\" : 25.0 }";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 25.0);
        assertFormatObject(obj, expectedResult);
    }

    @Test
    public void formatObject_should_return_single_property_object_with_single_property_sub_object() {
        String expectedResult = "{ \"one\" : { \"two\" : 25.0 } }";
        ObjectNode obj = _nodeProvider.createObjectNode();
        ObjectNode subObj = _nodeProvider.createObjectNode();
        subObj.put("two", 25.0);
        obj.put("one", subObj);
        assertFormatObject(obj, expectedResult);
    }

    @Test
    public void formatObject_should_return_single_property_object_with_sub_object() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : {"
                + BasePrettyPrinter.NEW_LINE + "    \"two\" : 25.0,"
                + BasePrettyPrinter.NEW_LINE + "    \"three\" : 26.0"
                + BasePrettyPrinter.NEW_LINE + "  }"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode obj = _nodeProvider.createObjectNode();
        ObjectNode subObj = _nodeProvider.createObjectNode();
        subObj.put("two", 25.0);
        subObj.put("three", 26.0);
        obj.put("one", subObj);
        assertFormatObject(obj, expectedResult);
    }

    private String assertFormatObject(ObjectNode obj, String expectedResult, int indentSize, boolean shouldIndent) {
        String result = _printer.formatObject(new JsonNodeWrapper(obj), indentSize, shouldIndent);
        assertEquals(expectedResult, result);
        return result;
    }

    private String assertFormatObject(ObjectNode obj, String expectedResult) {
        return assertFormatObject(obj, expectedResult, 0, false);
    }

    //formatArray

    @Test
    public void formatArray_should_return_empty_array() {
        String expectedResult = "[ ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_empty_double_array() {
        String expectedResult = "[[ ]]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        arr.add(subArr);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_array_of_doubles() {
        String expectedResult = "[ 24.0, 25.0, 26.0 ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(24.0);
        arr.add(25.0);
        arr.add(26.0);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_array_of_integers() {
        String expectedResult = "[ 24, 25, 26 ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(24);
        arr.add(25);
        arr.add(26);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_array_of_booleans() {
        String expectedResult = "[ true, false, true ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(true);
        arr.add(false);
        arr.add(true);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_array_of_strings() {
        String expectedResult = "[ \"one\", \"two\", \"three\" ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add("one");
        arr.add("two");
        arr.add("three");
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_exclude_null_entries() {
        String expectedResult = "[ \"one\", \"two\", \"three\" ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add("one");
        arr.add("two");
        arr.add((String)null);
        arr.add("three");
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_combination_array() {
        String expectedResult = "[ \"one\", 2, { } ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add("one");
        arr.add(2);
        ObjectNode obj = _nodeProvider.createObjectNode();
        arr.add(obj);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_single_item_sub_array() {
        String expectedResult = "[[ 24.0 ]]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        subArr.add(24.0);
        arr.add(subArr);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_single_item_sub_array_with_multi_item_sub_arrays() {
        String expectedResult = "[[[[ 24.0, 25.0 ]]]]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        ArrayNode subSubArr = _nodeProvider.createArrayNode();
        ArrayNode subSubSubArr = _nodeProvider.createArrayNode();
        subSubSubArr.add(24.0);
        subSubSubArr.add(25.0);
        subSubArr.add(subSubSubArr);
        subArr.add(subSubArr);
        arr.add(subArr);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_sub_array_of_multi_property_objects_indent_three_levels() {
        String expectedResult = "[[["
                + BasePrettyPrinter.NEW_LINE + "  {"
                + BasePrettyPrinter.NEW_LINE + "    \"one\" : 5,"
                + BasePrettyPrinter.NEW_LINE + "    \"two\" : 6"
                + BasePrettyPrinter.NEW_LINE + "  }"
                + BasePrettyPrinter.NEW_LINE + "]]]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        ArrayNode subSubArr = _nodeProvider.createArrayNode();
        ObjectNode subObj = _nodeProvider.createObjectNode();
        subObj.put("one", 5);
        subObj.put("two", 6);
        subSubArr.add(subObj);
        subArr.add(subSubArr);
        arr.add(subArr);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_sub_array_of_multi_property_objects_different_indents() {
        String expectedResult = "[["
                + BasePrettyPrinter.NEW_LINE + "  [["
                + BasePrettyPrinter.NEW_LINE + "    {"
                + BasePrettyPrinter.NEW_LINE + "      \"one\" : 5,"
                + BasePrettyPrinter.NEW_LINE + "      \"two\" : 6"
                + BasePrettyPrinter.NEW_LINE + "    }"
                + BasePrettyPrinter.NEW_LINE + "  ]],"
                + BasePrettyPrinter.NEW_LINE + "  {"
                + BasePrettyPrinter.NEW_LINE + "    \"three\" : 7,"
                + BasePrettyPrinter.NEW_LINE + "    \"four\" : 8"
                + BasePrettyPrinter.NEW_LINE + "  }"
                + BasePrettyPrinter.NEW_LINE + "]]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        ArrayNode subSubArr = _nodeProvider.createArrayNode();
        ArrayNode subSubSubArr = _nodeProvider.createArrayNode();
        ObjectNode subObj = _nodeProvider.createObjectNode();
        subObj.put("one", 5);
        subObj.put("two", 6);
        subSubSubArr.add(subObj);
        subSubArr.add(subSubSubArr);
        subArr.add(subSubArr);
        ObjectNode subObjTwo = _nodeProvider.createObjectNode();
        subObjTwo.put("three", 7);
        subObjTwo.put("four", 8);
        subArr.add(subObjTwo);
        arr.add(subArr);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_single_property_object_array() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  { \"one\" : 24.0 },"
                + BasePrettyPrinter.NEW_LINE + "  { \"two\" : 25.0 }"
                + BasePrettyPrinter.NEW_LINE + "]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        arr.add(obj);
        ObjectNode objTwo = _nodeProvider.createObjectNode();
        objTwo.put("two", 25.0);
        arr.add(objTwo);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_single_object_multiple_properties_array() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  {"
                + BasePrettyPrinter.NEW_LINE + "    \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "    \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "  }"
                + BasePrettyPrinter.NEW_LINE + "]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        arr.add(obj);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_multi_object_variable_properties_array() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  {"
                + BasePrettyPrinter.NEW_LINE + "    \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "    \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "  },"
                + BasePrettyPrinter.NEW_LINE + "  { \"three\" : 26.0 }"
                + BasePrettyPrinter.NEW_LINE + "]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        ObjectNode objTwo = _nodeProvider.createObjectNode();
        objTwo.put("three", 26.0);
        arr.add(obj);
        arr.add(objTwo);
        assertFormatArray(arr, expectedResult);
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
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_single_object_with_single_sub_objects_array() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  {"
                + BasePrettyPrinter.NEW_LINE + "    \"one\" : {"
                + BasePrettyPrinter.NEW_LINE + "      \"two\" : 25.0,"
                + BasePrettyPrinter.NEW_LINE + "      \"three\" : 26.0"
                + BasePrettyPrinter.NEW_LINE + "    }"
                + BasePrettyPrinter.NEW_LINE + "  }"
                + BasePrettyPrinter.NEW_LINE + "]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ObjectNode obj = _nodeProvider.createObjectNode();
        ObjectNode subObj = _nodeProvider.createObjectNode();
        subObj.put("two", 25.0);
        subObj.put("three", 26.0);
        obj.put("one", subObj);
        arr.add(obj);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_single_object_with_single_sub_array_of_objects_array() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  {"
                + BasePrettyPrinter.NEW_LINE + "    \"one\" : ["
                + BasePrettyPrinter.NEW_LINE + "      {"
                + BasePrettyPrinter.NEW_LINE + "        \"two\" : 25.0,"
                + BasePrettyPrinter.NEW_LINE + "        \"three\" : 26.0"
                + BasePrettyPrinter.NEW_LINE + "      }"
                + BasePrettyPrinter.NEW_LINE + "    ]"
                + BasePrettyPrinter.NEW_LINE + "  }"
                + BasePrettyPrinter.NEW_LINE + "]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ObjectNode obj = _nodeProvider.createObjectNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        ObjectNode subObj = _nodeProvider.createObjectNode();
        subObj.put("two", 25.0);
        subObj.put("three", 26.0);
        subArr.add(subObj);
        obj.put("one", subArr);
        arr.add(obj);
        assertFormatArray(arr, expectedResult);
    }

    @Test
    public void formatArray_should_return_single_object_with_multiple_sub_objects_array() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  {"
                + BasePrettyPrinter.NEW_LINE + "    \"one\" : {"
                + BasePrettyPrinter.NEW_LINE + "      \"two\" : 25.0,"
                + BasePrettyPrinter.NEW_LINE + "      \"three\" : 26.0"
                + BasePrettyPrinter.NEW_LINE + "    },"
                + BasePrettyPrinter.NEW_LINE + "    \"four\" : { \"five\" : 27.0 }"
                + BasePrettyPrinter.NEW_LINE + "  }"
                + BasePrettyPrinter.NEW_LINE + "]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ObjectNode obj = _nodeProvider.createObjectNode();
        ObjectNode subObj = _nodeProvider.createObjectNode();
        subObj.put("two", 25.0);
        subObj.put("three", 26.0);
        obj.put("one", subObj);
        ObjectNode subObjTwo = _nodeProvider.createObjectNode();
        subObjTwo.put("five", 27.0);
        obj.put("four", subObjTwo);
        arr.add(obj);
        assertFormatArray(arr, expectedResult);
    }

    private String assertFormatArray(JsonNode arr, String expectedResult, int indentSize, boolean shouldIndent) {
        String result = _printer.formatArray(new JsonNodeWrapper(arr), indentSize, shouldIndent);
        assertEquals(expectedResult, result);
        return result;
    }

    private String assertFormatArray(JsonNode obj, String expectedResult) {
        return assertFormatArray(obj, expectedResult, 0, false);
    }

    //formatAsIntended

    @Test
    public void formatAsIntended_should_return_null() {
        IJsonWrapper obj = null;
        String result = _printer.formatAsIntended(obj, 0, false);
        assertNull(result);
    }

    @Test
    public void formatAsIntended_should_return_null_for_null_string() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", "null");
        JsonNode toSend = obj.get("blah");
        String result = _printer.formatAsIntended(new JsonNodeWrapper(toSend), 0, false);
        assertNull(result);
    }

    @Test
    public void formatAsIntended_should_return_null_when_toString_is_null() {
        IJsonWrapper wrapped = mock(JsonNodeWrapper.class);
        when(wrapped.toString()).thenReturn(null);
        String result = _printer.formatAsIntended(wrapped, 0, false);
        assertNull(result);
    }

    @Test
    public void formatAsIntended_should_return_double() {
        String expectedResult = "24.0";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", 24.0);
        JsonNode subObj = obj.get("blah");
        assertFormatAsIntended(subObj, expectedResult);
    }

    @Test
    public void formatAsIntended_should_return_integer() {
        String expectedResult = "24";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", 24);
        JsonNode subObj = obj.get("blah");
        assertFormatAsIntended(subObj, expectedResult);
    }

    @Test
    public void formatAsIntended_should_return_boolean() {
        String expectedResult = "true";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", true);
        JsonNode subObj = obj.get("blah");
        assertFormatAsIntended(subObj, expectedResult);
    }

    @Test
    public void formatAsIntended_should_return_string() {
        String expectedResult = "\"yay\"";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", "yay");
        JsonNode subObj = obj.get("blah");
        assertFormatAsIntended(subObj, expectedResult);
    }

    @Test
    public void formatAsIntended_should_return_array() {
        String expectedResult = "[ \"yay\" ]";
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add("yay");
        String result = _printer.formatAsIntended(new JsonNodeWrapper(arr), 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatAsIntended_should_return_single_property_object() {
        String expectedResult = "{ \"one\" : 24.0 }";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        assertFormatAsIntended(obj, expectedResult);
    }

    @Test
    public void formatAsIntended_should_return_object() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        assertFormatAsIntended(obj, expectedResult);
    }

    private String assertFormatAsIntended(JsonNode obj, String expectedResult, int indentSize, boolean shouldIndent) {
        String result = _printer.formatAsIntended(new JsonNodeWrapper(obj), indentSize, shouldIndent);
        assertEquals(expectedResult, result);
        return result;
    }

    private String assertFormatAsIntended(JsonNode obj, String expectedResult) {
        return assertFormatAsIntended(obj, expectedResult, 0, false);
    }

    //wrapObject

    @Test
    public void wrapObject_should_give_null_for_null() {
        IJsonWrapper wrapper = _printer.wrapObject(null);
        assertNull(wrapper);
    }

    @Test
    public void wrapObject_should_give_null_for_unknown() {
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        IJsonWrapper wrapper = _printer.wrapObject(obj);
        assertNull(wrapper);
    }

    @Test
    public void wrapObject_should_give_JSONObjectWrapper_for_JSONObject() {
        IJsonWrapper wrapper = _printer.wrapObject(new JSONObject());
        assertTrue(wrapper instanceof JSONObjectWrapper);
    }

    @Test
    public void wrapObject_should_give_JSONArrayWrapper_for_JSONArray() {
        IJsonWrapper wrapper = _printer.wrapObject(new JSONArray());
        assertTrue(wrapper instanceof JSONArrayWrapper);
    }

    @Test
    public void wrapObject_should_give_JsonNodeWrapper_for_ObjectNode() {
        IJsonWrapper wrapper = _printer.wrapObject(_nodeProvider.createObjectNode());
        assertTrue(wrapper instanceof JsonNodeWrapper);
    }

    @Test
    public void wrapObject_should_give_JsonNodeWrapper_for_ArrayNode() {
        IJsonWrapper wrapper = _printer.wrapObject(_nodeProvider.createArrayNode());
        assertTrue(wrapper instanceof JsonNodeWrapper);
    }

    @Test
    public void wrapObject_should_give_JsonNodeWrapper_for_value_type_node() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", 1);
        IJsonWrapper wrapper = _printer.wrapObject(obj.get("blah"));
        assertTrue(wrapper instanceof JsonNodeWrapper);
    }

    @Test
    public void wrapObject_should_give_ValueTypeWrapper_for_boolean() {
        IJsonWrapper wrapper = _printer.wrapObject(true);
        assertTrue(wrapper instanceof ValueTypeWrapper);
    }

    @Test
    public void wrapObject_should_give_ValueTypeWrapper_for_double() {
        IJsonWrapper wrapper = _printer.wrapObject(24.0);
        assertTrue(wrapper instanceof ValueTypeWrapper);
    }

    @Test
    public void wrapObject_should_give_ValueTypeWrapper_for_integer() {
        IJsonWrapper wrapper = _printer.wrapObject(24);
        assertTrue(wrapper instanceof ValueTypeWrapper);
    }

    @Test
    public void wrapObject_should_give_ValueTypeWrapper_for_strings() {
        IJsonWrapper wrapper = _printer.wrapObject("one");
        assertTrue(wrapper instanceof ValueTypeWrapper);
    }
}
