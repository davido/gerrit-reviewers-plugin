// Copyright (C) 2014 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.reviewers;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.reviewdb.client.RefNames;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.git.VersionedMetaData;
import com.google.gerrit.server.project.NoSuchProjectException;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ReviewersConfig extends VersionedMetaData {
  private static final String FILENAME = "reviewers.config";
  private static final String FILTER = "filter";
  private static final String REVIEWER = "reviewer";
  private Config cfg;

  interface Factory {
    ReviewersConfig create(Project.NameKey projectName);
  }

  @Inject
  ReviewersConfig(PluginConfigFactory cfgFactory,
      @PluginName String pluginName,
      @Assisted Project.NameKey projectName)
      throws NoSuchProjectException {
    cfg = cfgFactory.getProjectPluginConfigWithInheritance(projectName,
        pluginName);
  }

  List<ReviewerFilterSection> getReviewerFilterSections() {
    ImmutableList.Builder<ReviewerFilterSection> b = ImmutableList.builder();
    for (String f : cfg.getSubsections(FILTER)) {
      b.add(newReviewerFilterSection(f));
    }
    return b.build();
  }

  void addReviewer(String filter, String reviewer) {
    if (!newReviewerFilterSection(filter).getReviewers().contains(reviewer)) {
      List<String> values = new ArrayList<>(Arrays.asList(cfg.getStringList(
          FILTER, filter, REVIEWER)));
      values.add(reviewer);
      cfg.setStringList(FILTER, filter, REVIEWER, values);
    }
  }

  void removeReviewer(String filter, String reviewer) {
    if (newReviewerFilterSection(filter).getReviewers().contains(reviewer)) {
      List<String> values = new ArrayList<>(Arrays.asList(cfg.getStringList(
          FILTER, filter, REVIEWER)));
      values.remove(reviewer);
      if (values.isEmpty()) {
        cfg.unsetSection(FILTER, filter);
      } else {
        cfg.setStringList(FILTER, filter, REVIEWER, values);
      }
    }
  }

  private ReviewerFilterSection newReviewerFilterSection(String filter) {
    ImmutableSet.Builder<String> b = ImmutableSet.builder();
    for (String reviewer : cfg.getStringList(FILTER, filter, REVIEWER)) {
      b.add(reviewer);
    }
    return new ReviewerFilterSection(filter, b.build());
  }

  @Override
  protected String getRefName() {
    return RefNames.REFS_CONFIG;
  }

  @Override
  protected void onLoad() throws IOException, ConfigInvalidException {
    cfg = readConfig(FILENAME);
  }

  @Override
  protected boolean onSave(CommitBuilder commit) throws IOException,
      ConfigInvalidException {
    if (Strings.isNullOrEmpty(commit.getMessage())) {
      commit.setMessage("Update reviewers configuration\n");
    }
    saveConfig(FILENAME, cfg);
    return true;
  }
}
