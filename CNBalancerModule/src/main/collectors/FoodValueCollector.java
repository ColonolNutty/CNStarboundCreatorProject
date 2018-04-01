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
public class FoodValueCollector extends BaseCollector implements ICollector {
    private BalancerSettings _settings;
    private Double _totalValue;

    public FoodValueCollector(BalancerSettings settings) {
        _settings = settings;
        _totalValue = 0.0;
    }

    @Override
    public void collectData(Ingredient ingredient, double inputCount, Recipe recipe) {
        _totalValue += calculateValue(inputCount, ingredient.getFoodValue(), _settings.increasePercentage);
    }

    @Override
    public boolean applyData(Ingredient ingredient, double outputCount) {
        double endValue = CNMathUtils.roundTwoDecimalPlaces(_totalValue / outputCount);
        // Apply minimum value
        if(_settings.minimumFoodValue != null && endValue < _settings.minimumFoodValue) {
            endValue = (double)_settings.minimumFoodValue;
        }
        ingredient.update(IngredientProperty.FoodValue, endValue);
        if(ingredient.filePath != null && !ingredient.filePath.endsWith(".consumable")) {
            return false;
        }
        if(ingredient.patchFile != null && !ingredient.patchFile.endsWith(".consumable.patch")) {
            return false;
        }
        Double value = ingredient.getFoodValue();
        if(ingredient.foodValue == null && value == null) {
            return false;
        }
        if(ingredient.foodValue == null && value != null) {
            return true;
        }
        if(ingredient.foodValue != null && value == null) {
            return true;
        }
        return !ingredient.foodValue.equals(value);
    }

    @Override
    public String getDescriptionOfUpdate(Ingredient ingredient) {
        Double oldVal = ingredient.foodValue;
        Double newVal = ingredient.getFoodValue();
        if(oldVal == null) {
            oldVal = 0.0;
        }
        if(newVal == null) {
            newVal = 0.0;
        }
        return "Food Value was: " + oldVal + " it is now: " + newVal;
    }

    @Override
    public String getName() {
        return "Food Value";
    }
}
