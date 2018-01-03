package tests.jsonhandlers;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.jsonhandlers.DescriptionHandler;
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
 * Time: 1:47 PM
 */
public class DescriptionHandlerTests {
    private NodeProvider _nodeProvider;
    private DescriptionHandler _handler;

    public DescriptionHandlerTests() {
        _nodeProvider = new NodeProvider();
        _handler = new DescriptionHandler();
    }

    //createTestNode

    @Test
    public void createTestNode_should_give_null_when_ingredient_has_null_description() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = null;
        JsonNode result = _handler.createTestNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createTestNode_should_give_null_when_ingredient_has_empty_description() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "";
        JsonNode result = _handler.createTestNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createTestNode_should_give_null_when_ingredient_has_whitespace_description() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "";
        JsonNode result = _handler.createTestNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createTestNode_should_give_test_node_when_ingredient_has_description() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "I am description";
        ArrayNode testNode = _nodeProvider.createArrayNode();
        NodeProvider mockNodeProvider = mock(NodeProvider.class);
        when(mockNodeProvider.createTestAddStringNode(DescriptionHandler.PATH_NAME)).thenReturn(testNode);
        _handler.setNodeProvider(mockNodeProvider);
        JsonNode result = _handler.createTestNode(ingredient);
        assertEquals(testNode, result);
    }

    //createReplaceNode

    @Test
    public void createReplaceNode_should_give_null_when_ingredient_has_null_description() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = null;
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createReplaceNode_should_give_null_when_ingredient_has_empty_description() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "";
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createReplaceNode_should_give_null_when_ingredient_has_whitespace_description() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "    ";
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertNull(result);
    }

    @Test
    public void createReplaceNode_should_give_replace_node_when_ingredient_has_description() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "I am description";
        ObjectNode testNode = _nodeProvider.createObjectNode();
        NodeProvider mockNodeProvider = mock(NodeProvider.class);
        when(mockNodeProvider.createReplaceStringNode(DescriptionHandler.PATH_NAME, ingredient.description)).thenReturn(testNode);
        _handler.setNodeProvider(mockNodeProvider);
        JsonNode result = _handler.createReplaceNode(ingredient);
        assertEquals(testNode, result);
    }

    //canHandle

    @Test
    public void canHandle_should_return_true_when_path_matches() {
        boolean result = _handler.canHandle(DescriptionHandler.PATH_NAME);
        assertTrue(result);
    }

    @Test
    public void canHandle_should_return_false_when_path_does_not_match() {
        boolean result = _handler.canHandle("/someOtherPath");
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_true_when_ingredient_has_description_and_value_does_not_exist() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "one";
        ObjectNode obj = _nodeProvider.createObjectNode();
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }

    @Test
    public void needsUpdate_should_return_true_if_node_value_is_non_string() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "one";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", 24);
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }

    @Test
    public void needsUpdate_should_return_false_when_both_are_null() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = null;
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", (String) null);
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_false_when_node_is_array() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "one";
        ArrayNode arr = _nodeProvider.createArrayNode();
        boolean result = _handler.needsUpdate(arr, ingredient);
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_false_when_existing_and_ingredient_are_same() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "one";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", "one");
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertFalse(result);
    }

    @Test
    public void needsUpdate_should_return_true_when_existing_is_null_and_ingredient_is_not() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "one";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", (String) null);
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }

    @Test
    public void needsUpdate_should_return_true_when_value_description_is_different() {
        Ingredient ingredient = new Ingredient();
        ingredient.description = "one";
        ObjectNode obj = _nodeProvider.createObjectNode();
        obj.put("value", "two");
        boolean result = _handler.needsUpdate(obj, ingredient);
        assertTrue(result);
    }
}
