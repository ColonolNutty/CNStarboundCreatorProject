package tests.collectors;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.debug.CNLog;
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
import main.collectors.EffectsCollector;
import main.settings.BalancerSettings;
import org.junit.Test;
import tests.shared.TestBase;

import java.util.Hashtable;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Jack's Computer
 * Date: 02/08/2018
 * Time: 1:18 PM
 */
public class EffectsCollectorTests extends TestBase {
    private ObjectMapper _mapper;
    private CNLog _logMock;
    private StatusEffectStore _statusEffectStoreMock;
    private EffectsCollector _collector;
    private BalancerSettings _settings;
    private NodeProvider _nodeProvider;

    public EffectsCollectorTests() {
        JsonFactory jf = new JsonFactory();
        jf.enable(JsonParser.Feature.ALLOW_COMMENTS);
        _mapper = new ObjectMapper(jf);
        _mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        _logMock = mock(CNLog.class);
        _statusEffectStoreMock = mock(StatusEffectStore.class);
        _settings = new BalancerSettings();
        _settings.enableEffectsUpdate = true;
        _settings.excludedEffects = new String[0];
        _settings.increasePercentage = 0.0;
        _collector = new EffectsCollector(_settings, _logMock, _statusEffectStoreMock);
        _nodeProvider = new NodeProvider();
    }

    //effectsAreEqual
    @Test
    public void should_return_true_if_effects_are_equal() {
        StatusEffect oneOne = new StatusEffect("effOne", 10);
        StatusEffect oneTwo = new StatusEffect("effTwo", 12);
        StatusEffect[] arrOne = new StatusEffect[2];
        arrOne[0] = oneOne;
        arrOne[1] = oneTwo;
        ArrayNode nodeOne = createEffectsArray(arrOne);
        StatusEffect twoOne = new StatusEffect("effOne", 10);
        StatusEffect twoTwo = new StatusEffect("effTwo", 12);
        StatusEffect[] arrTwo = new StatusEffect[2];
        arrTwo[0] = twoOne;
        arrTwo[1] = twoTwo;
        ArrayNode nodeTwo = createEffectsArray(arrTwo);
        assertTrue(_collector.effectsAreEqual(nodeOne, nodeTwo));
    }

    @Test
    public void should_return_true_if_effects_are_equal_in_different_orders() {
        StatusEffect oneOne = new StatusEffect("effTwo", 12);
        StatusEffect oneTwo = new StatusEffect("effOne", 10);
        StatusEffect[] arrOne = new StatusEffect[2];
        arrOne[0] = oneOne;
        arrOne[1] = oneTwo;
        ArrayNode nodeOne = createEffectsArray(arrOne);
        StatusEffect twoOne = new StatusEffect("effOne", 10);
        StatusEffect twoTwo = new StatusEffect("effTwo", 12);
        StatusEffect[] arrTwo = new StatusEffect[2];
        arrTwo[0] = twoOne;
        arrTwo[1] = twoTwo;
        ArrayNode nodeTwo = createEffectsArray(arrTwo);
        assertTrue(_collector.effectsAreEqual(nodeOne, nodeTwo));
    }

    @Test
    public void should_return_false_if_effects_have_different_durations() {
        StatusEffect oneOne = new StatusEffect("effTwo", 13);
        StatusEffect oneTwo = new StatusEffect("effOne", 10);
        StatusEffect[] arrOne = new StatusEffect[2];
        arrOne[0] = oneOne;
        arrOne[1] = oneTwo;
        ArrayNode nodeOne = createEffectsArray(arrOne);
        StatusEffect twoOne = new StatusEffect("effOne", 10);
        StatusEffect twoTwo = new StatusEffect("effTwo", 12);
        StatusEffect[] arrTwo = new StatusEffect[2];
        arrTwo[0] = twoOne;
        arrTwo[1] = twoTwo;
        ArrayNode nodeTwo = createEffectsArray(arrTwo);
        assertFalse(_collector.effectsAreEqual(nodeOne, nodeTwo));
    }

    @Test
    public void should_return_false_if_extra_effects_are_found() {
        StatusEffect oneOne = new StatusEffect("effOne", 10);
        StatusEffect oneTwo = new StatusEffect("effTwo", 12);
        StatusEffect[] arrOne = new StatusEffect[2];
        arrOne[0] = oneOne;
        arrOne[1] = oneTwo;
        ArrayNode nodeOne = createEffectsArray(arrOne);
        StatusEffect twoOne = new StatusEffect("effOne", 10);
        StatusEffect twoTwo = new StatusEffect("effTwo", 12);
        StatusEffect twoThree = new StatusEffect("effThree", 24);
        StatusEffect[] arrTwo = new StatusEffect[3];
        arrTwo[0] = twoOne;
        arrTwo[1] = twoTwo;
        arrTwo[2] = twoThree;
        ArrayNode nodeTwo = createEffectsArray(arrTwo);
        assertFalse(_collector.effectsAreEqual(nodeOne, nodeTwo));
    }

