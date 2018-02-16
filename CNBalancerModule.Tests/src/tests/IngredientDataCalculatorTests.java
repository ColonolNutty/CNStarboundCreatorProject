package tests;

import com.colonolnutty.module.shareddata.JsonManipulator;
import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.locators.*;
import com.colonolnutty.module.shareddata.models.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.IngredientDataCalculator;
import main.settings.BalancerSettings;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Jack's Computer
 * Date: 12/14/2017
 * Time: 11:06 AM
 */
public class IngredientDataCalculatorTests {

    private CNLog _logMock;
    private BalancerSettings _settings;
    private RecipeStore _recipeStoreMock;
    private IngredientStore _ingredientStoreMock;
    private StatusEffectStore _statusEffectStoreMock;
    private JsonManipulator _jsonManipulatorMock;
    private IngredientDataCalculator _calculator;
    private NodeProvider _nodeProvider;

    public IngredientDataCalculatorTests() {

        _logMock = mock(CNLog.class);
        _settings = new BalancerSettings();
        _recipeStoreMock = mock(RecipeStore.class);
        _ingredientStoreMock = mock(IngredientStore.class);
        _statusEffectStoreMock = mock(StatusEffectStore.class);
        _jsonManipulatorMock = mock(JsonManipulator.class);
        _calculator = new IngredientDataCalculator(_logMock,
                _settings,
                _recipeStoreMock,
                _ingredientStoreMock,
                _statusEffectStoreMock,
                _jsonManipulatorMock);
        _nodeProvider = new NodeProvider();
    }

    //balanceIngredient

    @Test
    public void should_balance_ingredient_for_recipe_with_one_input() {
        Ingredient existing = createIngredient("Blah", 24.0, 29.0, "OutWhat", _nodeProvider.createArrayNode());
        Ingredient inOne = createIngredient("inOne", 10.0, 20.0, "InOneWhat", _nodeProvider.createArrayNode());

        _settings.enableEffectsUpdate = true;
        _settings.increasePercentage = 0.5;

        when(_ingredientStoreMock.getIngredient(existing.itemName)).thenReturn(existing);
        when(_ingredientStoreMock.getIngredient(inOne.itemName)).thenReturn(inOne);

        Recipe recipe = createRecipe(new ItemDescriptor(existing.itemName, 1.0), new String[0], new ItemDescriptor(inOne.itemName, 1.0));

        boolean result = _calculator.balanceIngredient(recipe);
        assertTrue(result);
        assertEquals(15.0, existing.price);
        assertEquals(30.0, existing.foodValue);
    }

    @Test
    public void should_balance_ingredient_for_recipe_with_two_inputs() {
        Ingredient existing = createIngredient("outputOne", 24.0, 29.0, "OutWhat", _nodeProvider.createArrayNode());
        Ingredient inOne = createIngredient("inOne", 10.0, 20.0, "What", _nodeProvider.createArrayNode());
        Ingredient inTwo = createIngredient("inTwo", 20.0, 30.0, "What2", _nodeProvider.createArrayNode());

        _settings.enableEffectsUpdate = true;
        _settings.increasePercentage = 0.5;

        Recipe recipe = createRecipe(
                new ItemDescriptor(existing.getName(), 1.0),
                new String[0],
                new ItemDescriptor(inOne.getName(), 1.0),
                new ItemDescriptor(inTwo.getName(), 1.0));

        boolean result = _calculator.balanceIngredient(recipe);
        assertTrue(result);
        assertEquals(45.0, existing.price);
        assertEquals(75.0, existing.foodValue);
    }

    @Test
    public void should_balance_ingredient_for_recipe_with_one_input_with_count_above_one() {
        Ingredient existing = createIngredient("outputOne", 24.0, 29.0, "OutWhat", _nodeProvider.createArrayNode());
        Ingredient inOne = createIngredient("inOne", 10.0, 20.0, "What", _nodeProvider.createArrayNode());

        _settings.enableEffectsUpdate = true;
        _settings.increasePercentage = 0.5;

        Recipe recipe = createRecipe(
                new ItemDescriptor(existing.getName(), 1.0),
                new String[0],
                new ItemDescriptor(inOne.getName(), 2.0));

        //10+10 20+5 25
        //20+20 40+10 50
        boolean result = _calculator.balanceIngredient(recipe);
        assertTrue(result);
        assertEquals(25.0, existing.price);
        assertEquals(50.0, existing.foodValue);
    }

    @Test
    public void should_balance_ingredient_for_recipe_with_two_inputs_of_different_counts() {
        Ingredient existing = createIngredient("outputOne", 24.0, 29.0, "OutWhat", _nodeProvider.createArrayNode());
        Ingredient inOne = createIngredient("inOne", 10.0, 20.0, "What", _nodeProvider.createArrayNode());
        Ingredient inTwo = createIngredient("inTwo", 20.0, 30.0, "What2", _nodeProvider.createArrayNode());

        _settings.enableEffectsUpdate = true;
        _settings.increasePercentage = 0.5;

        Recipe recipe = createRecipe(
                new ItemDescriptor(existing.getName(), 1.0),
                new String[0],
                new ItemDescriptor(inOne.getName(), 2.0),
                new ItemDescriptor(inTwo.getName(), 3.0));

        //10+10+20+20+20 20+60 80+15 95
        //20+20+30+30+30 40+90 130+25 155
        boolean result = _calculator.balanceIngredient(recipe);
        assertTrue(result);
        assertEquals(95.0, existing.price);
        assertEquals(155.0, existing.foodValue);
    }

    private Ingredient createIngredient(String itemName, Double price, Double foodValue, String description, ArrayNode effects) {
        Ingredient ingredient = new Ingredient();
        ingredient.itemName = itemName;
        ingredient.price = price;
        ingredient.foodValue = foodValue;
        ingredient.description = description;
        ingredient.effects = effects;
        when(_ingredientStoreMock.getIngredient(ingredient.getName())).thenReturn(ingredient);
        return ingredient;
    }

    private Recipe createRecipe(ItemDescriptor output, String[] groups, ItemDescriptor... inputs) {
        Recipe recipe = new Recipe();
        recipe.output = output;
        recipe.groups = groups;
        recipe.input = inputs;
        return recipe;
    }

}
