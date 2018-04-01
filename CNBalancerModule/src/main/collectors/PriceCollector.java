package main.collectors;

import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.IngredientProperty;
import com.colonolnutty.module.shareddata.models.Recipe;
import com.colonolnutty.module.shareddata.utils.CNMathUtils;
import main.settings.BalancerSettings;

/**
 * User: Jack's Computer
 * Date: 02/08/2018
 * Time: 12:21 PM
 */
public class PriceCollector extends BaseCollector implements ICollector {
    private BalancerSettings _settings;
    private Double _totalValue;

    public PriceCollector(BalancerSettings settings) {
        _settings = settings;
        _totalValue = 0.0;
    }

    @Override
    public void collectData(Ingredient ingredient, double inputCount, Recipe recipe) {
        _totalValue += calculateValue(inputCount, ingredient.getPrice(), _settings.increasePercentage);
    }

    @Override
    public boolean applyData(Ingredient ingredient, double outputCount) {
        double endValue = CNMathUtils.roundTwoDecimalPlaces(_totalValue / outputCount);
        // Apply minimum value
        if(endValue < 1.0) {
            endValue = 1.0;
        }
        ingredient.update(IngredientProperty.Price, endValue);
        Double value = ingredient.getPrice();
        if(ingredient.price == null && value == null) {
            return false;
        }
        if(ingredient.price == null && value != null) {
            return true;
        }
        if(ingredient.price != null && value == null) {
            return true;
        }
        return !ingredient.price.equals(value);
    }

    @Override
    public String getDescriptionOfUpdate(Ingredient ingredient) {
        Double oldVal = ingredient.price;
        Double newVal = ingredient.getPrice();
        if(oldVal == null) {
            oldVal = 0.0;
        }
        if(newVal == null) {
            newVal = 0.0;
        }
        return "Price was: " + oldVal + " it is now: " + newVal;
    }

    @Override
    public String getName() {
        return "Price";
    }
}
