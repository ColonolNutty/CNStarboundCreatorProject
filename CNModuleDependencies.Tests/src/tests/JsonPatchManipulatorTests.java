package tests;

import com.colonolnutty.module.shareddata.*;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;

/**
 * User: Jack's Computer
 * Date: 12/23/2017
 * Time: 10:45 AM
 */
public class JsonPatchManipulatorTests {
    protected BaseSettings _settings;
    protected tests.fakeclasses.FakeFileWriter _fileWriter;
    protected IFileReader _fileReader;
    protected NodeProvider _nodeProvider;
    protected IPrettyPrinter _prettyPrinter;
    protected JsonPatchManipulator _manipulator;

    public JsonPatchManipulatorTests() {
        CNLog log = mock(CNLog.class);
        String[] propToUpd = new String[4];
        propToUpd[0] = "foodValue";
        propToUpd[1] = "price";
        propToUpd[2] = "effects";
        propToUpd[3] = "description";
        _settings = new BaseSettings();
        _settings.propertiesToUpdate = propToUpd;

        _manipulator = new JsonPatchManipulator(log, _settings);

        _fileWriter = new tests.fakeclasses.FakeFileWriter();
        _prettyPrinter = new tests.fakeclasses.FakePrettyPrinter();
        _fileReader = mock(IFileReader.class);
        _nodeProvider = new NodeProvider();

        _manipulator.setFileWriter(_fileWriter);
        _manipulator.setFileReader(_fileReader);
        _manipulator.setNodeProvider(_nodeProvider);
        _manipulator.setPrettyPrinter(_prettyPrinter);
    }

    //sortPatchNodes

    @Test
    public void sortPatchNodes_should_return_empty_when_patch_is_null() {
        JsonPatchManipulator.PatchNodes result = _manipulator.sortPatchNodes(null);
        assertNotNull(result);
        assertNotNull(result.TestNodes);
        assertNotNull(result.NonTestNodes);
        assertEquals(0, result.TestNodes.size());
        assertEquals(0, result.NonTestNodes.size());
    }

    @Test
    public void sortPatchNodes_should_return_empty_when_patch_is_empty() {
        ArrayNode patchNodes = _nodeProvider.createArrayNode();
        JsonPatchManipulator.PatchNodes result = _manipulator.sortPatchNodes(patchNodes);
        assertNotNull(result);
        assertNotNull(result.TestNodes);
        assertNotNull(result.NonTestNodes);
        assertEquals(0, result.TestNodes.size());
        assertEquals(0, result.NonTestNodes.size());
    }

    @Test
    public void sortPatchNodes_should_return_empty_when_patch_is_not_an_array() {
        ObjectNode patchNodes = _nodeProvider.createObjectNode();
        patchNodes.put("name", "1");
        JsonPatchManipulator.PatchNodes result = _manipulator.sortPatchNodes(patchNodes);
        assertNotNull(result);
        assertNotNull(result.TestNodes);
        assertNotNull(result.NonTestNodes);
        assertEquals(0, result.TestNodes.size());
        assertEquals(0, result.NonTestNodes.size());
    }

    /**
     * [
     *   [
     *     Test
     *   ],
     *   [
     *     [
     *       NonTest
     *     ],
     *     [
     *       NonTest
     *     ],
     *     [
     *       [
     *         NonTest
     *       ]
     *     ]
     *   ]
     * ]
     */
    @Test
    public void sortPatchNodes_should_flatten_non_test_nodes() {
        ArrayNode patchNodes = _nodeProvider.createArrayNode();
        ArrayNode testNode = _nodeProvider.createTestAddStringNode("/onetwothree");
        patchNodes.add(testNode);
        ArrayNode nonTestArr = _nodeProvider.createArrayNode();
        ArrayNode nonTestNode = _nodeProvider.createArrayNode();
        ObjectNode nonTestReplaceNode = _nodeProvider.createReplaceStringNode("/onetwothree", "\"banana\"");
        nonTestNode.add(nonTestReplaceNode);
        nonTestArr.add(nonTestNode);

        ArrayNode nonTestNodeTwo = _nodeProvider.createArrayNode();
        ObjectNode nonTestReplaceNodeTwo = _nodeProvider.createReplaceStringNode("/onetwothreefour", "\"banana2\"");
        nonTestNodeTwo.add(nonTestReplaceNodeTwo);
        nonTestArr.add(nonTestNodeTwo);

        ArrayNode nonTestNodeThreeArr = _nodeProvider.createArrayNode();
        ArrayNode nonTestNodeThree = _nodeProvider.createArrayNode();
        ObjectNode nonTestReplaceNodeThree = _nodeProvider.createReplaceStringNode("/onethree", "\"banana3\"");
        nonTestNodeThree.add(nonTestReplaceNodeThree);
        nonTestNodeThreeArr.add(nonTestNodeThree);
        nonTestArr.add(nonTestNodeThreeArr);

        patchNodes.add(nonTestArr);
        JsonPatchManipulator.PatchNodes result = _manipulator.sortPatchNodes(patchNodes);
        assertNotNull(result);
        assertNotNull(result.TestNodes);
        assertNotNull(result.NonTestNodes);
        assertEquals(1, result.TestNodes.size());
        assertEquals(3, result.NonTestNodes.size());
        assertTrue(result.TestNodes.contains(testNode));
        assertTrue(result.NonTestNodes.contains(nonTestReplaceNode));
        assertTrue(result.NonTestNodes.contains(nonTestReplaceNodeTwo));
        assertTrue(result.NonTestNodes.contains(nonTestReplaceNodeThree));
    }

    /**
     * [
     *   [
     *     Test
     *   ],
     *   [
     *     NonTest
     *   ]
     * ]
     */
    @Test
    public void sortPatchNodes_should_split_nodes() {
        ArrayNode patchNodes = _nodeProvider.createArrayNode();
        ArrayNode testNode = _nodeProvider.createTestAddStringNode("/onetwothree");
        patchNodes.add(testNode);
        ArrayNode nonTestNode = _nodeProvider.createArrayNode();
        ObjectNode nonTestReplaceNode = _nodeProvider.createReplaceStringNode("/onetwothree", "\"banana\"");
        nonTestNode.add(nonTestReplaceNode);
        patchNodes.add(nonTestNode);
        JsonPatchManipulator.PatchNodes result = _manipulator.sortPatchNodes(patchNodes);
        assertNotNull(result);
        assertNotNull(result.TestNodes);
        assertNotNull(result.NonTestNodes);
        assertEquals(1, result.TestNodes.size());
        assertEquals(1, result.NonTestNodes.size());
        assertTrue(result.TestNodes.contains(testNode));
        assertTrue(result.NonTestNodes.contains(nonTestReplaceNode));
    }

    //sortPatchNodes
}
