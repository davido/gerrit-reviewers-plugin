API_VERSION = '2.9-SNAPSHOT'
REPO = MAVEN_LOCAL

gerrit_plugin(
  name = 'reviewers',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Gerrit-PluginName: reviewers',
    'Gerrit-Module: com.googlesource.gerrit.plugins.reviewers.Module',
  ]
)

maven_jar(
  name = 'plugin-lib',
  id = 'com.google.gerrit:gerrit-plugin-api:' + API_VERSION,
  repository = REPO,
)
