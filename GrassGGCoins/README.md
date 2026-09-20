# GrassGGCoins

GrassGGCoins is a second currency plugin for Paper 26.2.

## Current functionality

- Global 15-minute reward timer.
- Every online player receives +1 coin every 15 minutes.
- Reward action bar + chat message.
- `/coins` main menu.
- `/coins shop` and `/coinshop` shop menu.
- `/coins shop setDisplayItem <identifier>` stores the complete item from the player's main hand.
- Shop purchases deduct coins first, then run the configured command as console.
- `%player%` is replaced in shop commands.
- Balance can never go below zero.
- Insufficient balance sends a message and plays an error sound.
- `/coins give <player> <amount>`
- `/coins take <player> <amount>`
- `/coins set <player> <amount>`
- `/coins balance [player]`
- PlaceholderAPI support for `%grassggcoins_balance%` and `%grassggcoins_coins%`. (Current PlaceholderAPI parsing uses the underscore form.)
- Amounts are formatted using `k`, `m`, `b`, `t`, and `q` suffixes.
- All player-facing messages, titles, prefixes and primary colours are in `Messages.java`.
- Player balances are stored in `plugins/GrassGGCoins/players.yml`.
- Shop configuration is stored in `plugins/GrassGGCoins/shop.yml`.

## Build

Open the project in IntelliJ IDEA and import the Maven project from `pom.xml`.
The project targets Java 25 and Paper 26.2.

## PlaceholderAPI

PlaceholderAPI is optional. If installed, the internal expansion is registered automatically.
