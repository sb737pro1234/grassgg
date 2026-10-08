# GrassGGQuests

GrassGGQuests is a Paper 26.2 quest plugin for the GrassGG network.

## Requirements

- Paper 26.2
- Java 25 on the server
- GrassGGCoins commands available on the server

## Quest system

- Every player has 3 active quests.
- New quests are Medium quests and reward 10 coins.
- A Medium quest can be changed to Easy for 2 coins.
- Easy quests reward 5 coins.
- Quests can be rerolled for 2 coins.
- Rerolls use a confirmation menu with Cancel in slot 10, the quest display in slot 13, and Confirm in slot 16.
- Coin rewards use `/coins give %player% %amount%`.
- Coin costs use `/coins take %player% 2`.
- The plugin does not hook into or depend on GrassGGCoins.

## Commands

- `/quests`
- `/quest`
- `/q`
- `/quests reload` (admin)
- `/quests reset <player>` (admin)
- `/quests give <player> <quest>` (admin)

The main menu opens Coin Shop through `/coinshop` as the player. This plugin does not create or implement `/coinshop`.

Admin permission: `grassggquests.admin`

## Configuration

- `Messages.java` — GUI names, lore, colours and messages.
- `Quests.java` — quest definitions.
- `players.yml` — generated persistent player quest data.

## Building

Use Java 25 and Maven:

`mvn clean package`

The compiled plugin will be created as `target/GrassGGQuests-1.0.jar`.
