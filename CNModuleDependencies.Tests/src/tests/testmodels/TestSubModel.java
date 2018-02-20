package tests.testmodels;

/**
 * User: Jack's Computer
 * Date: 01/05/2018
 * Time: 11:25 AM
 */
public class TestSubModel {
    public String item;
    public Double count;

    //Used when reading using the Mapper
    public TestSubModel() {
        this(null, null);
    }

    public TestSubModel(String item, Double count) {
        this.item = item;
        this.count = count;
    }
}
