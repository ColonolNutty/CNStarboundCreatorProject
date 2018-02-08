package main.collectors;

import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.utils.CNMathUtils;

/**
 * User: Jack's Computer
 * Date: 02/08/2018
 * Time: 12:21 PM
 */
public class FoodValueCollector implements ICollector {
    private Double _increasePercentage;
    private Double _totalValue;

    public FoodValueCollector(Double increasePercentage) {
        _increasePercentage = increasePercentage;
        _totalValue = 0.0;
    }

    @Override
    public void collectData(Ingredient ingredient, double inputCount) {
        _totalValue += calculateValue(inputCount, ingredient.foodValue, _increasePercentage);
    }

    @Override
    public boolean applyData(Ingredient ingredient, double outputCount) {
        double endValue = CNMathUtils.roundTwoDecimalPlaces(_totalValue / outputCount);
        if(ingredient.foodValue == null || !ingredient.foodValue.equals(endValue)) {
            ingredient.foodValue = endValue;
            return true;
        }
        return false;
    }

    /**
     * Calculates using formula: (v * c) + (v * iP)
     * @param count (c) number of items
     * @param value (v) value of items
     * @param increasePercentage (iP) amount of value to add to the total
     * @return (v * c) + (v * iP)
     */
    public Double calculateValue(Double count, Double value, Double increasePercentage) {
        if(count == null || value == null || increasePercentage == null) {
            return 0.0;
        }
        if(count <= 0.0) {
            count = 1.0;
        }
        if(value < 0.0) {
            value = 0.0;
        }
        return (value * count) + (value * increasePercentage);
    }
}
