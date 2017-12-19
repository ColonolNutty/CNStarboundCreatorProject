package tests;

import com.colonolnutty.module.shareddata.FileReaderWrapper;
import com.colonolnutty.module.shareddata.models.ItemDescriptor;
import com.colonolnutty.module.shareddata.models.Recipe;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * User: Jack's Computer
 * Date: 12/16/2017
 * Time: 12:43 PM
 */
public class FileReaderWrapperTests {

    private FileReaderWrapper _wrapper;

    public FileReaderWrapperTests() {
        _wrapper = new FileReaderWrapper();
    }

    //read
    @Test
    public void can_read_from_file() throws IOException {
        String testRecipeFilePath = "testFiles\\testrecipe.recipe";
        Recipe recipe = _wrapper.read(testRecipeFilePath, Recipe.class);
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
    //read
}
