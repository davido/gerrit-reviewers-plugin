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

package com.googlesource.gerrit.plugins.reviewers.client;

import com.google.gerrit.client.rpc.NativeMap;
import com.google.gerrit.client.rpc.Natives;
import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gerrit.plugin.client.screen.Screen;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ReviewersScreen extends HorizontalPanel {
  private static final String REMOVE_BUTTON_IMG =
      "plugins/reviewers/static/remove_reviewer.png";
  static class Factory implements Screen.EntryPoint {
    @Override
    public void onLoad(Screen screen) {
      screen.setPageTitle("Reviewers");
      screen.show(new ReviewersScreen(URL.decodeQueryString(screen.getToken())));
    }
  }

  private boolean isOwner;
  private String projectName;
  private Set<ReviewerEntry> rEntries;

  ReviewersScreen(final String projectName) {
    setStyleName("reviewers-panel");
    this.projectName = projectName;
    this.rEntries = new HashSet<>();

    new RestApi("access/").addParameter("project", projectName).get(
        new AsyncCallback<NativeMap<ProjectAccessInfo>>() {

        @Override
        public void onSuccess(NativeMap<ProjectAccessInfo> result) {
          isOwner = result.get(projectName).isOwner();
          display();
        }

        @Override
        public void onFailure(Throwable caught) {
        }
      });
  }

  void display() {
    new RestApi("projects").id(projectName).view("reviewers").get(
        new AsyncCallback<JsArray<ReviewerFilterSection>>() {

      @Override
      public void onSuccess(JsArray<ReviewerFilterSection> result) {
        display(result);
      }

      @Override
      public void onFailure(Throwable caught) {
      }
    });
  }

  void display(JsArray<ReviewerFilterSection> sections) {
    add(createEntriesPanel(sections));
    add(createInputPanel());
  }

  Panel createEntriesPanel(JsArray<ReviewerFilterSection> sections) {
    Panel p = new VerticalPanel();
    for (ReviewerFilterSection section : Natives.asList(sections)) {
      Label filter = new Label(section.filter());
      filter.addStyleName("reviewers-filterLabel");
      p.add(filter);
      for (String reviewer : Natives.asList(section.reviewers())) {
        ReviewerEntry rEntry = new ReviewerEntry(section.filter(), reviewer);
        rEntries.add(rEntry);
        p.add(createOneEntry(rEntry));
      }
    }
    return p;
  }

  Panel createOneEntry(final ReviewerEntry e) {
    Label l = new Label(e.reviewer);
    l.setStyleName("reviewers-reviewerLabel");

    Image img = new Image(REMOVE_BUTTON_IMG);
    Button removeButton = Button.wrap(img.getElement());
    removeButton.setStyleName("reviewers-removeButton");
    removeButton.setTitle("remove reviewer");
    removeButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(final ClickEvent event) {
        doSave(Action.REMOVE, e);
      }
    });
    removeButton.setVisible(isOwner);

    HorizontalPanel p = new HorizontalPanel();
    p.add(l);
    p.add(removeButton);
    return p;
  }

  Panel createInputPanel(){
    final TextBox filterBox = new TextBox();
    final TextBox reviewerBox = new TextBox();
    filterBox.getElement().setPropertyString("placeholder", "filter");
    reviewerBox.getElement().setPropertyString("placeholder", "reviewer");

    Button addButton = new Button("Add");
    addButton.setStyleName("reviewers-addButton");
    addButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(final ClickEvent event) {
        ReviewerEntry e = new ReviewerEntry(filterBox.getValue(),
            reviewerBox.getValue());
        if (!rEntries.contains(e) && !e.filter.isEmpty() &&
            !e.reviewer.isEmpty()) {
          doSave(Action.ADD, e);
        }
        filterBox.setText("");
        reviewerBox.setText("");
      }
    });
    filterBox.setEnabled(isOwner);
    reviewerBox.setEnabled(isOwner);
    addButton.setEnabled(isOwner);

    Panel p = new VerticalPanel();
    p.setStyleName("reviewers-inputPanel");
    p.add(filterBox);
    p.add(reviewerBox);
    p.add(addButton);
    return p;
  }

  void doSave(Action action, ReviewerEntry entry) {
    ChangeReviewersInput in = ChangeReviewersInput.create();
    in.setAction(action);
    in.setFilter(entry.filter);
    in.setReviewer(entry.reviewer);
    reset();

    new RestApi("projects").id(projectName).view("reviewers").put(
        in, new AsyncCallback<JsArray<ReviewerFilterSection>>() {

      @Override
      public void onSuccess(JsArray<ReviewerFilterSection> result) {
        display(result);
      }

      @Override
      public void onFailure(Throwable caught) {
      }
    });
  }

  void reset() {
    clear();
    rEntries = new HashSet<>();
  }

  static class ReviewerEntry {
    private String filter;
    private String reviewer;

    ReviewerEntry(String filter, String reviewer) {
      this.filter = filter;
      this.reviewer = reviewer;
    }

    @Override
    public int hashCode() {
      return Objects.hash(filter, reviewer);
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || !(o instanceof ReviewerEntry)) {
        return false;
      }
      ReviewerEntry other = (ReviewerEntry) o;
      if (!this.filter.equals(other.filter)
          || !this.reviewer.equals(other.reviewer)) {
        return false;
      }
      return true;
    }
  }
}
