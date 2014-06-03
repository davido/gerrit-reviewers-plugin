Build
=====

This plugin is built with Buck.

Clone or link this plugin to the plugins directory of Gerrit's source
tree, and issue the command:

```
  buck build plugins/reviewers
```

The output is created in

```
  buck-out/gen/plugins/reviewers/reviewers.jar
```

This project can be imported into the Eclipse IDE:

```
  ./tools/eclipse/project.py
```

Note that for compatibility reasons a Maven build is provided, but is considered
to be deprecated and will be removed in a future version of this plugin.

To build with Maven, change directory to the plugin folder and issue the
command:

```
  mvn clean package
```

When building with Maven, the Gerrit Plugin API must be available.

How to build the Gerrit Plugin API is described in the [Gerrit
documentation](../../../Documentation/dev-buck.html#_extension_and_plugin_api_jar_files).
