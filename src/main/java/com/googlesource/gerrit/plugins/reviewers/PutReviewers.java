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

import com.google.common.base.Objects;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.restapi.ResourceConflictException;
import com.google.gerrit.extensions.restapi.ResourceNotFoundException;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.extensions.restapi.UnprocessableEntityException;
import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.reviewdb.client.RefNames;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.account.AccountResolver;
import com.google.gerrit.server.extensions.events.GitReferenceUpdated;
import com.google.gerrit.server.git.MetaDataUpdate;
import com.google.gerrit.server.group.GroupsCollection;
import com.google.gerrit.server.project.ProjectCache;
import com.google.gerrit.server.project.ProjectResource;
import com.google.gwtorm.server.OrmException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import com.googlesource.gerrit.plugins.reviewers.PutReviewers.Input;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Singleton
class PutReviewers implements RestModifyView<ProjectResource, Input> {
  private static final Logger log = LoggerFactory
      .getLogger(PutReviewers.class);

  public static class Input {
    public Action action;
    public String filter;
    public String reviewer;
  }

  private final String pluginName;
  private final ReviewersConfig.Factory configFactory;
  private final Provider<MetaDataUpdate.User> metaDataUpdateFactory;
  private final ProjectCache projectCache;
  private final Provider<CurrentUser> currentUser;
  private final GitReferenceUpdated gitRefUpdated;
  private final AccountResolver accountResolver;
  private final Provider<GroupsCollection> groupsCollection;

  @Inject
  PutReviewers(@PluginName String pluginName,
      ReviewersConfig.Factory configFactory,
      Provider<MetaDataUpdate.User> metaDataUpdateFactory,
      ProjectCache projectCache,
      GitReferenceUpdated gitRefUpdated,
      Provider<CurrentUser> currentUser,
      AccountResolver accountResolver,
      Provider<GroupsCollection> groupsCollection) {
    this.pluginName = pluginName;
    this.configFactory = configFactory;
    this.metaDataUpdateFactory = metaDataUpdateFactory;
    this.projectCache = projectCache;
    this.gitRefUpdated = gitRefUpdated;
    this.currentUser = currentUser;
    this.accountResolver = accountResolver;
    this.groupsCollection = groupsCollection;
  }

  @Override
  public List<ReviewerFilterSection> apply(ProjectResource rsrc, Input input)
      throws RestApiException {
    Project.NameKey projectName = rsrc.getNameKey();
    ReviewersConfig cfg = configFactory.create(projectName);
    if (!rsrc.getControl().isOwner() || cfg == null) {
      throw new ResourceNotFoundException(
          "Project" + projectName.get() + " not found");
    }

    try (MetaDataUpdate md = metaDataUpdateFactory.get().create(projectName)) {
      if (input.action == Action.ADD) {
        validateReviewer(input.reviewer);
      }
      try {
        StringBuilder message = new StringBuilder(pluginName)
            .append(" plugin: ");
        cfg.load(md);
        if (input.action == Action.ADD) {
          message.append("Add reviewer ")
            .append(input.reviewer)
            .append(" to filter ")
            .append(input.filter);
          cfg.addReviewer(input.filter, input.reviewer);
        } else {
          message.append("Remove reviewer ")
            .append(input.reviewer)
            .append(" from filter ")
            .append(input.filter);
          cfg.removeReviewer(input.filter, input.reviewer);
        }
        message.append("\n");
        md.setMessage(message.toString());
        try {
          ObjectId baseRev = cfg.getRevision();
          ObjectId commitRev = cfg.commit(md);
          // Only fire hook if project was actually changed.
          if (!Objects.equal(baseRev, commitRev)) {
            IdentifiedUser user = (IdentifiedUser) currentUser.get();
            gitRefUpdated.fire(projectName, RefNames.REFS_CONFIG, baseRev,
                commitRev, user.getAccount());
          }
          projectCache.evict(projectName);
        } catch (IOException e) {
          if (e.getCause() instanceof ConfigInvalidException) {
            throw new ResourceConflictException("Cannot update " + projectName
                + ": " + e.getCause().getMessage());
          }
          throw new ResourceConflictException("Cannot update " + projectName);
        }
      } catch (ConfigInvalidException err) {
        throw new ResourceConflictException("Cannot read " + pluginName
            + " configurations for project " + projectName, err);
      } catch (IOException err) {
        throw new ResourceConflictException("Cannot update " + pluginName
            + " configurations for project " + projectName, err);
      }
    } catch (RepositoryNotFoundException err) {
      throw new ResourceNotFoundException(projectName.get());
    } catch (IOException err) {
      throw new ResourceNotFoundException(projectName.get(), err);
    }
    return cfg.getReviewerFilterSections();
  }

  private void validateReviewer(String reviewer) throws RestApiException {
    try {
      Account account = accountResolver.find(reviewer);
      if (account == null) {
        try {
          groupsCollection.get().parse(reviewer);
        } catch (UnprocessableEntityException e) {
          throw new ResourceNotFoundException(
              "Account or group " + reviewer + " not found");
        }
      }
    } catch (OrmException e) {
      log.error("Failed to resolve account " + reviewer);
    }
  }
}
