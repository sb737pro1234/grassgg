# GrassGGQuests

GrassGGQuests is a Paper 26.2 quest progression plugin for the GrassGG network.

## Requirements

- Paper 26.2
- Java 25 on the server
- PlaceholderAPI is optional; when installed, `%grassggquests_level%`, `%grassggquests_xp%` and `%grassggquests_points%` are available.

## Project configuration

- `Messages.java` — all messages, colours, GUI names and GUI lore.
- `Quests.java` — quest definitions and Quest XP level requirements.
- `config.yml` — Quest Shop items, costs, commands, slots and saved display items.
- `players.yml` — generated persistent player data.

## Commands

- `/quests`
- `/quests shop`
- `/quests level`
- `/quest`
- `/questshop`
- `/questlevel`
- `/quests reload` (admin)
- `/quests shop setDisplayItem <ITEM_IDENTIFIER>` (admin)
- `/quests setlevel <player> <level>` (admin)
- `/quests reset <player>` (admin)
- `/quests give <player> <quest>` (admin)

Admin permission: `grassggquests.admin`

## Building

Use Java 25 and Maven:

`mvn clean package`

The compiled plugin will be created as `target/GrassGGQuests-1.0.jar`.

## Quest Progress Feedback

- Active quest progress displays in a per-player bossbar while the player is making progress.
- Bossbar colour, style, title and display duration are configurable in `Messages.java`.
- Completing a quest awards 1 Quest Point, shows a configurable title/subtitle, and sends an achievement-style boxed message.
- Completion title timing is configurable in `Messages.java`.
