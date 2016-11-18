workspace(name = "reviewers")

load("//:bazlets.bzl", "load_bazlets")

load_bazlets(
    commit = "3bec81727c69207e591ae1761d5a78d8ec418a0b",
    #    local_path = "/home/<user>/projects/bazlets",
)

# Release Plugin API
#load("@com_googlesource_gerrit_bazlets//:gerrit_api.bzl",
#     "gerrit_api")

# Snapshot Plugin API
load(
    "@com_googlesource_gerrit_bazlets//:gerrit_api_maven_local.bzl",
    "gerrit_api_maven_local",
)
load(
    "@com_googlesource_gerrit_bazlets//:gerrit_gwt.bzl",
    "gerrit_gwt",
)

# Load release Plugin API
# gerrit_api()

# Load snapshot Plugin API
gerrit_api_maven_local()

gerrit_gwt()
