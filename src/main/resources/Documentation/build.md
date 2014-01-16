Build
=====

This plugin can be built with Buck or Maven.

Buck
----

To build with Buck in standalone mode (outside of the Gerrit tree),
run

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

gerrit-plugin-api must be fetched from remote or local Maven repository.

How to obtain the Gerrit Plugin API is described in the [Gerrit
documentation](../../../Documentation/dev-buck.html#_extension_and_plugin_api_jar_files).

