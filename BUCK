gerrit_plugin(
  name = 'reviewers',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Gerrit-PluginName: reviewers',
    'Gerrit-Module: com.googlesource.gerrit.plugins.reviewers.Module',
  ],
  provided_deps = [
    '//gerrit-antlr:query_exception',
    '//gerrit-antlr:query_parser',
  ],
)
