package tests.fakeclasses;

import com.colonolnutty.module.shareddata.IPrettyPrinter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.json.JSONObject;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 11:34 AM
 */
public class FakePrettyPrinter implements IPrettyPrinter {

    @Override
    public String makePretty(JSONObject obj, int indentSize) {
        return obj.toString();
    }

    @Override
    public String makePretty(ArrayNode node, int indentSize) {
        return node.asText();
    }
}
