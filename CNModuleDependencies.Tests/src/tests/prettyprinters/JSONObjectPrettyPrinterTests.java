package tests.prettyprinters;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.prettyprinters.BasePrettyPrinter;
import com.colonolnutty.module.shareddata.prettyprinters.JSONObjectPrettyPrinter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * User: Jack's Computer
 * Date: 12/31/2017
 * Time: 12:47 PM
 */
public class JSONObjectPrettyPrinterTests {
    private JSONObjectPrettyPrinter _printer;
    private NodeProvider _nodeProvider;

    public JSONObjectPrettyPrinterTests() {
        _printer = new JSONObjectPrettyPrinter();
        _nodeProvider = new NodeProvider();
    }

    //formatObject

    @Test
    public void formatObject_should_return_formatted_object_with_null_properties() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"three\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        obj.put("two", "null");
        obj.put("three", 25.0);
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_single_property_object_with_null_property() {
        String expectedResult = "{ }";
        JSONObject obj = new JSONObject();
        obj.put("one", "null");
        String result = _printer.formatObject(obj, 0, false);
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
        JSONObject obj = new JSONObject();
        JSONObject subObj = new JSONObject();
        subObj.put("two", 24.0);
        subObj.put("three", 25.0);
        obj.put("one", subObj);
        String result = _printer.formatObject(obj, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
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
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        JSONArray arr = new JSONArray();
        JSONArray subArrOne = new JSONArray();
        subArrOne.put(0, 26.0);
        JSONArray subArrTwo = new JSONArray();
        subArrTwo.put(0, 27.0);
        arr.put(0, subArrOne);
        arr.put(1, subArrTwo);
        obj.put("three", arr);
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
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
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        JSONObject subObj = new JSONObject();
        subObj.put("four", 26.0);
        subObj.put("five", 27.0);
        obj.put("three", subObj);
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_sub_array_of_objects() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"three\" : ["
                + BasePrettyPrinter.NEW_LINE + "    ["
                + BasePrettyPrinter.NEW_LINE + "      {"
                + BasePrettyPrinter.NEW_LINE + "        \"four\" : 26.0,"
                + BasePrettyPrinter.NEW_LINE + "        \"five\" : 27.0"
                + BasePrettyPrinter.NEW_LINE + "      },"
                + BasePrettyPrinter.NEW_LINE + "      {"
                + BasePrettyPrinter.NEW_LINE + "        \"six\" : 28.0,"
                + BasePrettyPrinter.NEW_LINE + "        \"seven\" : 29.0"
                + BasePrettyPrinter.NEW_LINE + "      }"
                + BasePrettyPrinter.NEW_LINE + "    ]"
                + BasePrettyPrinter.NEW_LINE + "  ]"
                + BasePrettyPrinter.NEW_LINE + "}";
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        JSONObject subObj = new JSONObject();
        subObj.put("four", 26.0);
        subObj.put("five", 27.0);
        JSONObject subObjTwo = new JSONObject();
        subObjTwo.put("six", 28.0);
        subObjTwo.put("seven", 29.0);
        JSONArray arr = new JSONArray();
        JSONArray subArr = new JSONArray();
        subArr.put(0, subObj);
        subArr.put(1, subObjTwo);
        arr.put(0, subArr);
        obj.put("three", arr);
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_priority_properties() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"three\" : 26.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        obj.put("three", 26.0);
        String[] propertyOrder = new String[2];
        propertyOrder[0] = "three";
        propertyOrder[1] = "one";
        _printer.setPropertyOrder(propertyOrder);
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_double_array() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : [[ ]]"
                + BasePrettyPrinter.NEW_LINE + "}";
        JSONObject obj = new JSONObject();
        JSONArray arrNodeOne = new JSONArray();
        JSONArray subNodeArr = new JSONArray();
        arrNodeOne.put(0, subNodeArr);
        obj.put("one",  arrNodeOne);
        String asIntendedResult = _printer.formatAsIntended(obj, 0, false);
        assertEquals(expectedResult, asIntendedResult);
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
        assertEquals(result, asIntendedResult);
    }

    //formatArray

    @Test
    public void formatArray_should_return_empty_array() {
        String expectedResult = "[ ]";
        JSONArray arr = new JSONArray();
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_empty_double_array() {
        String expectedResult = "[[ ]]";
        JSONArray arr = new JSONArray();
        JSONArray subArr = new JSONArray();
        arr.put(0, subArr);
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_double_array() {
        String expectedResult = "[ 24.0, 25.0, 26.0 ]";
        JSONArray arr = new JSONArray();
        arr.put(0, 24.0);
        arr.put(1, 25.0);
        arr.put(2, 26.0);
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_string_array() {
        String expectedResult = "[ \"one\", \"two\", \"three\" ]";
        JSONArray arr = new JSONArray();
        arr.put(0, "one");
        arr.put(1, "two");
        arr.put(2, "three");
        String result = _printer.formatArray(arr, 0, false);
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
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_combination_array() {
        String expectedResult = "[ \"one\", 2, { } ]";
        JSONArray arr = new JSONArray();
        arr.put(0, "one");
        arr.put(1, 2);
        JSONObject obj = new JSONObject();
        arr.put(2, obj);
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_single_property_object_array() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  { \"one\" : 24.0 },"
                + BasePrettyPrinter.NEW_LINE + "  { \"two\" : 25.0 }"
                + BasePrettyPrinter.NEW_LINE + "]";
        JSONArray arr = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        arr.put(0, obj);
        JSONObject objTwo = new JSONObject();
        objTwo.put("two", 25.0);
        arr.put(1, objTwo);
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_combination_single_property_object_array() {
        String expectedResult = "[ \"one\", 2, { \"three\" : 24 } ]";
        JSONArray arr = new JSONArray();
        arr.put(0, "one");
        arr.put(1, 2);
        JSONObject obj = new JSONObject();
        obj.put("three", 24);
        arr.put(2, obj);
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_single_object_multiple_properties_array() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  {"
                + BasePrettyPrinter.NEW_LINE + "    \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "    \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "  }"
                + BasePrettyPrinter.NEW_LINE + "]";
        JSONArray arr = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        arr.put(0, obj);
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    //formatAsIntended

    @Test
    public void formatAsIntended_should_return_null() {
        String result = _printer.formatAsIntended(null, 0, false);
        assertNull(result);
    }

    @Test
    public void formatAsIntended_should_return_double() {
        JSONObject obj = new JSONObject();
        obj.put("blah", 24.0);
        String result = _printer.formatAsIntended(obj.get("blah"), 0, false);
        assertEquals("24.0", result);
    }

    @Test
    public void formatAsIntended_should_return_integer() {
        JSONObject obj = new JSONObject();
        obj.put("blah", 24);
        String result = _printer.formatAsIntended(obj.get("blah"), 0, false);
        assertEquals("24", result);
    }

    @Test
    public void formatAsIntended_should_return_boolean() {
        JSONObject obj = new JSONObject();
        obj.put("blah", true);
        String result = _printer.formatAsIntended(obj.get("blah"), 0, false);
        assertEquals("true", result);
    }

    @Test
    public void formatAsIntended_should_return_string() {
        JSONObject obj = new JSONObject();
        obj.put("blah", "yay");
        String result = _printer.formatAsIntended(obj.get("blah"), 0, false);
        assertEquals("\"yay\"", result);
    }

    @Test
    public void formatAsIntended_should_return_array() {
        String expectedResult = "[ \"yay\" ]";
        JSONArray arr = new JSONArray();
        arr.put(0, "yay");
        String result = _printer.formatAsIntended(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatAsIntended_should_return_single_property_object() {
        String expectedResult = "{ \"one\" : 24.0 }";
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        String result = _printer.formatAsIntended(obj, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatAsIntended_should_return_object() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        String result = _printer.formatAsIntended(obj, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatAsIntended_should_return_formatted_object() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : [[ ]]"
                + BasePrettyPrinter.NEW_LINE + "}";
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        JSONArray subArr = new JSONArray();
        arr.put(0, subArr);
        obj.put("one",  arr);
        String asIntendedResult = _printer.formatAsIntended(obj, 0, false);
        assertEquals(expectedResult, asIntendedResult);
        String result = _printer.formatObject(obj, 0, false);
        assertEquals(expectedResult, result);
        assertEquals(result, asIntendedResult);
    }
}
