package tests.shared;

import junit.framework.TestCase;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 03/25/2018
 * Time: 12:04 PM
 */
public abstract class TestBase {
    public <T, U> void assertContainsKey(Hashtable<T, U> table, T key) {
        TestCase.assertTrue("'" + key + "' not found in table, table contents: " + formatTable(table), table.containsKey(key));
    }

    public <T, U> void assertNotContainsKey(Hashtable<T, U> table, T key) {
        TestCase.assertFalse("'" + key + "' found in table, table contents: " + formatTable(table), table.containsKey(key));
    }

    private <T, U> String formatTable(Hashtable<T, U> table) {
        if(table.isEmpty()) {
            return "[ ]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        Enumeration<T> keys = table.keys();
        if(table.size() == 1) {
            T key = keys.nextElement();
            return "[{ " + key + ", " + table.get(key) + " }]";
        }
        while(keys.hasMoreElements()) {
            T key = keys.nextElement();
            builder.append("{ " + key + ", " + table.get(key) + " }]");
            if(keys.hasMoreElements()) {
                builder.append(",");
            }
        }
        return builder.toString();
    }
}
