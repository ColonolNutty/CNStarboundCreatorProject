package tests;

import com.colonolnutty.module.shareddata.*;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.colonolnutty.module.shareddata.prettyprinters.IPrettyPrinter;
import tests.fakes.FakeFileWriter;
import tests.fakes.FakePrettyPrinter;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Jack's Computer
 * Date: 12/14/2017
 * Time: 12:37 PM
 */
public class JsonManipulatorTests {
    protected BaseSettings _settings;
    protected FakeFileWriter _fileWriter;
    protected IFileReader _fileReader;
    protected NodeProvider _nodeProvider;
    protected IPrettyPrinter _prettyPrinter;
    protected JsonManipulator _manipulator;

    public JsonManipulatorTests() {
        CNLog log = mock(CNLog.class);
        String[] propToUpd = new String[4];
        propToUpd[0] = "foodValue";
        propToUpd[1] = "price";
        propToUpd[2] = "effects";
        propToUpd[3] = "description";
        _settings = new BaseSettings();
        _settings.propertiesToUpdate = propToUpd;

        _manipulator = new JsonManipulator(log, _settings);

        _fileWriter = new FakeFileWriter();
        _prettyPrinter = new FakePrettyPrinter();
        _fileReader = mock(IFileReader.class);
        _nodeProvider = new NodeProvider();

        _manipulator.setFileWriter(_fileWriter);
        _manipulator.setFileReader(_fileReader);
        _manipulator.setNodeProvider(_nodeProvider);
        _manipulator.setPrettyPrinter(_prettyPrinter);
    }

    protected void setFileReaderReturn(String filePath, String returnedData) throws IOException {
        when(_fileReader.readFile(filePath)).thenReturn(returnedData);
    }
}
