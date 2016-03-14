Configuration
=============

The configuration of the @PLUGIN@ plugin is done on project level in
the `reviewers.config` file of the project. Missing values are inherited
from the parent projects. This means a global default configuration can
be done in the `reviewers.config` file of the `All-Projects` root project.
Other projects can then override the configuration in their own
`reviewers.config` file.

```
  [filter "*"]
    reviewer = john.doe@example.com

  [filter "branch:main file:^lib/.*"]
    reviewer = jane.doe@example.com

  [filter "branch:stable-2.10"]
    reviewer = QAGroup

```

filter.<filter>.reviewer
:	An account or a group name. Must be an exact match (case sensitive) with the
	account's email address or username, or the group name.  Multiple `reviewer`
	occurrences are allowed.

##Multiple filter matches

The plugin supports multiple filter matches.

###Example

```
  [filter "file:^build/modules/.*"]
    reviewer = john.doe@example.com

  [filter "file:^build/.*"]
    reviewer = jane.doe@example.com

```

1. Push a change for review involving file "build/modules/GLOBAL.pm".
2. Both john.doe@example.com and jane.doe@example.com get added as reviewers.
