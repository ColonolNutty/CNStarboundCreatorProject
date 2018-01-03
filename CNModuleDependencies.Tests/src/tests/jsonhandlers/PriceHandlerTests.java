package tests.jsonhandlers;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.jsonhandlers.PriceHandler;
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
public class PriceHandlerTests {
    private NodeProvider _nodeProvider;
    private PriceHandler _handler;

    public PriceHandlerTests() {
        _nodeProvider = new NodeProvider();
        _handler = new PriceHandler();
    }

    //createTestNode

    @Test
    public void createTestNode_should_give_null_when_ingredient_has_null_price() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = null;
        JsonNode result = _handler.createTestNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createTestNode_should_give_null_when_ingredient_has_negative_price() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = -20.0;
        JsonNode result = _handler.createTestNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createTestNode_should_give_test_node_when_ingredient_has_price() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = 24.0;
        ArrayNode testNode = _nodeProvider.createArrayNode();
        NodeProvider mockNodeProvider = mock(NodeProvider.class);
        when(mockNodeProvider.createTestAddDoubleNode(PriceHandler.PATH_NAME)).thenReturn(testNode);
        _handler.setNodeProvider(mockNodeProvider);
        JsonNode result = _handler.createTestNode(ingredient);
        assertEquals(testNode, result);
    }

    //createReplaceNode

    @Test
    public void createReplaceNode_should_give_null_when_ingredient_has_null_price() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = null;
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertNull(result);
    }
    @Test
    public void createReplaceNode_should_give_null_when_ingredient_has_negative_price() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = -20.0;
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createReplaceNode_should_give_replace_node_when_ingredient_has_price() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = 24.0;
        ObjectNode testNode = _nodeProvider.createObjectNode();
        NodeProvider mockNodeProvider = mock(NodeProvider.class);
        when(mockNodeProvider.createReplaceDoubleNode(PriceHandler.PATH_NAME, ingredient.price)).thenReturn(testNode);
        _handler.setNodeProvider(mockNodeProvider);
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertEquals(testNode, result);
    }

    //canHandle

    @Test
    public void canHandle_should_return_true_when_path_matches() {
        boolean result = _handler.canHandle(PriceHandler.PATH_NAME);
        assertTrue(result);
    }

    @Test
    public void canHandle_should_return_false_when_path_does_not_match() {
        boolean result = _handler.canHandle("/someOtherPath");
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_true_when_ingredient_has_price_and_value_does_not_exist() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = 24.0;
        ObjectNode obj = _nodeProvider.createObjectNode();
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }

    @Test
    public void needsUpdate_should_return_true_if_node_value_is_non_double() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = 0.0;
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", "no");
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }

    @Test
    public void needsUpdate_should_return_false_when_both_are_null() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = null;
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", (Double) null);
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_false_when_node_is_array() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = 24.0;
        ArrayNode arr = _nodeProvider.createArrayNode();
        boolean result = _handler.needsUpdate(arr, ingredient);
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_false_when_existing_and_ingredient_are_same() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = 24.0;
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", 24.0);
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_true_when_existing_is_null_and_ingredient_is_not() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = 24.0;
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", (Double) null);
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }

    @Test
    public void needsUpdate_should_return_true_when_value_price_is_different() {
        Ingredient ingredient = new Ingredient();
        ingredient.price = 24.0;
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", 25.0);
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }
}
