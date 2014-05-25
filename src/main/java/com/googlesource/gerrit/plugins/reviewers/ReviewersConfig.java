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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.project.NoSuchProjectException;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.jgit.lib.Config;

import java.util.List;

class ReviewersConfig {
  private final static String FILTER = "filter";
  private final Config cfg;

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

  private ReviewerFilterSection newReviewerFilterSection(String filter) {
    ImmutableSet.Builder<String> b = ImmutableSet.builder();
    for (String reviewer : cfg.getStringList(FILTER, filter, "reviewer")) {
      b.add(reviewer);
    }
    return new ReviewerFilterSection(filter, b.build());
  }
}
