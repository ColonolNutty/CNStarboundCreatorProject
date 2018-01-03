package tests.jsonhandlers;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.jsonhandlers.EffectsHandler;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.util.Hashtable;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

/**
 * User: Jack's Computer
 * Date: 01/03/2018
 * Time: 12:35 PM
 */
public class EffectsHandlerTests {

    private NodeProvider _nodeProvider;
    private EffectsHandler _handler;

    public EffectsHandlerTests() {
        _nodeProvider = new NodeProvider();
        _handler = new EffectsHandler();
    }

    //needsUpdate

    @Test
    public void needsUpdate_should_return_true_when_ingredient_has_effects_and_existing_does_not() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        subArr.add(createEffectNode("one", 1));
        arr.add(subArr);
        Ingredient ingredient = new Ingredient();
        ingredient.effects = arr;
        boolean result = _handler.needsUpdate(null, ingredient);
        assertTrue(result);
    }

    @Test
    public void needsUpdate_should_return_true_when_existing_has_effects_and_ingredient_does_not() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        subArr.add(createEffectNode("one", 1));
        arr.add(subArr);
        Ingredient ingredient = new Ingredient();
        ingredient.effects = null;
        boolean result = _handler.needsUpdate(arr, ingredient);
        assertTrue(result);
    }

    @Test
    public void needsUpdate_should_return_false_when_ingredient_and_existing_are_null() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        Ingredient ingredient = new Ingredient();
        ingredient.effects = null;
        boolean result = _handler.needsUpdate(arr, ingredient);
        assertFalse(result);
    }

    //createTestNode

    @Test
    public void createTestNode_should_give_null_when_ingredient_has_no_effects() {
        Ingredient ingredient = new Ingredient();
        ingredient.effects = null;
        JsonNode result = _handler.createTestNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createTestNode_should_give_test_node_when_ingredient_has_effects() {
        Ingredient ingredient = new Ingredient();
        ingredient.effects = _nodeProvider.createArrayNode();
        ArrayNode testNode = _nodeProvider.createArrayNode();
        NodeProvider mockNodeProvider = mock(NodeProvider.class);
        when(mockNodeProvider.createTestAddArrayNode(EffectsHandler.PATH_NAME)).thenReturn(testNode);
        _handler.setNodeProvider(mockNodeProvider);
        JsonNode result = _handler.createTestNode(ingredient);
        assertEquals(testNode, result);
    }

    //createReplaceNode

    @Test
    public void createReplaceNode_should_give_null_when_ingredient_has_no_effects() {
        Ingredient ingredient = new Ingredient();
        ingredient.effects = null;
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createReplaceNode_should_give_replace_node_when_ingredient_has_effects() {
        Ingredient ingredient = new Ingredient();
        ingredient.effects = _nodeProvider.createArrayNode();
        ObjectNode testNode = _nodeProvider.createObjectNode();
        NodeProvider mockNodeProvider = mock(NodeProvider.class);
        when(mockNodeProvider.createReplaceArrayNode(EffectsHandler.PATH_NAME, ingredient.effects)).thenReturn(testNode);
        _handler.setNodeProvider(mockNodeProvider);
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertEquals(testNode, result);
    }

    //canHandle

    @Test
    public void canHandle_should_return_true_when_path_name_matches() {
        boolean result = _handler.canHandle(EffectsHandler.PATH_NAME);
        assertTrue(result);
    }

    @Test
    public void canHandle_should_return_true_when_path_name_does_not_match() {
        boolean result = _handler.canHandle("/someOtherPath");
        assertFalse(result);
    }

    //getEffectsNodeFromNode

    @Test
    public void getEffectsNodeFromNode_should_give_null_if_object_has_no_value_property() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        JsonNode result = _handler.getEffectsNodeFromNode(obj);
        assertNull(result);
    }

    @Test
    public void getEffectsNodeFromNode_should_give_null_when_node_is_value_type() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", "24");
        JsonNode result = _handler.getEffectsNodeFromNode(obj.get("blah"));
        assertNull(result);
    }

    @Test
    public void getEffectsNodeFromNode_should_give_null_when_object_value_property_is_an_object() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        ObjectNode subObj = _nodeProvider.createObjectNode();
        obj.put("value", subObj);
        JsonNode result = _handler.getEffectsNodeFromNode(obj);
        assertNull(result);
    }

    @Test
    public void getEffectsNodeFromNode_should_give_null_when_object_value_property_is_value_type() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", "24");
        JsonNode result = _handler.getEffectsNodeFromNode(obj);
        assertNull(result);
    }

    @Test
    public void getEffectsNodeFromNode_should_give_array_when_object_value_property_is_an_array() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        ArrayNode arr = _nodeProvider.createArrayNode();
        obj.put("value", arr);
        JsonNode result = _handler.getEffectsNodeFromNode(obj);
        assertEquals(arr, result);
    }

    @Test
    public void getEffectsNodeFromNode_should_give_array_when_node_is_array() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        JsonNode result = _handler.getEffectsNodeFromNode(arr);
        assertEquals(arr, result);
    }

    //getEffects

    @Test
    public void getEffects_should_give_null_when_node_is_null() {
        Hashtable<String, Integer> result = _handler.getEffects(null);
        assertNull(result);
    }

    @Test
    public void getEffects_should_give_null_when_node_is_an_object() {
        ObjectNode obj = _nodeProvider.createObjectNode();
        Hashtable<String, Integer> result = _handler.getEffects(obj);
        assertNull(result);
    }

    @Test
    public void getEffects_should_give_null_when_node_is_an_empty_array() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        Hashtable<String, Integer> result = _handler.getEffects(arr);
        assertNull(result);
    }

    @Test
    public void getEffects_should_give_null_when_node_contains_more_than_one_item() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(_nodeProvider.createArrayNode());
        arr.add(_nodeProvider.createArrayNode());
        Hashtable<String, Integer> result = _handler.getEffects(arr);
        assertNull(result);
    }

    @Test
    public void getEffects_should_give_null_when_node_contains_an_object() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(_nodeProvider.createObjectNode());
        Hashtable<String, Integer> result = _handler.getEffects(arr);
        assertNull(result);
    }

    @Test
    public void getEffects_should_give_null_when_node_contains_an_empty_array() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(_nodeProvider.createArrayNode());
        Hashtable<String, Integer> result = _handler.getEffects(arr);
        assertNull(result);
    }

    @Test
    public void getEffects_should_give_null_when_node_contains_an_array_of_arrays() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        subArr.add(_nodeProvider.createArrayNode());
        arr.add(subArr);
        Hashtable<String, Integer> result = _handler.getEffects(arr);
        assertNull(result);
    }

    @Test
    public void getEffects_should_give_null_when_node_contains_an_array_of_non_effect_objects() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", "1");
        subArr.add(obj);
        arr.add(subArr);
        Hashtable<String, Integer> result = _handler.getEffects(arr);
        assertNull(result);
    }

    @Test
    public void getEffects_should_give_null_when_node_contains_an_array_of_objects_missing_duration() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        ObjectNode obj = createEffectNode("one", null);
        subArr.add(obj);
        arr.add(subArr);
        Hashtable<String, Integer> result = _handler.getEffects(arr);
        assertNull(result);
    }

    @Test
    public void getEffects_should_give_null_when_node_contains_an_array_of_objects_missing_effect_property() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        ObjectNode obj = createEffectNode(null, 12);
        subArr.add(obj);
        arr.add(subArr);
        Hashtable<String, Integer> result = _handler.getEffects(arr);
        assertNull(result);
    }

    @Test
    public void getEffects_should_give_effects() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();

        ObjectNode obj = createEffectNode("one", 1);
        subArr.add(obj);
        ObjectNode objTwo = createEffectNode("two", 2);
        subArr.add(objTwo);

        arr.add(subArr);

        Hashtable<String, Integer> result = _handler.getEffects(arr);

        assertNotNull(result);
        assertTrue(result.containsKey("one"));
        assertEquals(1, (int)result.get("one"));
        assertTrue(result.containsKey("two"));
        assertEquals(2, (int)result.get("two"));
    }

    @Test
    public void getEffects_should_exclude_invalid_effects() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();

        ArrayNode subSubArr = _nodeProvider.createArrayNode();
        subArr.add(subSubArr);
        ObjectNode obj = createEffectNode("one", 1);
        subArr.add(obj);
        subArr.add("24");
        ObjectNode objTwo = createEffectNode("two", 2);
        subArr.add(objTwo);
        ObjectNode objThree = createEffectNode(null, 3);
        subArr.add(objThree);
        ObjectNode objFour = createEffectNode("four", null);
        subArr.add(objFour);
        ObjectNode objFive = createEffectNode("two", 5);
        subArr.add(objFive);
        ObjectNode objSix = _nodeProvider.createObjectNode();
        objSix.put("effect", 5);
        objSix.put("duration", 1);
        subArr.add(objSix);
        ObjectNode objSeven = _nodeProvider.createObjectNode();
        objSix.put("effect", "seven");
        objSix.put("duration", "no");
        subArr.add(objSeven);

        arr.add(subArr);

        Hashtable<String, Integer> result = _handler.getEffects(arr);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("one"));
        assertEquals(1, (int)result.get("one"));
        assertTrue(result.containsKey("two"));
        assertEquals(2, (int)result.get("two"));
    }

    private ObjectNode createEffectNode(String name, Integer duration) {
        ObjectNode obj = _nodeProvider.createObjectNode();
        if(name != null) {
            obj.put("effect", name);
        }
        if(duration != null) {
            obj.put("duration", duration);
        }
        return obj;
    }

    //checkShouldUpdate

    @Test
    public void checkShouldUpdate_should_return_true_when_sizes_are_different() {
        Hashtable<String, Integer> one = new Hashtable<String, Integer>();
        one.put("blah", 2);
        Hashtable<String, Integer> two = new Hashtable<String, Integer>();
        boolean result = _handler.checkShouldUpdate(one, two);
        assertTrue(result);
    }

    @Test
    public void checkShouldUpdate_should_return_true_when_keys_are_different() {
        Hashtable<String, Integer> one = new Hashtable<String, Integer>();
        one.put("blah", 2);
        Hashtable<String, Integer> two = new Hashtable<String, Integer>();
        one.put("blah2", 2);
        boolean result = _handler.checkShouldUpdate(one, two);
        assertTrue(result);
    }

    @Test
    public void checkShouldUpdate_should_return_true_when_values_are_different() {
        Hashtable<String, Integer> one = new Hashtable<String, Integer>();
        one.put("blah", 1);
        Hashtable<String, Integer> two = new Hashtable<String, Integer>();
        one.put("blah", 2);
        boolean result = _handler.checkShouldUpdate(one, two);
        assertTrue(result);
    }

    @Test
    public void checkShouldUpdate_should_return_false_when_values_are_same() {
        Hashtable<String, Integer> one = new Hashtable<String, Integer>();
        one.put("blah", 2);
        Hashtable<String, Integer> two = new Hashtable<String, Integer>();
        one.put("blah", 2);
        boolean result = _handler.checkShouldUpdate(one, two);
        assertTrue(result);
    }

    @Test
    public void getShortStringValue_should_return_null_when_ingredient_has_no_effects() {
        Ingredient ingredient = mock(Ingredient.class);
        when(ingredient.hasEffects()).thenReturn(false);
        String result = _handler.getShortStringValue(ingredient);
        assertNull(result);
    }

    @Test
    public void getShortStringValue_should_give_null_when_node_contains_more_than_one_item() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(_nodeProvider.createArrayNode());
        arr.add(_nodeProvider.createArrayNode());
        getShortStringValue_runAndAssert(arr, null);
    }

    @Test
    public void getShortStringValue_should_give_null_when_node_contains_an_object() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(_nodeProvider.createObjectNode());
        getShortStringValue_runAndAssert(arr, null);
    }

    @Test
    public void getShortStringValue_should_give_null_when_node_contains_an_empty_array() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        arr.add(_nodeProvider.createArrayNode());
        getShortStringValue_runAndAssert(arr, null);
    }

    @Test
    public void getShortStringValue_should_give_null_when_node_contains_an_array_of_arrays() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        subArr.add(_nodeProvider.createArrayNode());
        arr.add(subArr);
        getShortStringValue_runAndAssert(arr, null);
    }

    @Test
    public void getShortStringValue_should_give_null_when_node_contains_an_array_of_non_effect_objects() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("blah", "1");
        subArr.add(obj);
        arr.add(subArr);
        getShortStringValue_runAndAssert(arr, null);
    }

    @Test
    public void getShortStringValue_should_give_null_when_node_contains_an_array_of_objects_missing_duration() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        ObjectNode obj = createEffectNode("one", null);
        subArr.add(obj);
        arr.add(subArr);
        getShortStringValue_runAndAssert(arr, null);
    }

    @Test
    public void getShortStringValue_should_give_null_when_node_contains_an_array_of_objects_missing_effect_property() {
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        ObjectNode obj = createEffectNode(null, 12);
        subArr.add(obj);
        arr.add(subArr);
        getShortStringValue_runAndAssert(arr, null);
    }

    @Test
    public void getShortStringValue_should_return_effects_of_ingredient() {
        String expectedResult = "effects: { n: \"one\" d: 1 }, { n: \"two\" d: 2 }";
        ArrayNode arr = _nodeProvider.createArrayNode();
        ArrayNode subArr = _nodeProvider.createArrayNode();
        ObjectNode obj = createEffectNode("one", 1);
        subArr.add(obj);
        ObjectNode objTwo = createEffectNode("two", 2);
        subArr.add(objTwo);
        arr.add(subArr);
        getShortStringValue_runAndAssert(arr, expectedResult);
    }

    private void getShortStringValue_runAndAssert(ArrayNode arr, String expectedResult) {
        Ingredient ingredient = new Ingredient();
        ingredient.effects = arr;
        String result = _handler.getShortStringValue(ingredient);
        assertEquals(expectedResult, result);
    }
}