    @Test
    public void should_return_false_if_effects_are_duplicated() {
        StatusEffect oneOne = new StatusEffect("effOne", 10);
        StatusEffect oneTwo = new StatusEffect("effOne", 10);
        StatusEffect[] arrOne = new StatusEffect[2];
        arrOne[0] = oneOne;
        arrOne[1] = oneTwo;
        ArrayNode nodeOne = createEffectsArray(arrOne);
        StatusEffect twoOne = new StatusEffect("effOne", 10);
        StatusEffect twoTwo = new StatusEffect("effTwo", 12);
        StatusEffect[] arrTwo = new StatusEffect[2];
        arrTwo[0] = twoOne;
        arrTwo[1] = twoTwo;
        ArrayNode nodeTwo = createEffectsArray(arrTwo);
        assertFalse(_collector.effectsAreEqual(nodeOne, nodeTwo));
    }
    //effectsAreEqual
    
    //checkShouldUpdate

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
        Hashtable<String, Integer> result = _collector.getEffects(ingredient, false);
        assertEquals(2, result.size());
        assertContainsKey(result, "One");
        assertEquals(5, (int)result.get("One"));
        assertContainsKey(result, "Two");
        assertEquals(10, (int)result.get("Two"));
        assertNotContainsKey(result, "Three");
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
        Hashtable<String, Integer> result = _collector.getEffects(ingredient, false);
        assertEquals(3, result.size());
        assertContainsKey(result, "One");
        assertEquals(5, (int)result.get("One"));
        assertContainsKey(result, "Two");
        assertEquals(10, (int)result.get("Two"));
        assertContainsKey(result, "Three");
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
        Hashtable<String, Integer> result = _collector.getEffects(ingredient, false);
        assertEquals(2, result.size());
        assertContainsKey(result, "One");
        assertEquals(5, (int)result.get("One"));
        assertContainsKey(result, "Two");
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
        Hashtable<String, Integer> result = _collector.getEffects(ingredient, false);
        assertEquals(1, result.size());
        assertNotContainsKey(result, "Two");
        assertContainsKey(result, "One");
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
        Hashtable<String, Integer> result = _collector.getEffects(ingredient, false);
        assertEquals(1, result.size());
        assertNotContainsKey(result, "");
        assertContainsKey(result, "One");
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
        Hashtable<String, Integer> result = _collector.getEffects(ingredient, false);
        assertEquals(1, result.size());
        assertNotContainsKey(result, "Two");
        assertContainsKey(result, "One");
        assertEquals(5, (int)result.get("One"));
    }

    @Test
    public void should_give_effects_with_one_effect() {
        StatusEffect[] statusEffects = new StatusEffect[1];
        statusEffects[0] = new StatusEffect("One", 5);
        ArrayNode effectsArr = createEffectsArray(statusEffects);
        Ingredient ingredient = new Ingredient();
        ingredient.effects = effectsArr;
        Hashtable<String, Integer> result = _collector.getEffects(ingredient, false);
        assertEquals(1, result.size());
        assertContainsKey(result, "One");
        assertEquals(5, (int)result.get("One"));
    }

    @Test
    public void should_include_foodpoison_effect_when_food_is_raw() {
        String effectOne = "EffectOne";
        String effectTwo = "foodpoison";
        StatusEffect[] statusEffects = new StatusEffect[3];
        statusEffects[0] = new StatusEffect(effectOne, 5);
        statusEffects[1] = new StatusEffect(effectTwo, 20);
        statusEffects[2] = new StatusEffect(effectTwo, 5);
        ArrayNode effectsArr = createEffectsArray(statusEffects);
        Ingredient ingredient = new Ingredient();
        ingredient.effects = effectsArr;
        Hashtable<String, Integer> result = _collector.getEffects(ingredient, true);
        assertEquals(2, result.size());
        assertContainsKey(result, effectOne);
        assertEquals(5, (int)result.get(effectOne));
        assertContainsKey(result, effectTwo);
        assertEquals(25, (int)result.get(effectTwo));
    }

    @Test
    public void should_exclude_foodpoison_effect_when_food_is_not_raw() {
        String effectOne = "effectOne";
        String effectTwo = "foodpoison";
        StatusEffect[] statusEffects = new StatusEffect[3];
        statusEffects[0] = new StatusEffect(effectOne, 5);
        statusEffects[1] = new StatusEffect(effectTwo, 20);
        statusEffects[2] = new StatusEffect(effectTwo, 5);
        ArrayNode effectsArr = createEffectsArray(statusEffects);
        Ingredient ingredient = new Ingredient();
        ingredient.effects = effectsArr;
        Hashtable<String, Integer> result = _collector.getEffects(ingredient, false);
        assertContainsKey(result, effectOne);
        assertEquals(5, (int)result.get(effectOne));
        assertNotContainsKey(result, effectTwo);
    }
    //getEffects

    @Test
    public void should_balance_ingredient_effects_for_recipe_with_two_inputs_of_different_counts() {
        Ingredient existing = new Ingredient();
        existing.itemName = "outputOne";
        existing.effects =  _nodeProvider.createArrayNode();
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
        Ingredient inOne = new Ingredient();
        inOne.effects = inEffectsOne;
        Ingredient inTwo = new Ingredient();
        inTwo.effects = inEffectsTwo;

        _settings.enableEffectsUpdate = true;
        _settings.increasePercentage = 0.5;

        Recipe recipe = new Recipe();
        recipe.output = new ItemDescriptor(existing.getName(), 1.0);

        //10+10+20+20+20 20+60 80+15 95
        //20+20+30+30+30 40+90 130+25 155
        _collector.collectData(inOne, 2.0, recipe);
        _collector.collectData(inTwo, 3.0, recipe);
        _collector.applyData(existing, recipe.output.count);

        Hashtable<String, Integer> effectsTable = assertAndConvertEffectsArray(existing.getEffects());

        assertContainsKey(effectsTable, "effectOne");
        assertContainsKey(effectsTable, "effectTwo");
        assertContainsKey(effectsTable, "effectThree");

        //10+10+5 25
        assertEquals(25, (int)effectsTable.get("effectOne"));

        //20+20+10+10+10 40+30 70+10+5 85
        assertEquals(85, (int)effectsTable.get("effectTwo"));

        //50+50+50 150+25 175
        assertEquals(175, (int)effectsTable.get("effectThree"));
    }

    @Test
    public void should_balance_ingredient_for_recipe_with_output_greater_than_one() {
        String effectOneName = "effectOne";
        String effectTwoName = "effectTwo";
        String effectThreeName = "effectThree";
        Ingredient existing = new Ingredient();
        existing.itemName = "outputOne";
        existing.effects =  _nodeProvider.createArrayNode();
        ArrayNode inEffectsOne = _nodeProvider.createArrayNode();
        ArrayNode inEffectsOneSub = _nodeProvider.createArrayNode();
        inEffectsOneSub.add(createEffect(effectOneName, 10));
        inEffectsOneSub.add(createEffect(effectTwoName, 20));
        inEffectsOne.add(inEffectsOneSub);
        ArrayNode inEffectsTwo = _nodeProvider.createArrayNode();
        ArrayNode inEffectsTwoSub = _nodeProvider.createArrayNode();
        inEffectsTwoSub.add(createEffect(effectTwoName, 10));
        inEffectsTwoSub.add(createEffect(effectThreeName, 50));
        inEffectsTwo.add(inEffectsTwoSub);
        Ingredient inOne = new Ingredient();
        inOne.effects = inEffectsOne;
        Ingredient inTwo = new Ingredient();
        inTwo.effects = inEffectsTwo;

        _settings.enableEffectsUpdate = true;
        _settings.increasePercentage = 0.5;

        Recipe recipe = new Recipe();
        recipe.output = new ItemDescriptor(existing.getName(), 10.0);

        //10+10+20+20+20 20+60 80+15 95
        //20+20+30+30+30 40+90 130+25 155
        _collector.collectData(inOne, 2.0, recipe);
        _collector.collectData(inTwo, 3.0, recipe);
        _collector.applyData(existing, recipe.output.count);

        Hashtable<String, Integer> effectsTable = assertAndConvertEffectsArray(existing.getEffects());

        assertContainsKey(effectsTable, effectOneName);
        assertContainsKey(effectsTable, effectTwoName);
        assertContainsKey(effectsTable, effectThreeName);

        //No output division
        //10+10+5/10 25/10 2.5 2
        assertEquals(25, (int)effectsTable.get(effectOneName));

        //20+20+10+10+10 40+30 70+10+5/10 85/10 8.5 8
        assertEquals(85, (int)effectsTable.get(effectTwoName));

        //50+50+50 150+25 175/10 17.5 17
        assertEquals(175, (int)effectsTable.get(effectThreeName));
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
            assertNotContainsKey(effectsTable, effectName);
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
}
