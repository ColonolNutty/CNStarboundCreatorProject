package tests.collectors;

import com.colonolnutty.module.shareddata.debug.CNLog;
import main.collectors.DescriptionCollector;
import main.settings.BalancerSettings;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * User: Jack's Computer
 * Date: 02/09/2018
 * Time: 11:33 AM
 */
public class DescriptionCollectorTests {

    private DescriptionCollector _collector;
    private BalancerSettings _settings;
    private CNLog _logMock;

    public DescriptionCollectorTests() {
        _logMock = mock(CNLog.class);
        _settings = new BalancerSettings();
        _settings.enableEffectsUpdate = true;
        _settings.excludedEffects = new String[0];
        _settings.increasePercentage = 0.0;
        _collector = new DescriptionCollector(_settings, _logMock);
    }

    //createDescription
    @Test
    public void should_give_null_description_with_null_description() {
        String result = _collector.createDescription(null, null, null);
        assertNull(result);
    }

    @Test
    public void should_give_null_description_with_empty_description() {
        String result = _collector.createDescription("", null, null);
        assertNull(result);
    }

    @Test
    public void should_give_null_description_with_whitespace_description() {
        String result = _collector.createDescription("  ", null, null);
        assertNull(result);
    }

    @Test
    public void should_give_description_containing_recipe_group() {
        HashMap<String, String> friendlyNames = new HashMap<String, String>();
        friendlyNames.put("GroupOne", "GroupOne");
        String expectedResult = "(GroupOne):-} Blah";
        String description = "Blah";
        String[] groups = new String[1];
        groups[0] = "GroupOne";
        String result = _collector.createDescription(description, groups, friendlyNames);
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
        String[] groups = new String[2];
        groups[0] = "GroupOne";
        groups[1] = "GroupThree";
        String result = _collector.createDescription(description, groups, friendlyNames);
        assertEquals(expectedResult, result);
    }

    @Test
    public void should_give_description_when_recipe_has_no_groups() {
        HashMap<String, String> friendlyNames = new HashMap<String, String>();
        String description = "Blah";
        String[] groups = new String[0];
        String result = _collector.createDescription(description, groups, friendlyNames);
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
        String[] groups = new String[2];
        groups[0] = "GroupOne";
        groups[1] = "GroupThree";
        String result = _collector.createDescription(description, groups, friendlyNames);
        assertEquals(expectedResult, result);
    }
    //createDescription

    //createMethodText
    @Test
    public void should_create_method_text_with_null_groups() {
        String result = _collector.createMethodText(null);
        assertEquals("", result);
    }

    @Test
    public void should_create_method_text_with_no_groups() {
        String result = _collector.createMethodText(new ArrayList<String>());
        assertEquals("", result);
    }

    @Test
    public void should_create_method_text_with_one_groups() {
        ArrayList<String> groups = new ArrayList<String>();
        groups.add("One");
        String result = _collector.createMethodText(groups);
        assertEquals("(One)", result);
    }

    @Test
    public void should_create_method_text_with_two_groups() {
        ArrayList<String> groups = new ArrayList<String>();
        groups.add("One");
        groups.add("Two");
        String result = _collector.createMethodText(groups);
        assertEquals("(One)(Two)", result);
    }
    //createMethodText

    //getRecipeGroupNames

    @Test
    public void should_return_empty_array_with_null_groups() {
        HashMap<String, String> friendlyGroupNames = createFriendlyGroupNames();

        ArrayList<String> result = _collector.getRecipeGroupNames(null, friendlyGroupNames);
        assertEquals(0, result.size());
    }

    @Test
    public void should_return_empty_array_with_no_groups() {
        HashMap<String, String> friendlyGroupNames = createFriendlyGroupNames();
        String[] recipeGroups = new String[0];

        ArrayList<String> result = _collector.getRecipeGroupNames(recipeGroups, friendlyGroupNames);
        assertEquals(0, result.size());
    }

    @Test
    public void should_return_the_names_of_recipe_groups_with_one_group() {
        HashMap<String, String> friendlyGroupNames = createFriendlyGroupNames();
        String[] recipeGroups = new String[1];
        recipeGroups[0] = "one";

        ArrayList<String> result = _collector.getRecipeGroupNames(recipeGroups, friendlyGroupNames);
        assertEquals(1, result.size());
        assertTrue(result.contains(friendlyGroupNames.get("one")));
    }

    @Test
    public void should_return_the_names_of_recipe_groups_with_two_groups() {
        HashMap<String, String> friendlyGroupNames = createFriendlyGroupNames();
        String[] recipeGroups = new String[2];
        recipeGroups[0] = "one";
        recipeGroups[1] = "two";

        ArrayList<String> result = _collector.getRecipeGroupNames(recipeGroups, friendlyGroupNames);
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

        ArrayList<String> result = _collector.getRecipeGroupNames(recipeGroups, friendlyGroupNames);
        assertEquals(2, result.size());
        assertTrue(result.contains(friendlyGroupNames.get("one")));
        assertTrue(result.contains(friendlyGroupNames.get("two")));
    }
    //getRecipeGroupNames

    private HashMap<String, String> createFriendlyGroupNames() {
        HashMap<String, String> friendlyGroupNames = new HashMap<String, String>();
        friendlyGroupNames.put("one", "One");
        friendlyGroupNames.put("two", "Two");
        return friendlyGroupNames;
    }
}
