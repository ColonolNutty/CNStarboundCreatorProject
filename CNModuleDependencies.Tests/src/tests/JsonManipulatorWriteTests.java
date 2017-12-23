package tests;

import com.colonolnutty.module.shareddata.JsonManipulator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import tests.fakeclasses.TestObject;

import javax.sound.midi.Patch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.TestCase.*;

/**
 * User: Jack's Computer
 * Date: 12/16/2017
 * Time: 11:43 AM
 */
public class JsonManipulatorWriteTests extends JsonManipulatorTests {

    @Test
    public void writeNew_should_write_recipe() {
        String testFilePath = "somePath\\somePath.json";
        TestObject testObject = new TestObject();
        testObject.propOne = "someVal";
        testObject.propThree = "someVal3";
        String expectedOutput = "{\"propOne\":\"someVal\",\"propThree\":\"someVal3\"}";
        _manipulator.writeNew(testFilePath, testObject);
        HashMap<String, ArrayList<String>> writtenData = _fileWriter.getWrittenData();
        assertTrue(writtenData.containsKey(testFilePath));
        ArrayList<String> data = writtenData.get(testFilePath);
        assertEquals(1, data.size());
        String first = data.get(0);
        assertEquals(expectedOutput, first);
    }

    @Test
    public void writeNewWithTemplate_should_write_recipe() throws IOException {
        String testFilePath = "somePath\\somePath.json";
        String templateFilePath = "somePath\\path.json";
        TestObject testObject = new TestObject();
        testObject.propOne = "someVal";
        testObject.propThree = "someVal3";
        String templateStr = "{\"propOne\":\"someVa6l\",\"propTwo\":\"someVal5\",\"propThree\":\"someVal4\"}";
        String expectedOutput = "{\"propOne\":\"someVal\",\"propThree\":\"someVal3\",\"propTwo\":\"someVal5\"}";
        setFileReaderReturn(templateFilePath, templateStr);
        _manipulator.writeNewWithTemplate(templateFilePath, testFilePath, testObject);

        HashMap<String, ArrayList<String>> writtenData = _fileWriter.getWrittenData();
        assertTrue(writtenData.containsKey(testFilePath));
        ArrayList<String> data = writtenData.get(testFilePath);
        assertEquals(1, data.size());
        String first = data.get(0);
        assertEquals(expectedOutput, first);
    }
}