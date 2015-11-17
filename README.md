# PermissionsSetup
A Bukkit plugin for adding permissions to commands from plugins that don't properly do it themselves.

All permissions and plugins are managed via configuration:
```
plugins:
  # Name of the plugin. This is case-sensitive, capitalize properly!
  exAMPLE:
    # format is the permission format, where %s is the name of the command
    # Ex: 'example.%s' results in the permission 'example.test' being generated for /test
    format: 'example.%s'
    # Wildcards are optional parent nodes. This section can be an empty list or excluded entirely.
    wildcards:
    - example.*
    - example.admin
```
The format section must be a valid [format](http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax)! The only parameter for formatting is a String, the command's name.  
The wildcards section is a list of parent nodes. No wildcards are required.

To add more plugins, add sections by the name of the plugin.
```
plugins:
  'Essentials':
    format: 'essentials.%s'
    wildcards:
    - essentials.*
  'AnotherExample':
    format: 'anotherexample.command.%s'
    wildcards:
    - anotherexample.command.*
    - anotherexample.*
  'NoWildcardsExample':
    format: 'nowildcards.%s'
```
