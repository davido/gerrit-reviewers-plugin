@PLUGIN@ - /reviewers/ REST API
===============================

This page describes the REST endpoints that are added by the @PLUGIN@ plugin.

Please also take note of the general information on the
[REST API](../../../Documentation/rest-api.html).

<a id="project-endpoints"> Reviewers Endpoints
----------------------------------------------

### <a id="get-reviewers"> Get Reviewers
_GET /projects/project_name/@PLUGIN@_

Gets the default reviewers for specified project.

#### Request

```
  GET /projects/myproject/@PLUGIN@ HTTP/1.0
```

As response a List of [ReviewerFilterSection](#reviewer-filter-section) is returned
that describes the default reviewers for myproject.

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json;charset=UTF-8
  )]}'
  [
    {
      "filter": "branch:master",
      "reviewers": [
        "UserA",
        "UserB"
      ]
    },
    {
      "filter": "file:^lib/*",
      "reviewers": [
        "UserB",
        "UserC"
      ]
    }
  ]
```

### <a id="change-reviewers"> Change Reviewers
_PUT /projects/project_name/@PLUGIN@_

Changes the default reviewers for the specified project.

The change to reviewers must be provided in the request body inside
a [ConfigReviewersInput](#config-reviewers-input) entity.

#### Request

```
  PUT /projects/myproject/@PLUGIN@ HTTP/1.0
  Content-Type: application/json;charset=UTF-8
  {
    "action": "ADD",
    "filter": "branch:master"
    "reviewer": "UserA"
  }
```

As response the default reviewers are returned as a list of
[ReviewerFilterSection](#reviewer-filter-section).

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json;charset=UTF-8
  )]}'
  [
    {
      "filter": "branch:master",
      "reviewers": [
        "UserA",
        "UserB"
      ]
    },
    {
      "filter": "file:^lib/*",
      "reviewers": [
        "UserB",
        "UserC"
      ]
    }
  ]

```


<a id="json-entities">JSON Entities
-----------------------------------

### <a id="reviewer-filter-section"></a>ReviewerFilterSection

The `ReviewerFilterSection` entity contains a filter section of the
default reviewers.

* _filter_: A filter that is used to assign default reviewers.
* _reviewers_: List of usernames which are assigned as default reviewers
 under the filter.

### <a id="config-reviewers-input"></a>ConfigReviewersInput

The `ConfigReviewersInput` entity contains an update for the default
reviewers.

* _action_: Indicates whether to add or remove the input reviewer
* _filter_: The filter associated with the input reviewer.
* _reviewer_: The user to add or remove from the default reviewers.

GERRIT
------
Part of [Gerrit Code Review](../../../Documentation/index.html)
