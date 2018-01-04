package main.models;

import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 01/04/2018
 * Time: 11:18 AM
 */
public class BalancedIngredient {
    public String ItemName;
    public Double Price;
    public Double FoodValue;
    public String Description;
    public Hashtable<String, Integer> Effects;

    public BalancedIngredient(String itemName) {
        ItemName = itemName;
        Effects = new Hashtable<String, Integer>();
        Price = 0.0;
        FoodValue = 0.0;
        Description = "";
    }

    public void addOrUpdateEffect(String name, int duration) {
        if(Effects.containsKey(name)) {
            int existingValue = Effects.get(name);
            Effects.put(name, existingValue + duration);
        }
        else {
            Effects.put(name, duration);
        }
    }
}
