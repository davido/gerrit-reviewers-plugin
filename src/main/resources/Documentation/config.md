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

  [filter "branch:main file:^lib/*"]
    reviewer = jane.doe@example.com

  [filter "branch:stable-2.10"]
    reviewer = QAGroup

```

filter.<filter>.reviewer
:	An account (email or full user name) or a group name. Multiple
	`reviewer` occurrences are allowed.
