package tests;

import com.colonolnutty.module.shareddata.CNLog;
import com.colonolnutty.module.shareddata.JsonManipulator;
import com.colonolnutty.module.shareddata.locators.IngredientStore;
import com.colonolnutty.module.shareddata.locators.RecipeStore;
import com.colonolnutty.module.shareddata.locators.StatusEffectStore;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.Recipe;
import com.colonolnutty.module.shareddata.models.StatusEffect;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.IngredientDataCalculator;
import main.settings.BalancerSettings;
import org.junit.Test;

import java.io.IOException;
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

    private ObjectMapper _mapper;
    private CNLog _logMock;
    private BalancerSettings _settings;
    private RecipeStore _recipeStoreMock;
    private IngredientStore _ingredientStoreMock;
    private StatusEffectStore _statusEffectStoreMock;
    private JsonManipulator _jsonManipulatorMock;
    private IngredientDataCalculator _calculator;

    public IngredientDataCalculatorTests() {
        JsonFactory jf = new JsonFactory();
        jf.enable(JsonParser.Feature.ALLOW_COMMENTS);
        _mapper = new ObjectMapper(jf);
        _mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

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
    }



    //getEffects
    @Test
    public void should_exclude_effects_with_just_a_name_and_no_default_duration() {
        StatusEffect[] statusEffects = new StatusEffect[2];
        statusEffects[0] = new StatusEffect("One", 5);
        statusEffects[1] = new StatusEffect("Two", 10);
        ArrayNode effectsArr = createEffectsArray(statusEffects);

        ArrayNode subArr = (ArrayNode)effectsArr.get(0);
        subArr.add("Three");

        Ingredient ingredient = new Ingredient();
        ingredient.effects = effectsArr;
        Hashtable<String, Integer> result = _calculator.getEffects(ingredient, false);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("One"));
        assertEquals(5, (int)result.get("One"));
        assertTrue(result.containsKey("Two"));
        assertEquals(10, (int)result.get("Two"));
        assertFalse(result.containsKey("Three"));
    }

    @Test
    public void should_handle_effects_with_just_a_name() {
        StatusEffect[] statusEffects = new StatusEffect[2];
        statusEffects[0] = new StatusEffect("One", 5);
        statusEffects[1] = new StatusEffect("Two", 10);
        ArrayNode effectsArr = createEffectsArray(statusEffects);

        ArrayNode subArr = (ArrayNode)effectsArr.get(0);
        subArr.add("Three");
        when(_statusEffectStoreMock.getDefaultStatusEffectDuration("Three")).thenReturn(200);

        Ingredient ingredient = new Ingredient();
        ingredient.effects = effectsArr;
        Hashtable<String, Integer> result = _calculator.getEffects(ingredient, false);
        assertEquals(3, result.size());
        assertTrue(result.containsKey("One"));
        assertEquals(5, (int)result.get("One"));
        assertTrue(result.containsKey("Two"));
        assertEquals(10, (int)result.get("Two"));
        assertTrue(result.containsKey("Three"));
        assertEquals(200, (int)result.get("Three"));
    }

    @Test
    public void should_add_durations_together_for_duplicate_effects() {
        StatusEffect[] statusEffects = new StatusEffect[3];
        statusEffects[0] = new StatusEffect("One", 5);
        statusEffects[1] = new StatusEffect("Two", 20);
        statusEffects[2] = new StatusEffect("Two", 5);
        ArrayNode effectsArr = createEffectsArray(statusEffects);
        Ingredient ingredient = new Ingredient();
        ingredient.effects = effectsArr;
        Hashtable<String, Integer> result = _calculator.getEffects(ingredient, false);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("One"));
        assertEquals(5, (int)result.get("One"));
        assertTrue(result.containsKey("Two"));
        assertEquals(25, (int)result.get("Two"));
    }

    @Test
    public void should_exclude_effects_with_negative_duration() {
        StatusEffect[] statusEffects = new StatusEffect[2];
        statusEffects[0] = new StatusEffect("One", 5);
        statusEffects[1] = new StatusEffect("Two", -20);
        ArrayNode effectsArr = createEffectsArray(statusEffects);
        Ingredient ingredient = new Ingredient();
        ingredient.effects = effectsArr;
        Hashtable<String, Integer> result = _calculator.getEffects(ingredient, false);
        assertEquals(1, result.size());
        assertFalse(result.containsKey("Two"));
        assertTrue(result.containsKey("One"));
        assertEquals(5, (int)result.get("One"));
    }

    @Test
    public void should_exclude_effects_with_no_name() {
        StatusEffect[] statusEffects = new StatusEffect[2];
        statusEffects[0] = new StatusEffect("One", 5);
        statusEffects[1] = new StatusEffect("", 20);
        ArrayNode effectsArr = createEffectsArray(statusEffects);
        Ingredient ingredient = new Ingredient();
        ingredient.effects = effectsArr;
        Hashtable<String, Integer> result = _calculator.getEffects(ingredient, false);
        assertEquals(1, result.size());
        assertFalse(result.containsKey(""));
        assertTrue(result.containsKey("One"));
        assertEquals(5, (int)result.get("One"));
    }

    @Test
    public void should_exclude_effects_with_zero_duration() {
        StatusEffect[] statusEffects = new StatusEffect[2];
        statusEffects[0] = new StatusEffect("One", 5);
        statusEffects[1] = new StatusEffect("Two", 0);
        ArrayNode effectsArr = createEffectsArray(statusEffects);
        Ingredient ingredient = new Ingredient();
        ingredient.effects = effectsArr;
        Hashtable<String, Integer> result = _calculator.getEffects(ingredient, false);
        assertEquals(1, result.size());
        assertFalse(result.containsKey("Two"));
        assertTrue(result.containsKey("One"));
        assertEquals(5, (int)result.get("One"));
    }

    @Test
    public void should_give_effects_with_one_effect() {
        StatusEffect[] statusEffects = new StatusEffect[1];
        statusEffects[0] = new StatusEffect("One", 5);
        ArrayNode effectsArr = createEffectsArray(statusEffects);
        Ingredient ingredient = new Ingredient();
        ingredient.effects = effectsArr;
        Hashtable<String, Integer> result = _calculator.getEffects(ingredient, false);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("One"));
        assertEquals(5, (int)result.get("One"));
    }
    //getEffects

    //createDescription
    @Test
    public void should_give_null_description_with_null_description() {
        String result = _calculator.createDescription(null, new Recipe());
        assertNull(result);
    }

    @Test
    public void should_give_null_description_with_empty_description() {
        String result = _calculator.createDescription("", new Recipe());
        assertNull(result);
    }

    @Test
    public void should_give_null_description_with_whitespace_description() {
        String result = _calculator.createDescription("  ", new Recipe());
        assertNull(result);
    }
    //createDescription

    //createMethodText
    @Test
    public void should_create_method_text_with_null_groups() {
        String result = _calculator.createMethodText(null);
        assertEquals("", result);
    }

    @Test
    public void should_create_method_text_with_no_groups() {
        String result = _calculator.createMethodText(new ArrayList<String>());
        assertEquals("", result);
    }

    @Test
    public void should_create_method_text_with_one_groups() {
        ArrayList<String> groups = new ArrayList<String>();
        groups.add("One");
        String result = _calculator.createMethodText(groups);
        assertEquals("(One)", result);
    }

    @Test
    public void should_create_method_text_with_two_groups() {
        ArrayList<String> groups = new ArrayList<String>();
        groups.add("One");
        groups.add("Two");
        String result = _calculator.createMethodText(groups);
        assertEquals("(One)(Two)", result);
    }
    //createMethodText

    //getRecipeGroupNames
    @Test
    public void should_return_empty_array_with_null_recipe() {
        HashMap<String, String> friendlyGroupNames = createFriendlyGroupNames();

        ArrayList<String> result = _calculator.getRecipeGroupNames(null, friendlyGroupNames);
        assertEquals(0, result.size());
    }

    @Test
    public void should_return_empty_array_with_null_groups() {
        HashMap<String, String> friendlyGroupNames = createFriendlyGroupNames();
        Recipe recipe = new Recipe();
        recipe.groups = null;

        ArrayList<String> result = _calculator.getRecipeGroupNames(recipe, friendlyGroupNames);
        assertEquals(0, result.size());
    }

    @Test
    public void should_return_empty_array_with_no_groups() {
        HashMap<String, String> friendlyGroupNames = createFriendlyGroupNames();
        Recipe recipe = new Recipe();
        recipe.groups = new String[0];

        ArrayList<String> result = _calculator.getRecipeGroupNames(recipe, friendlyGroupNames);
        assertEquals(0, result.size());
    }

    @Test
    public void should_return_the_names_of_recipe_groups_with_one_group() {
        HashMap<String, String> friendlyGroupNames = createFriendlyGroupNames();
        String[] recipeGroups = new String[1];
        recipeGroups[0] = "one";
        Recipe recipe = new Recipe();
        recipe.groups = recipeGroups;

        ArrayList<String> result = _calculator.getRecipeGroupNames(recipe, friendlyGroupNames);
        assertEquals(1, result.size());
        assertTrue(result.contains(friendlyGroupNames.get("one")));
    }

    @Test
    public void should_return_the_names_of_recipe_groups_with_two_groups() {
        HashMap<String, String> friendlyGroupNames = createFriendlyGroupNames();
        String[] recipeGroups = new String[2];
        recipeGroups[0] = "one";
        recipeGroups[1] = "two";
        Recipe recipe = new Recipe();
        recipe.groups = recipeGroups;

        ArrayList<String> result = _calculator.getRecipeGroupNames(recipe, friendlyGroupNames);
        assertEquals(2, result.size());
        assertTrue(result.contains(friendlyGroupNames.get("one")));
        assertTrue(result.contains(friendlyGroupNames.get("two")));
    }

    @Test
    public void should_return_only_groups_with_friendly_names() {
        HashMap<String, String> friendlyGroupNames = createFriendlyGroupNames();
        String[] recipeGroups = new String[3];
        recipeGroups[0] = "one";
        recipeGroups[1] = "three";
        recipeGroups[2] = "two";
        Recipe recipe = new Recipe();
        recipe.groups = recipeGroups;

        ArrayList<String> result = _calculator.getRecipeGroupNames(recipe, friendlyGroupNames);
        assertEquals(2, result.size());
        assertTrue(result.contains(friendlyGroupNames.get("one")));
        assertTrue(result.contains(friendlyGroupNames.get("two")));
    }
    //getRecipeGroupNames

    //calculateValue
    @Test
    public void should_return_zero_when_increasePercentage_is_null() {
        Double count = 5.0;
        Double value = 1.0;
        Double increasePercentage = null;
        Double expected = 0.0;
        Double result = _calculator.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_return_zero_when_value_is_null() {
        Double count = 5.0;
        Double value = null;
        Double increasePercentage = 0.0;
        Double expected = 0.0;
        Double result = _calculator.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_return_zero_when_count_is_null() {
        Double count = null;
        Double value = 25.0;
        Double increasePercentage = 0.0;
        Double expected = 0.0;
        Double result = _calculator.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_use_count_of_one_when_count_less_than_zero() {
        Double count = -1.0;
        Double value = 25.0;
        Double increasePercentage = 0.0;
        Double expected = 25.0;
        Double result = _calculator.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_use_count_of_one_when_count_is_zero() {
        Double count = 0.0;
        Double value = 25.0;
        Double increasePercentage = 0.0;
        Double expected = 25.0;
        Double result = _calculator.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_use_value_of_zero_when_value_less_than_zero() {
        Double count = 5.0;
        Double value = -1.0;
        Double increasePercentage = 0.0;
        Double expected = 0.0;
        Double result = _calculator.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_use_value_of_zero_when_value_is_zero() {
        Double count = 1.0;
        Double value = 0.0;
        Double increasePercentage = 0.0;
        Double expected = 0.0;
        Double result = _calculator.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }

    @Test
    public void should_calculate_with_increase_percentage() {
        Double count = 10.0;
        Double value = 5.0;
        Double increasePercentage = 0.5;
        Double expected = 52.5;
        Double result = _calculator.calculateValue(count, value, increasePercentage);
        assertEquals(expected, result);
    }
    //calculateValue

    private HashMap<String, String> createFriendlyGroupNames() {
        HashMap<String, String> friendlyGroupNames = new HashMap<String, String>();
        friendlyGroupNames.put("one", "One");
        friendlyGroupNames.put("two", "Two");
        return friendlyGroupNames;
    }

    private ArrayNode createEffectsArray(StatusEffect[] effects) {
        ArrayNode subNode = _mapper.createArrayNode();
        for(int i = 0; i < effects.length; i++) {
            StatusEffect effect = effects[i];
            when(_statusEffectStoreMock.getDefaultStatusEffectDuration(effect.name)).thenReturn(effect.defaultDuration);
            ObjectNode effectNode = _mapper.createObjectNode();
            effectNode.put("effect", effect.name);
            effectNode.put("duration", effect.defaultDuration);
            subNode.add(effectNode);
        }
        ArrayNode node = _mapper.createArrayNode();
        node.add(subNode);
        return node;
    }
}
