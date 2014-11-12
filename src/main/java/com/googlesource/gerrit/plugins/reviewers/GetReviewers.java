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

import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.extensions.restapi.BadRequestException;
import com.google.gerrit.extensions.restapi.ResourceConflictException;
import com.google.gerrit.extensions.restapi.RestReadView;
import com.google.gerrit.server.project.ProjectResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

@Singleton
class GetReviewers implements RestReadView<ProjectResource> {
  private final ReviewersConfig.Factory configFactory;

  @Inject
  GetReviewers(ReviewersConfig.Factory configFactory) {
    this.configFactory = configFactory;
  }

  @Override
  public List<ReviewerFilterSection> apply(ProjectResource resource) throws AuthException,
      BadRequestException, ResourceConflictException, Exception {
    return configFactory.create(resource.getNameKey()).getReviewerFilterSections();
  }
}
