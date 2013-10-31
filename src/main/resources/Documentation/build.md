Build
=====

This plugin can be built with Buck or Maven.

Buck
----

Two modes of operations of building with Buck are supported: build in Gerrit
tree and outside of the Gerrit tree.

To build in Gerrit tree clone the plugin under plugins directory and run

```
  $>buck build plugins/reviewers:reviewers
```

from the Gerrit base directory. To build the plugin standalone (outside of
the Gerrit tree), run

```
  $>buck build plugin
```

Maven
-----

To build with Maven, run

```
mvn clean package
```

Prerequisites
-------------

Only Gerrit in tree mode doesn't need gerrit-plugin-api dependency. For
other build modes gerrit-plugin-api must be fetched from remote or local
Maven repository.

How to obtain the Gerrit Plugin API is described in the [Gerrit
documentation](../../../Documentation/dev-buck.html#_extension_and_plugin_api_jar_files).

