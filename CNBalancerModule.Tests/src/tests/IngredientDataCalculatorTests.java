package tests;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.JsonManipulator;
import com.colonolnutty.module.shareddata.locators.IngredientStore;
import com.colonolnutty.module.shareddata.locators.RecipeStore;
import com.colonolnutty.module.shareddata.locators.StatusEffectStore;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.ItemDescriptor;
import com.colonolnutty.module.shareddata.models.Recipe;
import com.colonolnutty.module.shareddata.models.StatusEffect;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

    private ObjectMapper _mapper;
    private CNLog _logMock;
    private BalancerSettings _settings;
    private RecipeStore _recipeStoreMock;
    private IngredientStore _ingredientStoreMock;
    private StatusEffectStore _statusEffectStoreMock;
    private JsonManipulator _jsonManipulatorMock;
    private IngredientDataCalculator _calculator;
    private NodeProvider _nodeProvider;

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
        String result = _calculator.createDescription(null, new Recipe(), null);
        assertNull(result);
    }

    @Test
    public void should_give_null_description_with_empty_description() {
        String result = _calculator.createDescription("", new Recipe(), null);
        assertNull(result);
    }

    @Test
    public void should_give_null_description_with_whitespace_description() {
        String result = _calculator.createDescription("  ", new Recipe(), null);
        assertNull(result);
    }

    @Test
    public void should_give_description_containing_recipe_group() {
        HashMap<String, String> friendlyNames = new HashMap<String, String>();
        friendlyNames.put("GroupOne", "GroupOne");
        String expectedResult = "(GroupOne):-} Blah";
        String description = "Blah";
        Recipe recipe = new Recipe();
        String[] groups = new String[1];
        groups[0] = "GroupOne";
        recipe.groups = groups;
        String result = _calculator.createDescription(description, recipe, friendlyNames);
        assertEquals(expectedResult, result);
    }

    @Test
    public void should_give_description_containing_recipe_groups() {
        HashMap<String, String> friendlyNames = new HashMap<String, String>();
        friendlyNames.put("GroupOne", "GroupOne");
        friendlyNames.put("GroupThree", "GroupThree");
        friendlyNames.put("GroupFive", "GroupFive");
        String expectedResult = "(GroupOne)(GroupThree):-} Blah";
        String description = "Blah";
        Recipe recipe = new Recipe();
        String[] groups = new String[2];
        groups[0] = "GroupOne";
        groups[1] = "GroupThree";
        recipe.groups = groups;
        String result = _calculator.createDescription(description, recipe, friendlyNames);
        assertEquals(expectedResult, result);
    }

    @Test
    public void should_give_description_when_recipe_has_no_groups() {
        HashMap<String, String> friendlyNames = new HashMap<String, String>();
        String description = "Blah";
        Recipe recipe = new Recipe();
        String[] groups = new String[0];
        recipe.groups = groups;
        String result = _calculator.createDescription(description, recipe, friendlyNames);
        assertEquals(description, result);
    }

    @Test
    public void should_keep_existing_description_with_groups() {
        HashMap<String, String> friendlyNames = new HashMap<String, String>();
        friendlyNames.put("GroupOne", "GroupOne");
        friendlyNames.put("GroupTwo", "GroupTwo");
        friendlyNames.put("GroupThree", "GroupThree");
        friendlyNames.put("GroupFive", "GroupFive");
        String expectedResult = "(GroupOne)(GroupThree):-} Blah";
        String description = "(GroupTwo):-} Blah";
        Recipe recipe = new Recipe();
        String[] groups = new String[2];
        groups[0] = "GroupOne";
        groups[1] = "GroupThree";
        recipe.groups = groups;
        String result = _calculator.createDescription(description, recipe, friendlyNames);
        assertEquals(expectedResult, result);
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
