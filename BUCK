include_defs('//bucklets/gerrit_plugin.bucklet')

MODULE = 'com.googlesource.gerrit.plugins.reviewers.ReviewersForm'

gerrit_plugin(
  name = 'reviewers',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/**/*']),
  gwt_module = MODULE,
  manifest_entries = [
    'Gerrit-PluginName: reviewers',
    'Gerrit-Module: com.googlesource.gerrit.plugins.reviewers.Module',
    'Gerrit-HttpModule: com.googlesource.gerrit.plugins.reviewers.HttpModule',
  ]
)

java_library(
  name = 'classpath',
  deps = GERRIT_GWT_API + GERRIT_PLUGIN_API + [
    ':reviewers__plugin',
    '//lib/gwt:user',
  ],
)

