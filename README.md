# PermissionsSetup
A Bukkit plugin for adding permissions to commands from plugins that don't properly do it themselves.

All permissions and plugins are managed via configuration:
```
# Manage all commands for plugins. Far easier than manually configuring individual commands.
plugins:
  # Name of the plugin. This is case-sensitive, capitalize properly!
  exAMPLE:
    # format is the permission format, where %s is the name of the command
    # Ex: 'example.%s' results in the permission 'example.test' being generated for /test
    format: 'example.%s'
    # Parent nodes are optional. This section can be an empty list or excluded entirely.
    # Parent nodes grant the automatically generated child nodes.
    parents:
    - example.*
    - example.admin
# Manually configure commands
commands:
  # Name of the command
  example:
    # Permission for the command
    permission: example.example
    # Parent nodes for the command
    parents:
    - example.*
    - example.admin
```
The format section must be a valid [format](http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax)! The only parameter for formatting is a String, the command's name.  
The parents section is a list of parent nodes. No parents are required.

To add more plugins, add sections by the name of the plugin.
```
plugins:
  'Essentials':
    format: 'essentials.%s'
    parents:
    - essentials.*
  'AnotherExample':
    format: 'anotherexample.command.%s'
    parents:
    - anotherexample.command.*
    - anotherexample.*
  'NoParentsExample':
    format: 'noparents.%s'
```

For individual commands, simply add more sections by command name.  
PermissionsSetup will attempt to match a command using aliases if provided, but makes no guarantee that it can properly, and may fall back to whatever plugin command by the same name is taking precedence.  
As with the plugins section, parents is an optional list of parent nodes.  

You can also use this section to edit permissions for commands added by the server implementation, however, server updates may break this functionality.
```
commands:
  # Name of the command
  example:
    # Permission for the command
    permission: example.example
    # Parent nodes for the command
    parents:
    - example.*
    - example.admin
  # Aliased name of the command
  anotherexample:example
    # Permission for the command
    permission: anotherexample.example
    # Parent nodes for the command
    parents:
    - anotherexample.*
    - anotherexample.command.*
```