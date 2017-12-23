package tests.fakes;

import com.colonolnutty.module.shareddata.IFileWriter;
import com.colonolnutty.module.shareddata.MapperWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: Jack's Computer
 * Date: 12/16/2017
 * Time: 11:32 AM
 */
public class FakeFileWriter extends MapperWrapper implements IFileWriter {

    private HashMap<String, ArrayList<String>> _writtenData;

    public FakeFileWriter() {
        _writtenData = new HashMap<String, ArrayList<String>>();
    }

    @Override
    public String writeValueAsString(Object obj) throws JsonProcessingException {
        return _mapper.writeValueAsString(obj);
    }

    @Override
    public JsonNode valueToTree(Object obj) {
        return _mapper.valueToTree(obj);
    }

    @Override
    public <T> T treeToValue(TreeNode modNode, Class<T> valueType) throws JsonProcessingException {
        return _mapper.treeToValue(modNode, valueType);
    }

    @Override
    public File createFile(String filePath) throws IOException {
        addWrittenData(filePath, null);
        return new File(filePath);
    }

    @Override
    public void writeData(String filePath, String data) throws IOException {
        addWrittenData(filePath, data);
    }

    @Override
    public void writeData(File file, String data) throws IOException {
        addWrittenData(file.getPath(), data);
    }

    public HashMap<String, ArrayList<String>> getWrittenData() {
        return _writtenData;
    }

    private void addWrittenData(String filePath, String data) {
        if(_writtenData.containsKey(filePath)) {
            _writtenData.get(filePath).add(data);
            return;
        }
        ArrayList<String> writtenData = new ArrayList<String>();
        if(data != null) {
            writtenData.add(data);
        }
        _writtenData.put(filePath, writtenData);
    }
}
