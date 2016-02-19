// Copyright (C) 2016 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.reviewers.client;

import com.google.gerrit.client.rpc.NativeMap;
import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gerrit.client.rpc.Natives;
import com.google.gerrit.client.ui.HighlightSuggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/** A {@code SuggestOracle} for reviewers. */
public class ReviewerSuggestOracle extends SuggestOracle {
  private static final String ACCOUNT_KEY = "account";
  private static final String GROUP_KEY = "group";
  private static final String NAME_KEY = "name";
  private static final String EMAIL_KEY = "email";
  private static final String ACCOUNT_ID_KEY = "_account_id";

  private final int chars;
  private final String projectName;

  /**
   * @param chars minimum chars to start suggesting.
   * @param projectName the name of the project to check visibility
   */
  public ReviewerSuggestOracle(int chars, String projectName) {
    this.chars = chars;
    this.projectName = projectName;
  }

  @Override
  public boolean isDisplayStringHTML() {
    return true;
  }

  private class ReviewerSuggestion extends HighlightSuggestion {
    private final String name;

    ReviewerSuggestion(String query, String groupName) {
      super(query, groupName + " (group)");
      this.name = groupName;
    }

    ReviewerSuggestion(String query, String fullname, String email,
        String accountId) {
      super(query, fullname
          + ((!email.isEmpty()) ? " <" + email + ">" : " (" + accountId + ")"));
      this.name = fullname;
    }

    @Override
    public String getReplacementString() {
      return name;
    }
  }

  @Override
  public void requestSuggestions(final Request req, final Callback done) {
    if (req.getQuery().length() < chars) {
      responseEmptySuggestion(req, done);
      return;
    }
    RestApi rest = new RestApi("/projects/").id(projectName).view("suggest_reviewers");
    rest.addParameter("q", req.getQuery());
    if (req.getLimit() > 0) {
      rest.addParameter("n", req.getLimit());
    }
    rest.get(new AsyncCallback<NativeMap<JavaScriptObject>>() {
      @Override
      public void onSuccess(NativeMap<JavaScriptObject> result) {
        List<String> keys0 = result.sortedKeys();
        List<Suggestion> suggestions = new ArrayList<>(keys0.size());
        for (String key0 : keys0) {
          Set<String> keys1 = Natives.keys(result.get(key0));
          NativeMap<JavaScriptObject> map1 = result.get(key0).cast();
          for (String key1 : keys1) {
            NativeMap<JavaScriptObject> map2 = map1.get(key1).cast();
            String name =  map2.get(NAME_KEY).toString();
            if (ACCOUNT_KEY.equals(key1)) {
              String email = (map2.containsKey(EMAIL_KEY))
                  ? map2.get(EMAIL_KEY).toString() : "";
              String accountId = map2.get(ACCOUNT_ID_KEY).toString();
              suggestions
                  .add(new ReviewerSuggestion(req.getQuery(), name, email, accountId));
            } else if (GROUP_KEY.equals(key1)) {
              suggestions.add(new ReviewerSuggestion(req.getQuery(), name));
            }
          }
        }
        done.onSuggestionsReady(req, new Response(suggestions));
      }

      @Override
      public void onFailure(Throwable caught) {
        responseEmptySuggestion(req, done);
      }
    });
  }

  private static void responseEmptySuggestion(Request req, Callback done) {
    List<Suggestion> empty = Collections.emptyList();
    done.onSuggestionsReady(req, new Response(empty));
  }
}
