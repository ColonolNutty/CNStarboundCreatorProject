# PriceFoodValueBalancer
Balances food values and prices using recipes and the ingredients used to make those recipes

### What does this program do?
This program is designed to make it easy to set prices and food values for a mod that has a ton of ingredients

### What is the formula for determining the values?
This will scale with the number of ingredients
* V = Value of price or food
* V1 = Value of the first ingredient
* C = Count of ingredient used in recipe
* C1 = Count of the first ingredient
* OC = Number of output items the recipe produces
* P = Percentage determined in the config settings as "increasePercentage"
* Vn = (V * C) + (V + (V * P) = The formula for the value of each ingredient
* n = The ingredient position in a recipe

Three ingredients
* ((V1) + (V2) + (V3))/OC

The above is then rounded to two decimal places. See example below for a real scenario

Recipe for example:
```javascript
{
  "input" : [
    { "item" : "sugar", "count" : 3 },
    { "item" : "alienfruit", "count" : 2 }
  ],
  "output" : { "item" : "alienfruitjam", "count" : 5 },
  "groups" : [ "craftingfood", "condiments" ]
}
```
* S = Sugar
* AF = Alien Fruit
* IP = increasePercentage
* O = Output
* fv = Food Value
* p = Price
* c = Count of ingredient
* The `Sp` of `sugar` is `90`
* The `Sfv` of `sugar` is `0`
* The `Sc` of `sugar` is `3`
* The `AFp` of `alienfruit` is `40`
* The `AFfv` of `alienfruit` is `10`
* The `AFc` of `alienfruit` is `2`
* The `IP` is `0.05`
* The `Oc` is `5`

The formulas for the above recipe are:
```
Alien Fruit Jam Price = RoundedToTwoDecimalPlaces((((Sp * Sc) + (Sp * IP)) + ((AFp * Afc) + (AFp * IP)))/Oc)
Alien Fruit Jam Food Value = RoundedToTwoDecimalPlaces((((Sfv * Sc) + (Sfv * IP)) + ((AFfv * Afc) + (AFfv * IP)))/Oc)

Alien Fruit Jam Price:
Rounded((((90 * 3) + (90 * 0.05)) + ((40 * 2) + (40 * 0.05)))/5)
= Rounded(274.5 + 82)/5)
= Rounded(356.5/5) = 71.3

Alien Fruit Jam Food Value:
RoundedToTwoDecimalPlaces((((0 * 3) + (0 * 0.05)) + ((10 * 2) + (10 * 0.05)))/5)
= Rounded((0 + 20.5)/5)
= Rounded(20.5/5) = 4.1

```


### Configuration File:
| Type | Property Name | Description |
| ---- | ------------- | ----------- |
| String[] | locationsToUpdate | The file paths you would like food values and prices to updated in (It will overwrite your files within these locations) |
| String[] | includeLocations | The file paths of locations you would like to include when the program calculates  the prices and food values (The program will also NOT write to these locations, even if they are a sub folder listed in locationsToUpdate) |
| String | ingredientOverridePath | A path to a file that provides food values and prices for items that don't have them. This is where you list food values and prices you would like to initially start with, for example sugar.item doesn't have a food value, so with this file you can specify a food value |
| Double | increasePercentage | A percentage of the total price you would like to add to resulting food values and prices. |
| int | numberOfPasses | The total number of times you would like the program to run through ingredients, recommended value is 8 (The higher the number, the more accurate the food values and prices will be, but the longer it will take) |
| boolean | enableConsoleDebug | If set to true, you will see [Debug] message in the console |
