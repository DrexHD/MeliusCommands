# Arguments

## Brigadier

- `briagdier:bool`
- `briagdier:string <type>`: `<type>` can be `single_word`, `quotable_phrase` or `greedy_phrase`
- `brigadier:integer <min> <max>`: `<min>` and `<max>` can be any integer. It is possible to omit both arguments.
- `brigadier:double <min> <max>`: `<min>` and `<max>` can be any double. It is possible to omit both arguments.
- `brigadier:long <min> <max>`: `<min>` and `<max>` can be any long. It is possible to omit both arguments.
- `brigadier:float <min> <max>`: `<min>` and `<max>` can be any float. It is possible to omit both arguments.

## Minecraft

- `minecraft:entity <type>`: `<type>` can be `entity`, `entities`, `player`, `players`
- `minecraft:resource <resource_location>`: `<resource_location>` can be a registry resource location, eg: `minecraft:attribute`, `minecraft:damageType`, `minecraft:mob_effect`, `minecraft:enchantment`, `minecraft:entity_type`, `minecraft:worldgen/biome`
- `minecraft:resource_key <resource_location>`: `<resource_location>` can be a registry resource location, eg: `minecraft:advancement`, `minecraft:worldgen/configured_feature`, `minecraft:worldgen/template_pool`, `minecraft:worldgen/structure`, `minecraft:recipe`
- `minecraft:resource_or_tag <resource_location>`: `<resource_location>` can be a registry resource location, eg: `minecraft:worldgen/biome`, `minecraft:point_of_interest_type`
- `minecraft:resource_or_tag_key <resource_location>`: `<resource_location>` can be a registry resource location, eg: `minecraft:worldgen/structure`
- `minecraft:score_holder <multiple>`: `<multiple>` can be `true` or `false`
- `minecraft:time <minimum>`: `<minimum>` can be any integer
- `minecraft:vec2 <center_correct>`: `<center_correct>` can be `true` or `false`
- `minecraft:vec3 <center_correct>`: `<center_correct>` can be `true` or `false`

- `minecraft:angle`
- `minecraft:block_pos`
- `minecraft:block_predicate`
- `minecraft:block_state`
- `minecraft:color`
- `minecraft:column_pos`
- `minecraft:component`
- `minecraft:dimension`
- `minecraft:entity_anchor`
- `minecraft:float_range`
- `minecraft:function`
- `minecraft:game_profile`
- `minecraft:gamemode`
- `minecraft:heightmap`
- `minecraft:int_range`
- `minecraft:item_predicate`
- `minecraft:item_slot`
- `minecraft:item_slots`
- `minecraft:item_stack`
- `minecraft:loot_modifier`
- `minecraft:loot_predicate`
- `minecraft:loot_table`
- `minecraft:message`
- `minecraft:nbt_compound_tag`
- `minecraft:nbt_path`
- `minecraft:nbt_tag`
- `minecraft:objective_criteria`
- `minecraft:objective`
- `minecraft:operation`
- `minecraft:particle`
- `minecraft:resource_location`
- `minecraft:rotation`
- `minecraft:scoreboard_slot`
- `minecraft:style`
- `minecraft:swizzle`
- `minecraft:team`
- `minecraft:template_mirror`
- `minecraft:template_rotation`
- `minecraft:uuid`

*All resource location arguments are example vanilla use cases, there are many more that can be used!*