package tests.prettyprinters;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.prettyprinters.BasePrettyPrinter;
import com.colonolnutty.module.shareddata.prettyprinters.UnspecificPrettyPrinter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Hashtable;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * User: Jack's Computer
 * Date: 12/31/2017
 * Time: 1:40 PM
 */
public class UnspecificPrettyPrinterTests {

    private UnspecificPrettyPrinter _printer;
    private NodeProvider _nodeProvider;

    public UnspecificPrettyPrinterTests() {
        _printer = new UnspecificPrettyPrinter();
        _nodeProvider = new NodeProvider();
    }

    @Test
    public void makePretty_formats_null_as_null() {
        String result = _printer.makePretty(null, 0);
        assertNull(result);
    }

    @Test
    public void makePretty_formats_unknown_as_null() {
        Object obj = new Hashtable<String, Object>();
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
    public void makePretty_formats_ObjectNode() {
        String expectedResult = "{ }";
        ObjectNode obj = _nodeProvider.createObjectNode();
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePretty_formats_ArrayNode() {
        String expectedResult = "[ ]";
        ArrayNode obj = _nodeProvider.createArrayNode();
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makePretty_formats_JsonNode_Value_Type() {
        String expectedResult = "24.0";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", 24.0);
        String result = _printer.makePretty(obj.get("blah"), 0);
        assertEquals(expectedResult, result);
    }

    //formatObject

    @Test
    public void formatObject_should_return_formatted_object() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"two\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        String[] propertyOrder = new String[2];
        propertyOrder[0] = "one";
        propertyOrder[1] = "two";
        _printer.setPropertyOrder(propertyOrder);
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        String result = _printer.makePretty(obj, 0);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatObject_should_return_formatted_object_with_null_property() {
        String expectedResult = "{"
                + BasePrettyPrinter.NEW_LINE + "  \"one\" : 24.0,"
                + BasePrettyPrinter.NEW_LINE + "  \"three\" : 25.0"
                + BasePrettyPrinter.NEW_LINE + "}";
        String[] propertyOrder = new String[3];
        propertyOrder[0] = "one";
        propertyOrder[1] = "two";
        propertyOrder[2] = "three";
        _printer.setPropertyOrder(propertyOrder);
        JSONObject obj = new JSONObject();
        obj.put("one", 24.0);
        obj.put("two", "null");
        obj.put("three", 25.0);
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
        String[] propertyOrder = new String[3];
        propertyOrder[0] = "one";
        propertyOrder[1] = "two";
        propertyOrder[2] = "three";
        _printer.setPropertyOrder(propertyOrder);
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
        String[] propertyOrder = new String[5];
        propertyOrder[0] = "one";
        propertyOrder[1] = "two";
        propertyOrder[2] = "three";
        propertyOrder[3] = "four";
        propertyOrder[4] = "five";
        _printer.setPropertyOrder(propertyOrder);
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
        String[] propertyOrder = new String[7];
        propertyOrder[0] = "one";
        propertyOrder[1] = "two";
        propertyOrder[2] = "three";
        propertyOrder[3] = "four";
        propertyOrder[4] = "five";
        propertyOrder[5] = "six";
        propertyOrder[6] = "seven";
        _printer.setPropertyOrder(propertyOrder);
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
        String expectedResult = "{ \"one\" : [[ ]] }";
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        ArrayList<Object> arr = new ArrayList<Object>();
        ArrayList<Object> subArr = new ArrayList<Object>();
        arr.add(subArr);
        obj.put("one",  arr);
        String result = _printer.formatObject(obj, 0, false);
        assertEquals(expectedResult, result);
        String asIntendedResult = _printer.formatAsIntended(obj, 0, false);
        assertEquals(expectedResult, asIntendedResult);
        assertEquals(result, asIntendedResult);
    }

    @Test
    public void formatObject_should_return_single_property_object_with_null_property() {
        String expectedResult = "{ }";
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        obj.put("one", "null");
        String result = _printer.formatObject(obj, 0, false);
        assertEquals(expectedResult, result);
    }

    //formatArray

    @Test
    public void formatArray_should_return_empty_array() {
        String expectedResult = "[ ]";
        String result = _printer.formatArray(new ArrayList<Object>(), 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_empty_double_array() {
        String expectedResult = "[[ ]]";
        ArrayList<Object> arr = new ArrayList<Object>();
        ArrayList<Object> subArr = new ArrayList<Object>();
        arr.add(subArr);
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_double_array() {
        String expectedResult = "[ 24.0, 25.0, 26.0 ]";
        ArrayList<Object> arr = new ArrayList<Object>();
        arr.add(24.0);
        arr.add(25.0);
        arr.add(26.0);
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_string_array() {
        String expectedResult = "[ \"one\", \"two\", \"three\" ]";
        ArrayList<Object> arr = new ArrayList<Object>();
        arr.add("one");
        arr.add("two");
        arr.add("three");
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_exclude_null_entries() {
        String expectedResult = "[ \"one\", \"two\", \"three\" ]";
        ArrayList<Object> arr = new ArrayList<Object>();
        arr.add("one");
        arr.add("two");
        arr.add((String)null);
        arr.add("three");
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_combination_array() {
        String expectedResult = "[ \"one\", 2, { } ]";
        ArrayList<Object> arr = new ArrayList<Object>();
        arr.add("one");
        arr.add(2);
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        arr.add(obj);
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_single_property_object_array() {
        String expectedResult = "["
                + BasePrettyPrinter.NEW_LINE + "  { \"one\" : 24.0 },"
                + BasePrettyPrinter.NEW_LINE + "  { \"two\" : 25.0 }"
                + BasePrettyPrinter.NEW_LINE + "]";
        ArrayList<Object> arr = new ArrayList<Object>();
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        obj.put("one", 24.0);
        arr.add(obj);
        Hashtable<String, Object> objTwo = new Hashtable<String, Object>();
        objTwo.put("two", 25.0);
        arr.add(objTwo);
        String result = _printer.formatArray(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatArray_should_return_combination_single_property_object_array() {
        String expectedResult = "[ \"one\", 2, { \"three\" : 24 } ]";
        ArrayList<Object> arr = new ArrayList<Object>();
        arr.add("one");
        arr.add(2);
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        obj.put("three", 24);
        arr.add(obj);
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
        ArrayList<Object> arr = new ArrayList<Object>();
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        arr.add(obj);
        String result = _printer.makePretty(arr, 0);
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
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        obj.put("blah", 24.0);
        String result = _printer.formatAsIntended(obj.get("blah"), 0, false);
        assertEquals("24.0", result);
    }

    @Test
    public void formatAsIntended_should_return_integer() {
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        obj.put("blah", 24);
        String result = _printer.formatAsIntended(obj.get("blah"), 0, false);
        assertEquals("24", result);
    }

    @Test
    public void formatAsIntended_should_return_boolean() {
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        obj.put("blah", true);
        String result = _printer.formatAsIntended(obj.get("blah"), 0, false);
        assertEquals("true", result);
    }

    @Test
    public void formatAsIntended_should_return_string() {
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        obj.put("blah", "yay");
        String result = _printer.formatAsIntended(obj.get("blah"), 0, false);
        assertEquals("\"yay\"", result);
    }

    @Test
    public void formatAsIntended_should_return_array() {
        String expectedResult = "[ \"yay\" ]";
        ArrayList<Object> arr = new ArrayList<Object>();
        arr.add("yay");
        String result = _printer.formatAsIntended(arr, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatAsIntended_should_return_single_property_object() {
        String expectedResult = "{ \"one\" : 24.0 }";
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
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
        String[] propertyOrder = new String[2];
        propertyOrder[0] = "one";
        propertyOrder[1] = "two";
        _printer.setPropertyOrder(propertyOrder);
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        obj.put("one", 24.0);
        obj.put("two", 25.0);
        String result = _printer.formatAsIntended(obj, 0, false);
        assertEquals(expectedResult, result);
    }

    @Test
    public void formatAsIntended_should_return_formatted_object() {
        String expectedResult = "{ \"one\" : [[ ]] }";
        Hashtable<String, Object> obj = new Hashtable<String, Object>();
        ArrayList<Object> arr = new ArrayList<Object>();
        ArrayList<Object> subArr = new ArrayList<Object>();
        arr.add(subArr);
        obj.put("one",  arr);
        String asIntendedResult = _printer.formatAsIntended(obj, 0, false);
        assertEquals(expectedResult, asIntendedResult);
        String result = _printer.formatObject(obj, 0, false);
        assertEquals(expectedResult, result);
        assertEquals(result, asIntendedResult);
    }
}
