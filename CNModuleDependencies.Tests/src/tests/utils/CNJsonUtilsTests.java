package tests.utils;

import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.colonolnutty.module.shareddata.utils.CNMathUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * User: Jack's Computer
 * Date: 12/14/2017
 * Time: 12:37 PM
 */
public class CNJsonUtilsTests {

    private NodeProvider _nodeProvider;

    public CNJsonUtilsTests() {
        _nodeProvider = new NodeProvider();
    }

    //getNodePath
    @Test
    public void getNodePath_should_return_first_non_empty_path_found() {
        ArrayNode arrNode = _nodeProvider.createArrayNode();
        ObjectNode objOne = _nodeProvider.createObjectNode();
        arrNode.add(objOne);
        ObjectNode objTwo = _nodeProvider.createObjectNode();
        objTwo.put("path", "two");
        arrNode.add(objTwo);
        ObjectNode objThree = _nodeProvider.createObjectNode();
        objThree.put("path", "three");
        arrNode.add(objThree);
        String result = CNJsonUtils.getNodePath(arrNode);
        assertEquals("two", result);
    }

    @Test
    public void getNodePath_should_return_first_path_found() {
        ArrayNode arrNode = _nodeProvider.createArrayNode();
        ObjectNode objOne = _nodeProvider.createObjectNode();
        objOne.put("path", "one");
        arrNode.add(objOne);
        ObjectNode objTwo = _nodeProvider.createObjectNode();
        objTwo.put("path", "two");
        arrNode.add(objTwo);
        String result = CNJsonUtils.getNodePath(arrNode);
        assertEquals("one", result);
    }

    @Test
    public void getNodePath_should_return_null_for_empty_arraynode() {
        ArrayNode arrNode = _nodeProvider.createArrayNode();
        String result = CNJsonUtils.getNodePath(arrNode);
        assertNull(result);
    }

    @Test
    public void getNodePath_should_return_null_from_objectnode_inside_arraynode_with_no_path() {
        ArrayNode arrNode = _nodeProvider.createArrayNode();
        ObjectNode node = _nodeProvider.createObjectNode();
        arrNode.add(node);
        String result = CNJsonUtils.getNodePath(arrNode);
        assertNull(result);
    }

    @Test
    public void getNodePath_should_return_path_from_objectnode_inside_arraynode_inside_arraynode() {
        ArrayNode arrNode = _nodeProvider.createArrayNode();
        ArrayNode arrNodeTwo = _nodeProvider.createArrayNode();
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("path", "I am path");
        arrNodeTwo.add(node);
        arrNode.add(arrNodeTwo);
        String result = CNJsonUtils.getNodePath(arrNode);
        assertEquals("I am path", result);
    }

    @Test
    public void getNodePath_should_return_path_from_objectnode_inside_arraynode() {
        ArrayNode arrNode = _nodeProvider.createArrayNode();
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("path", "I am path");
        arrNode.add(node);
        String result = CNJsonUtils.getNodePath(arrNode);
        assertEquals("I am path", result);
    }

    @Test
    public void getNodePath_should_return_null_from_objectnode_with_no_path() {
        ObjectNode node = _nodeProvider.createObjectNode();
        String result = CNJsonUtils.getNodePath(node);
        assertNull(result);
    }

    @Test
    public void getNodePath_should_return_path_from_objectnode() {
        ObjectNode node = _nodeProvider.createObjectNode();
        node.put("path", "I am path");
        String result = CNJsonUtils.getNodePath(node);
        assertEquals("I am path", result);
    }
    //getNodePath
}
