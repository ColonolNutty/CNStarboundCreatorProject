package tests.jsonhandlers;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.jsonhandlers.FoodValueHandler;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Jack's Computer
 * Date: 01/03/2018
 * Time: 2:20 PM
 */
public class FoodValueHandlerTests {
    private NodeProvider _nodeProvider;
    private FoodValueHandler _handler;

    public FoodValueHandlerTests() {
        _nodeProvider = new NodeProvider();
        _handler = new FoodValueHandler();
    }

    //createTestNode

    @Test
    public void createTestNode_should_give_null_when_ingredient_has_null_foodValue() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = null;
        JsonNode result = _handler.createTestNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createTestNode_should_give_null_when_ingredient_has_negative_foodValue() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = -20.0;
        JsonNode result = _handler.createTestNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createTestNode_should_give_test_node_when_ingredient_has_foodValue() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = 24.0;
        ArrayNode testNode = _nodeProvider.createArrayNode();
        NodeProvider mockNodeProvider = mock(NodeProvider.class);
        when(mockNodeProvider.createTestAddDoubleNode(FoodValueHandler.PATH_NAME)).thenReturn(testNode);
        _handler.setNodeProvider(mockNodeProvider);
        JsonNode result = _handler.createTestNode(ingredient);
        assertEquals(testNode, result);
    }

    //createReplaceNode

    @Test
    public void createReplaceNode_should_give_null_when_ingredient_has_null_foodValue() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = null;
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertNull(result);
    }
    @Test
    public void createReplaceNode_should_give_null_when_ingredient_has_negative_foodValue() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = -20.0;
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createReplaceNode_should_give_replace_node_when_ingredient_has_foodValue() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = 24.0;
        ObjectNode testNode = _nodeProvider.createObjectNode();
        NodeProvider mockNodeProvider = mock(NodeProvider.class);
        when(mockNodeProvider.createReplaceDoubleNode(FoodValueHandler.PATH_NAME, ingredient.foodValue)).thenReturn(testNode);
        _handler.setNodeProvider(mockNodeProvider);
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertEquals(testNode, result);
    }

    //canHandle

    @Test
    public void canHandle_should_return_true_when_path_matches() {
        boolean result = _handler.canHandle(FoodValueHandler.PATH_NAME);
        assertTrue(result);
    }

    @Test
    public void canHandle_should_return_false_when_path_does_not_match() {
        boolean result = _handler.canHandle("/someOtherPath");
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_true_when_ingredient_has_foodValue_and_value_does_not_exist() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = 24.0;
        ObjectNode obj = _nodeProvider.createObjectNode();
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }

    @Test
    public void needsUpdate_should_return_true_if_node_value_is_non_double() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = 0.0;
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", "no");
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }

    @Test
    public void needsUpdate_should_return_false_when_both_are_null() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = null;
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", (Double) null);
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_false_when_node_is_array() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = 24.0;
        ArrayNode arr = _nodeProvider.createArrayNode();
        boolean result = _handler.needsUpdate(arr, ingredient);
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_false_when_existing_and_ingredient_are_same() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = 24.0;
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", 24.0);
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_true_when_existing_is_null_and_ingredient_is_not() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = 24.0;
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", (Double) null);
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }

    @Test
    public void needsUpdate_should_return_true_when_value_foodValue_is_different() {
        Ingredient ingredient = new Ingredient();
        ingredient.foodValue = 24.0;
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", 25.0);
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }
}
