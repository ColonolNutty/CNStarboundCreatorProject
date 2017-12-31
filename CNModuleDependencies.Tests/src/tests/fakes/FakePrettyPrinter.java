package tests.fakes;

import com.colonolnutty.module.shareddata.prettyprinters.BasePrettyPrinter;
import org.json.JSONException;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 11:34 AM
 */
public class FakePrettyPrinter extends BasePrettyPrinter {
    @Override
    public String makePretty(Object obj, int indentSize) throws JSONException {
        return obj.toString();
    }

    @Override
    public boolean canPrettyPrint(Object obj) {
        return true;
    }
}
