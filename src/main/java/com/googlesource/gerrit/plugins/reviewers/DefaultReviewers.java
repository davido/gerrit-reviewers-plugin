// Copyright (C) 2013 The Android Open Source Project
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

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.reviewdb.client.Change;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.change.ChangeResource;
import com.google.gerrit.server.change.PostReviewers;
import com.google.gerrit.server.project.ChangeControl;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

public class DefaultReviewers implements Runnable {
  private static final Logger log = LoggerFactory
      .getLogger(DefaultReviewers.class);

  private final Change change;
  private final Set<Account> reviewers;
  private final Provider<PostReviewers> reviewersProvider;
  private final IdentifiedUser.GenericFactory identifiedUserFactory;
  private final ChangeControl.GenericFactory changeControlFactory;

  public interface Factory {
    DefaultReviewers create(Change change,
        Set<Account> reviewers);
  }

  @Inject
  public DefaultReviewers(
      ChangeControl.GenericFactory changeControlFactory,
      Provider<PostReviewers> reviewersProvider,
      IdentifiedUser.GenericFactory identifiedUserFactory,
      @Assisted Change change, @Assisted Set<Account> reviewers) {
    this.change = change;
    this.reviewers = reviewers;
    this.reviewersProvider = reviewersProvider;
    this.identifiedUserFactory = identifiedUserFactory;
    this.changeControlFactory = changeControlFactory;
  }

  @Override
  public void run() {
    addReviewers(reviewers, change);
  }

  /**
   * Append the reviewers to change#{@link Change}
   *
   * @param topReviewers Set of reviewers proposed
   * @param change {@link Change} to add the reviewers to
   */
  private void addReviewers(Set<Account> reviewers, Change change) {
    try {
      ChangeControl changeControl =
          changeControlFactory.controlFor(change,
              identifiedUserFactory.create(change.getOwner()));
      ChangeResource changeResource = new ChangeResource(changeControl);
      PostReviewers post = reviewersProvider.get();
      for (Account account : reviewers) {
        PostReviewers.Input input = new PostReviewers.Input();
        input.reviewer = account.getId().toString();
        post.apply(changeResource, input);
      }
    } catch (Exception ex) {
      log.error("Couldn't add reviewers to the change", ex);
    }
  }
}
