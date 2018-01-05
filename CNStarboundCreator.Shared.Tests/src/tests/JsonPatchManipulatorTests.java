package tests;

import com.colonolnutty.module.shareddata.JsonPatchManipulator;
import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.io.IFileReader;
import com.colonolnutty.module.shareddata.models.NodeAvailability;
import com.colonolnutty.module.shareddata.models.PatchNodes;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.colonolnutty.module.shareddata.prettyprinters.IPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import tests.fakes.FakeFileWriter;
import tests.fakes.FakePrettyPrinter;

import java.util.ArrayList;
import java.util.Hashtable;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;

/**
 * User: Jack's Computer
 * Date: 12/23/2017
 * Time: 10:45 AM
 */
public class JsonPatchManipulatorTests {
    protected BaseSettings _settings;
    protected FakeFileWriter _fileWriter;
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

        _fileWriter = new FakeFileWriter();
        _prettyPrinter = new FakePrettyPrinter();
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
        PatchNodes result = _manipulator.sortPatchNodes(null);
        assertNotNull(result);
        assertNotNull(result.TestNodes);
        assertNotNull(result.NonTestNodes);
        assertEquals(0, result.TestNodes.size());
        assertEquals(0, result.NonTestNodes.size());
    }

    @Test
    public void sortPatchNodes_should_return_empty_when_patch_is_empty() {
        ArrayNode patchNodes = _nodeProvider.createArrayNode();
        PatchNodes result = _manipulator.sortPatchNodes(patchNodes);
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
        PatchNodes result = _manipulator.sortPatchNodes(patchNodes);
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
        PatchNodes result = _manipulator.sortPatchNodes(patchNodes);
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
        PatchNodes result = _manipulator.sortPatchNodes(patchNodes);
        assertNotNull(result);
        assertNotNull(result.TestNodes);
        assertNotNull(result.NonTestNodes);
        assertEquals(1, result.TestNodes.size());
        assertEquals(1, result.NonTestNodes.size());
        assertTrue(result.TestNodes.contains(testNode));
        assertTrue(result.NonTestNodes.contains(nonTestReplaceNode));
    }

    @Test
    public void getNodeAvailability_should_group_test_nodes_and_add_array_nodes_properly() {
        String path = "/interactions";
        ArrayNode firstTestNode = _nodeProvider.createTestAddArrayNode(path);
        ObjectNode firstAddNode = _nodeProvider.createAddStringNode(path + "/-", "valueOne");
        ArrayList<JsonNode> testNodes = new ArrayList<JsonNode>();
        testNodes.add(firstTestNode);
        ArrayList<JsonNode> nonTestNodes = new ArrayList<JsonNode>();
        nonTestNodes.add(firstAddNode);
        PatchNodes patchNodes = new PatchNodes(testNodes, nonTestNodes);
        Hashtable<String, NodeAvailability> result = _manipulator.getNodeAvailability(patchNodes);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(path));
        NodeAvailability nodeAv = result.get(path);
        assertTrue(nodeAv.hasNonTestNodes());
        assertEquals(1, nodeAv.NonTestNodes.size());
        assertEquals(firstAddNode, nodeAv.NonTestNodes.get(0));
        assertEquals(firstTestNode, nodeAv.TestNode);
    }

    @Test
    public void getNodeAvailability_should_group_test_nodes_and_add_nodes_properly() {
        String path = "/pathOne";
        String pathTwo = "/pathTwo";
        ArrayNode firstTestNode = _nodeProvider.createTestAddArrayNode(path);
        ArrayNode secondTestNode = _nodeProvider.createTestAddStringNode(pathTwo);
        ObjectNode firstAddNode = _nodeProvider.createAddStringNode(path, "valueOne");
        ObjectNode secondAddNode = _nodeProvider.createAddStringNode(pathTwo, "valueTwo");
        ArrayList<JsonNode> testNodes = new ArrayList<JsonNode>();
        testNodes.add(firstTestNode);
        testNodes.add(secondTestNode);
        ArrayList<JsonNode> nonTestNodes = new ArrayList<JsonNode>();
        nonTestNodes.add(firstAddNode);
        nonTestNodes.add(secondAddNode);
        PatchNodes patchNodes = new PatchNodes(testNodes, nonTestNodes);
        Hashtable<String, NodeAvailability> result = _manipulator.getNodeAvailability(patchNodes);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(path));
        assertTrue(result.containsKey(pathTwo));
        NodeAvailability nodeAv = result.get(path);
        assertTrue(nodeAv.hasNonTestNodes());
        assertEquals(1, nodeAv.NonTestNodes.size());
        assertEquals(firstAddNode, nodeAv.NonTestNodes.get(0));
        assertEquals(firstTestNode, nodeAv.TestNode);
        NodeAvailability nodeAvTwo = result.get(pathTwo);
        assertEquals(1, nodeAvTwo.NonTestNodes.size());
        assertEquals(secondAddNode, nodeAvTwo.NonTestNodes.get(0));
        assertEquals(secondTestNode, nodeAvTwo.TestNode);
    }

    @Test
    public void getNodeAvailability_should_allow_duplicates() {
        String path = "/interactions";
        ObjectNode firstAddNode = _nodeProvider.createAddStringNode(path, "valueOne");
        ObjectNode secondAddNode = _nodeProvider.createAddStringNode(path, "valueTwo");
        ArrayList<JsonNode> nonTestNodes = new ArrayList<JsonNode>();
        nonTestNodes.add(firstAddNode);
        nonTestNodes.add(secondAddNode);
        PatchNodes patchNodes = new PatchNodes(new ArrayList<JsonNode>(), nonTestNodes);
        Hashtable<String, NodeAvailability> result = _manipulator.getNodeAvailability(patchNodes);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(path));
        NodeAvailability nodeAv = result.get(path);
        assertTrue(nodeAv.hasNonTestNodes());
        assertEquals(2, nodeAv.NonTestNodes.size());
        assertEquals(firstAddNode, nodeAv.NonTestNodes.get(0));
        assertEquals(secondAddNode, nodeAv.NonTestNodes.get(1));
    }

    //sortPatchNodes
}
