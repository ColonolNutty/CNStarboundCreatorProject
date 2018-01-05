package tests.testmodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:09 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestModel {
    public TestSubModel[] input;
    public TestSubModel output;
    public String[] groups;
}
