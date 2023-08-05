# MeliusCommands
[![Discord](https://img.shields.io/discord/904419828192927885.svg?logo=discord)](https://discord.gg/HeZayd6SxF)

A fabric server side mod to create custom, and modify existing commands

## Custom Commands
To add custom commands, create a json file in `./config/melius-commands/commands`.
For example files, see [examples/commands](./examples/commands)! Apply your changes, by running `/reload` in-game.

### JSON Format
Json format includes comments for clarification (don't copy `//` in your files)

#### Command node (literal / argument)
The root node must always be a literal node.
```json5
{
  "id": "...", // Command id (command name for literal command nodes) (required)
  "type": "...", // Argument type, see below. (argument node only, required)
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
  ]
}
```

#### Execution
```json5
{
  "command": "...", // The command that will be executed (can reference argument values, with ${id})
  "silent": false, // Disable command output if set to true
  "as_console": false, // Whether this command should be executed as console command source
  "op_level": 4 // The operator level of the command source (optional) (Possible values: 0 - 4)
}
```

#### Argument Type
For a full list of all argument types refer to the [Argument types wiki](https://minecraft.fandom.com/wiki/Argument_types).
Some argument types require additional arguments, which must be appended seperated by spaces.

## Command Requirements
To add command requirements, create a json file in `./config/melius-commands/requirements`.
For example files, see [examples/requirements](./examples/requirements)!

### JSON Format

Json format includes comments for clarification (don't copy `//` in your files)
#### Command requirement

```json5
{
  "command_path": "...", // Command path (a . seperated string of all command node ids) of the modified command
  "replace": true, // Whether the original requirements should be replaced or added
  "require": {
    // See https://github.com/Patbox/PredicateAPI/ for further information.
  }
}
```