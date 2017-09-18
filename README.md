# PriceFoodValueBalancer
Balances food values and prices using recipes and the ingredients used to make those recipes

What does this program do?


Configuration File:
String[] locationsToUpdate - The file paths you would like food values and prices to updated in (It will overwrite your files within these locations)
String[] includeLocations - The file paths of locations you would like to include when the program calculates  the prices and food values (The program will also NOT write to these locations, even if they are a sub folder listed in locationsToUpdate 
String ingredientOverridePath - A path to a file that provides food values and prices for items that don't have them. This is where you list food values and prices you would like to initially start with, for example sugar.item doesn't have a food value, so with this file you can specify a food value
Double increasePercentage - A percentage of the total price you would like to add to resulting food values and prices.
int numberOfPasses - The total number of times you would like the program to run through ingredients, recommended value is 8 (The higher the number, the more accurate the food values and prices will be, but the longer it will take)
boolean enableConsoleDebug - If set to true, you will see [Debug] message in the console
