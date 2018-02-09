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
public class PriceCollector extends BaseCollector implements ICollector {
    private BalancerSettings _settings;
    private Double _totalValue;

    public PriceCollector(BalancerSettings settings) {
        _settings = settings;
        _totalValue = 0.0;
    }

    @Override
    public void collectData(Ingredient ingredient, double inputCount, Recipe recipe) {
        _totalValue += calculateValue(inputCount, ingredient.price, _settings.increasePercentage);
    }

    @Override
    public boolean applyData(Ingredient ingredient, double outputCount) {
        double endValue = CNMathUtils.roundTwoDecimalPlaces(_totalValue / outputCount);
        // Apply minimum value
        if(endValue < 1.0) {
            endValue = 1.0;
        }
        if(ingredient.price == null || !ingredient.price.equals(endValue)) {
            ingredient.price = endValue;
            return true;
        }
        return false;
    }
}
