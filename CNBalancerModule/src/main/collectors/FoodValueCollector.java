package main.collectors;

import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.Recipe;
import com.colonolnutty.module.shareddata.utils.CNMathUtils;
import main.settings.BalancerSettings;

/**
 * User: Jack's Computer
 * Date: 02/08/2018
 * Time: 12:21 PM
 */
public class FoodValueCollector extends BaseCollector implements ICollector {
    private BalancerSettings _settings;
    private Double _totalValue;

    public FoodValueCollector(BalancerSettings settings) {
        _settings = settings;
        _totalValue = 0.0;
    }

    @Override
    public void collectData(Ingredient ingredient, double inputCount, Recipe recipe) {
        _totalValue += calculateValue(inputCount, ingredient.foodValue, _settings.increasePercentage);
    }

    @Override
    public boolean applyData(Ingredient ingredient, double outputCount) {
        double endValue = CNMathUtils.roundTwoDecimalPlaces(_totalValue / outputCount);
        // Apply minimum value
        if(_settings.minimumFoodValue != null && endValue < _settings.minimumFoodValue) {
            endValue = (double)_settings.minimumFoodValue;
        }
        if(ingredient.foodValue == null || !ingredient.foodValue.equals(endValue)) {
            ingredient.foodValue = endValue;
            return true;
        }
        return false;
    }
}
