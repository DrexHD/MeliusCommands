# Melius Commands

[![Discord](https://img.shields.io/discord/904419828192927885.svg?logo=discord)](https://discord.gg/HeZayd6SxF)

A fabric server side mod to create custom, and modify existing commands

## Custom Commands

To add custom commands, create a json file in `./config/melius-commands/commands`.
For example files, see [examples/commands](./examples/commands)! Apply your changes, by running `/reload` in-game.

### JSON Format

Json format includes comments for clarification (don't copy `//` in your files)

#### Command node (literal / argument)

Check out the [online generator](https://drexhd.vercel.app/melius-commands/commands/) to create your custom commands
quick and easy! This will also make sure your syntax is valid.

The root node must always be a literal node.

```json5
{
  "id": "...",
  // Command id (command name for literal command nodes) (required)
  "type": "...",
  // Argument type, see below. (argument node only, required)
  "executes": [
    // A list of command node executions, see below.
  ],
  "require": {
    // See https://github.com/Patbox/PredicateAPI/ for further information.
    // If this evaluates to true for a Command Source, it will have access to this command node!
  },
  "literals": [
    // A list of literal children nodes
  ],
  "arguments": [
    // A list of argument children nodes
  ],
  "redirect": "..."
  // If specified the command will copy execution, requirements and children from the specified node.
  // This can effectively create an alias for another command
}
```

#### Execution

This can either be a simple string, which will be executed silent, as console with op level 4 or manually configured
like this:

```json5
{
  "command": "...",
  // The command that will be executed (can reference argument values, with ${id})
  "silent": false,
  // Disable command output if set to true
  "as_console": false,
  // Whether this command should be executed as console command source
  "op_level": 4
  // The operator level of the command source (optional) (Possible values: 0 - 4)
}
```

#### Argument Type

For a full list of all argument types refer to the [Argument types wiki](https://minecraft.wiki/w/Argument_types).
Some argument types require additional arguments, which must be appended seperated by spaces.

## Command Modifiers

To add command requirements, create a json file in `./config/melius-commands/modifiers`.
For example files, see [examples/modifiers](./examples/modifiers)!

### JSON Format

Check out the [online generator](https://drexhd.vercel.app/melius-commands/modifiers/) to create your custom command
modifiers quick and easy! This will also make sure your syntax is valid.

Json format includes comments for clarification (don't copy `//` in your files)

#### Command modifier

The syntax depends on the modifier type you choose. The most important thing to note is that there are two types of
command matchers:

- Node matchers will match command nodes (the structure used by brigadier to represent commands internally) by their
  path (for example `teleport.location`, will match any command like this `/teleport 0 100 0`)
- Command matchers will match against the string used during command execution (for example `warp end` will
  match `/warp end`)

Both of these types come with three different flavours:

- `strict` modifiers will only be applied to that exact node / that exact command input
- `starts_with` modifiers will be applied to the specified node / command and anything that is "longer" than it
- `regex` modifiers will be applied to everything matching the regular expression

Node matchers have an extra field called `requirement_modifier`, which allows you to modify the requirement of the node,
which will completely remove the command from players / command sources that don't meet the requirement!

### Command node paths

You can use `/melius-commands path <cmd>` command to determine command node paths of any command.
Example: to check the path of `/tp DrexHD 0 100 0` (teleporting an entity to a block location) you type
`/melius-commands path tp DrexHD 0 100 0` and will receive `teleport.targets.location`, which you can use in command
modifiers.