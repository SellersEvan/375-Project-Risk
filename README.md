# 375 Project - Risk
**Members:** Aidan Mazany, Evan Sellers, Richard Costa, and Hunter Masur \
**Original 376 Code**: Andrew Sullivan, Aidan Mazany, Dixon Ramey, and Jadon Brutcher

## Assets
[Trello Board](https://trello.com/b/sk6E8u3f/schedule) \
[Orginal Codebase](https://github.com/SellersEvan/375-Project-Risk/tree/be735fef9072abc9651f811f5ac86df7feb8801a)

## Potential Features 
- Updated User Interfaces
- Abilitiy to have different maps

## Potential Refactoring Changes
- Remove switch statement and make more abstract method `model.CardTrader.generateNewCard`
- Bad Data Class (possible) `model.Card`
- Overcomplicated logic, why if else and throw error `model.CardTrader.getCurrentSetValue`
- Is it possible to simplify the logic for `model.CardTrader.tradeInCardSet`
- Fix logic with constant of 42 `model.Player.hasWon`
- Roll dice has no association to the player class, it can be moved `model.Player.rollDice`
- Implement Contenent Enum in `model.Territory`
- Implement HashMap for `model.Territory.**_TERRITORIES`, `model.Territory.**_BONUS`
- Rename Paramaters for `model.Territory.loseArmies`, `model.Territory.addArmies`, `model.Territory.addAdjacentTerritory`
- Seperate return statment for `model.Territory.attackTerritory`
- Complete Re-Organize for `controller.Game`
