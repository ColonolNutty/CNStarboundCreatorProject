package com.colonolnutty.module.shareddata.io;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * User: Jack's Computer
 * Date: 12/16/2017
 * Time: 12:07 PM
 */
public class MapperWrapper {

    protected ObjectMapper _mapper;

    public MapperWrapper() {
        JsonFactory jf = new JsonFactory();
        jf.enable(JsonParser.Feature.ALLOW_COMMENTS);
        _mapper = new ObjectMapper(jf);
        _mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    public ObjectMapper getMapper() {
        return _mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        _mapper = mapper;
    }
}
