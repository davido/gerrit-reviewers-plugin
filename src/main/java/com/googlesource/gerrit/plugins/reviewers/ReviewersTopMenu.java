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

import com.google.common.collect.Lists;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.client.MenuItem;
import com.google.gerrit.extensions.webui.TopMenu;
import com.google.inject.Inject;

import java.util.Collections;
import java.util.List;

public class ReviewersTopMenu implements TopMenu {
  private final List<MenuEntry> menuEntries;

  @Inject
  ReviewersTopMenu(@PluginName String pluginName) {
    menuEntries = Lists.newArrayList();
    menuEntries.add(new MenuEntry("Projects", Collections.singletonList(
        new MenuItem(
            "Reviewers", "#/x/" + pluginName + "/p/${projectName}", "_self"))));
  }

  @Override
  public List<MenuEntry> getEntries() {
    return this.menuEntries;
  }
}
