package tests;

import com.colonolnutty.module.shareddata.models.*;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * User: Jack's Computer
 * Date: 12/16/2017
 * Time: 11:40 AM
 */
public class JsonManipulatorReadTests extends JsonManipulatorTests {
    //readRecipe
    @Test
    public void can_read_recipe_from_file() throws IOException {
        String testRecipeFilePath = "testFiles\\testrecipe.recipe";
        Recipe recipe = _manipulator.readRecipe(testRecipeFilePath);
        assertNotNull(recipe);
        assertEquals(2, recipe.input.length);
        ItemDescriptor ingredOne = recipe.input[0];
        assertEquals("testIngredient1", ingredOne.item);
        assertEquals(1.0, ingredOne.count);
        ItemDescriptor ingredTwo = recipe.input[1];
        assertEquals("testIngredient2", ingredTwo.item);
        assertEquals(4.0, ingredTwo.count);
        assertEquals(2, recipe.groups.length);
        assertEquals("bakingMFM", recipe.groups[0]);
        assertEquals("banana", recipe.groups[1]);
        assertNotNull(recipe.output);
        assertEquals("testOutput", recipe.output.item);
        assertEquals(5.0, recipe.output.count);
    }
    //readRecipe

    //readIngredient
    @Test
    public void can_read_ingredient_from_file() throws IOException {
        String ingredFilePath = "testFiles\\testIngredient1.consumable";
        Ingredient ingredient = _manipulator.readIngredient(ingredFilePath);
        assertNotNull(ingredient);
        assertEquals("testIngredient1", ingredient.itemName);
        assertEquals(8.4, ingredient.price);
        assertEquals(2.0, ingredient.foodValue);
    }
    //readIngredient
}
