# GrassGGSuperTools

Paper 26.2 / Java 25 plugin.

Command:
`/supertools give [player] [tool]`

Permission:
`grassgg.supertools.admin`

Tool IDs:
- `pickaxe_silk`
- `pickaxe_fortune`
- `shovel_silk`
- `shovel_fortune`
- `axe`
- `hoe`

The tools are marked with a PersistentDataContainer value so renamed ordinary netherite tools cannot accidentally trigger SuperTools behaviour.

Build with Maven:
`mvn clean package`

The resulting plugin jar is `target/GrassGGSuperTools.jar`.
