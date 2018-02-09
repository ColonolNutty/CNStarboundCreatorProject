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
        Ingredient existing = new Ingredient();
        existing.description = "Blah";
        existing.price = 24.0;
        existing.foodValue = 29.0;
        existing.itemName = "What";
        existing.effects = _nodeProvider.createArrayNode();

        Ingredient inOne = new Ingredient();
        inOne.itemName = "inOne";
        inOne.price = 10.0;
        inOne.foodValue = 20.0;
        inOne.description = "What";
        inOne.effects = _nodeProvider.createArrayNode();

        _settings.enableEffectsUpdate = true;
        _settings.increasePercentage = 0.5;

        when(_ingredientStoreMock.getIngredient(existing.itemName)).thenReturn(existing);
        when(_ingredientStoreMock.getIngredient(inOne.itemName)).thenReturn(inOne);

        Recipe recipe = createRecipe(new ItemDescriptor(existing.itemName, 1.0), new String[0], new ItemDescriptor(inOne.itemName, 1.0));

        Ingredient result = _calculator.balanceIngredient(recipe);
        assertNotNull(result);
        assertEquals(15.0, result.price);
        assertEquals(30.0, result.foodValue);
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

        Ingredient result = _calculator.balanceIngredient(recipe);
        assertNotNull(result);
        assertEquals(45.0, result.price);
        assertEquals(75.0, result.foodValue);
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
        Ingredient result = _calculator.balanceIngredient(recipe);
        assertNotNull(result);
        assertEquals(25.0, result.price);
        assertEquals(50.0, result.foodValue);
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
        Ingredient result = _calculator.balanceIngredient(recipe);
        assertNotNull(result);
        assertEquals(95.0, result.price);
        assertEquals(155.0, result.foodValue);
    }

    @Test
    public void should_balance_ingredient_effects_for_recipe_with_two_inputs_of_different_counts() {
        Ingredient existing = createIngredient("outputOne", 24.0, 29.0, "OutWhat", _nodeProvider.createArrayNode());
        ArrayNode inEffectsOne = _nodeProvider.createArrayNode();
        ArrayNode inEffectsOneSub = _nodeProvider.createArrayNode();
        inEffectsOneSub.add(createEffect("effectOne", 10));
        inEffectsOneSub.add(createEffect("effectTwo", 20));
        inEffectsOne.add(inEffectsOneSub);
        ArrayNode inEffectsTwo = _nodeProvider.createArrayNode();
        ArrayNode inEffectsTwoSub = _nodeProvider.createArrayNode();
        inEffectsTwoSub.add(createEffect("effectTwo", 10));
        inEffectsTwoSub.add(createEffect("effectThree", 50));
        inEffectsTwo.add(inEffectsTwoSub);
        Ingredient inOne = createIngredient("inOne", 10.0, 20.0, "What", inEffectsOne);
        Ingredient inTwo = createIngredient("inTwo", 20.0, 30.0, "What2", inEffectsTwo);

        _settings.enableEffectsUpdate = true;
        _settings.increasePercentage = 0.5;

        Recipe recipe = createRecipe(
                new ItemDescriptor(existing.getName(), 1.0),
                new String[0],
                new ItemDescriptor(inOne.getName(), 2.0),
                new ItemDescriptor(inTwo.getName(), 3.0));

        //10+10+20+20+20 20+60 80+15 95
        //20+20+30+30+30 40+90 130+25 155
        Ingredient result = _calculator.balanceIngredient(recipe);
        assertNotNull(result);
        Hashtable<String, Integer> effectsTable = assertAndConvertEffectsArray(result.effects);

        assertTrue(effectsTable.containsKey("effectOne"));
        assertTrue(effectsTable.containsKey("effectTwo"));
        assertTrue(effectsTable.containsKey("effectThree"));

        //10+10+5 25
        assertEquals(25, (int)effectsTable.get("effectOne"));

        //20+20+10+10+10 40+30 70+10+5 85
        assertEquals(85, (int)effectsTable.get("effectTwo"));

        //50+50+50 150+25 175
        assertEquals(175, (int)effectsTable.get("effectThree"));
    }

    @Test
    public void should_balance_ingredient_for_recipe_with_output_greater_than_one() {
        Ingredient existing = createIngredient("outputOne", 24.0, 29.0, "OutWhat", _nodeProvider.createArrayNode());
        ArrayNode inEffectsOne = _nodeProvider.createArrayNode();
        ArrayNode inEffectsOneSub = _nodeProvider.createArrayNode();
        inEffectsOneSub.add(createEffect("effectOne", 10));
        inEffectsOneSub.add(createEffect("effectTwo", 20));
        inEffectsOne.add(inEffectsOneSub);
        ArrayNode inEffectsTwo = _nodeProvider.createArrayNode();
        ArrayNode inEffectsTwoSub = _nodeProvider.createArrayNode();
        inEffectsTwoSub.add(createEffect("effectTwo", 10));
        inEffectsTwoSub.add(createEffect("effectThree", 50));
        inEffectsTwo.add(inEffectsTwoSub);
        Ingredient inOne = createIngredient("inOne", 10.0, 20.0, "What", inEffectsOne);
        Ingredient inTwo = createIngredient("inTwo", 20.0, 30.0, "What2", inEffectsTwo);

        _settings.enableEffectsUpdate = true;
        _settings.increasePercentage = 0.5;

        Recipe recipe = createRecipe(
                new ItemDescriptor(existing.getName(), 10.0),
                new String[0],
                new ItemDescriptor(inOne.getName(), 2.0),
                new ItemDescriptor(inTwo.getName(), 3.0));

        //10+10+20+20+20 20+60 80+15 95
        //20+20+30+30+30 40+90 130+25 155
        Ingredient result = _calculator.balanceIngredient(recipe);
        assertNotNull(result);
        assertEquals(9.5, result.price);
        assertEquals(15.5, result.foodValue);

        Hashtable<String, Integer> effectsTable = assertAndConvertEffectsArray(result.effects);

        assertTrue(effectsTable.containsKey("effectOne"));
        assertTrue(effectsTable.containsKey("effectTwo"));
        assertTrue(effectsTable.containsKey("effectThree"));

        //10+10+5/10 25/10 2.5 2
        assertEquals(2, (int)effectsTable.get("effectOne"));

        //20+20+10+10+10 40+30 70+10+5/10 85/10 8.5 8
        assertEquals(8, (int)effectsTable.get("effectTwo"));

        //50+50+50 150+25 175/10 17.5 17
        assertEquals(17, (int)effectsTable.get("effectThree"));
    }

    private Hashtable<String, Integer> assertAndConvertEffectsArray(ArrayNode resultEffects) {
        assertNotNull(resultEffects);
        assertTrue(resultEffects.size() > 0);
        Hashtable<String, Integer> effectsTable = new Hashtable<String, Integer>();
        ArrayNode effects = (ArrayNode) resultEffects.get(0);
        for(JsonNode effect : effects) {
            assertTrue(effect.has("effect"));
            assertTrue(effect.has("duration"));
            JsonNode nameNode = effect.get("effect");
            assertTrue(nameNode.isTextual());
            String effectName = nameNode.asText();
            assertFalse(effectsTable.containsKey(effectName));
            JsonNode durationNode = effect.get("duration");
            assertTrue(durationNode.isInt());
            effectsTable.put(effectName, durationNode.asInt());
        }
        return effectsTable;
    }

    private ObjectNode createEffect(String name, int duration) {
        ObjectNode effect = _nodeProvider.createObjectNode();
        effect.put("effect", name);
        effect.put("duration", duration);
        return effect;
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
