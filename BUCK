include_defs('//bucklets/gerrit_plugin.bucklet')

gerrit_plugin(
  name = 'reviewers',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Gerrit-PluginName: reviewers',
    'Gerrit-Module: com.googlesource.gerrit.plugins.reviewers.Module',
  ],
)

java_library(
  name = 'classpath',
  deps = [':reviewers__plugin'],
)

