# 375 Project - Risk
**Members:** Aidan Mazany, Evan Sellers, Richard Costa, and Hunter Masur \
**Original 376 Code**: Andrew Sullivan, Aidan Mazany, Dixon Ramey, and Jadon Brutcher

## Assets
[Trello Board](https://trello.com/b/sk6E8u3f/schedule)

## Potential Features 
- Updated User Interfaces
  - Indicate which Territory is selected
  - Indicate which Territories can be attacked from the selected Territory (without needing to guess and check)
- Abilitiy to have different maps
- Implement "Wild Cards" (In the official game but not yet implemented in this project)
- New Risk Game Modes
  - Secret Mission Risk (Would also require implementing "Mission Cards") 
  - World Domination Risk
  - Capital Risk

## Potential Refactoring Changes (general remedies to bad smells and changes to accomodate the new features)
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
- Multiple classes use magic numbers - replace with constants using explanatory names
- Confusing return value in `model.Player.attackTerritory`
- Generalize win conditions to make alternative game modes possible (currently only checks that a player controls every Territory)
- World Domination Risk uses "neutral" armies (controlled by no players), but the current codebase requires every player color has a controlling player who takes turns. Multiple refactors would probably be necessary to implement this without giving `controller.Game` more responsibilities than it already has
