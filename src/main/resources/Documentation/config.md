Configuration
=============

Global configuration of the @PLUGIN@ plugin is done in the
`reviewers.config` file in the site's `etc` directory.

```
  [reviewers]
    enableREST = true
    enableUI = false
```

reviewers.enableREST
:	Enable the REST API. When set to false, the REST API is not available.
	Defaults to true.

reviewers.enableUI
:	Enable the UI.  When set to false, the 'Reviewers' menu is not displayed
	on the project screen. Defaults to true, or false when `enableREST` is false.


Per project configuration of the @PLUGIN@ plugin is done in the
`reviewers.config` file of the project. Missing values are inherited
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
