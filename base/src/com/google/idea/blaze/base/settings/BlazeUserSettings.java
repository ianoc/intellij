/*
 * Copyright 2016 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.base.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.EditorNotifications;
import com.intellij.util.xmlb.XmlSerializerUtil;

/** Stores blaze view settings. */
@State(
  name = "BlazeUserSettings",
  storages = {
    @Storage("blaze.user.settings.xml"),
    @Storage(value = "blaze.view.xml", deprecated = true)
  }
)
public class BlazeUserSettings implements PersistentStateComponent<BlazeUserSettings> {

  /**
   * A setting to control whether the Blaze Console / Blaze Problems view is activated for a given
   * operation.
   */
  public enum FocusBehavior {
    ALWAYS("Always"),
    ON_ERROR("On error"),
    NEVER("Never");

    private final String uiName;

    FocusBehavior(String uiName) {
      this.uiName = uiName;
    }

    @Override
    public String toString() {
      return uiName;
    }
  }

  private static final String DEFAULT_BLAZE_PATH =
      SystemInfo.isMac ? "/usr/local/bin/blaze" : "/usr/bin/blaze";
  private static final String DEFAULT_BAZEL_PATH = "bazel";

  private FocusBehavior showBlazeConsoleOnSync = FocusBehavior.ALWAYS;
  private FocusBehavior showBlazeProblemsViewOnSync = FocusBehavior.ALWAYS;
  private FocusBehavior showBlazeConsoleOnRun = FocusBehavior.ALWAYS;
  private FocusBehavior showProblemsViewOnRun = FocusBehavior.NEVER;
  @Deprecated private boolean suppressConsoleForRunAction = false;
  @Deprecated private boolean showProblemsViewForRunAction = false;
  private boolean resyncAutomatically = false;
  private boolean syncStatusPopupShown = false;
  private boolean expandSyncToWorkingSet = true;
  private boolean showPerformanceWarnings = false;
  private boolean collapseProjectView = true;
  private boolean formatBuildFilesOnSave = true;
  private boolean showAddFileToProjectNotification = true;
  private String blazeBinaryPath = DEFAULT_BLAZE_PATH;
  private String bazelBinaryPath = DEFAULT_BAZEL_PATH;

  public static BlazeUserSettings getInstance() {
    return ServiceManager.getService(BlazeUserSettings.class);
  }

  @Override
  public BlazeUserSettings getState() {
    return this;
  }

  @Override
  public void loadState(BlazeUserSettings state) {
    XmlSerializerUtil.copyBean(state, this);
    // migrate the old settings, then reset them to the defaults
    // TODO(brendandouglas): remove the old settings in September 2018
    if (suppressConsoleForRunAction) {
      showBlazeConsoleOnRun = FocusBehavior.NEVER;
      suppressConsoleForRunAction = false;
    }
    if (showProblemsViewForRunAction) {
      showProblemsViewOnRun = FocusBehavior.ALWAYS;
      showProblemsViewForRunAction = false;
    }
  }

  public void setResyncAutomatically(boolean resyncAutomatically) {
    this.resyncAutomatically = resyncAutomatically;
  }

  public boolean getResyncAutomatically() {
    return resyncAutomatically;
  }

  public FocusBehavior getShowBlazeConsoleOnSync() {
    return showBlazeConsoleOnSync;
  }

  public void setShowBlazeConsoleOnSync(FocusBehavior showBlazeConsoleOnSync) {
    this.showBlazeConsoleOnSync = showBlazeConsoleOnSync;
  }

  public FocusBehavior getShowProblemsViewOnSync() {
    return showBlazeProblemsViewOnSync;
  }

  public void setShowProblemsViewOnSync(FocusBehavior showProblemsViewOnSync) {
    this.showBlazeProblemsViewOnSync = showProblemsViewOnSync;
  }

  public FocusBehavior getShowBlazeConsoleOnRun() {
    return showBlazeConsoleOnRun;
  }

  public void setShowBlazeConsoleOnRun(FocusBehavior showBlazeConsoleOnRun) {
    this.showBlazeConsoleOnRun = showBlazeConsoleOnRun;
  }

  public FocusBehavior getShowProblemsViewOnRun() {
    return showProblemsViewOnRun;
  }

  public void setShowProblemsViewOnRun(FocusBehavior showProblemsViewForRunAction) {
    this.showProblemsViewOnRun = showProblemsViewForRunAction;
  }

  /**
   * @deprecated DO NOT USE: left here temporarily while migrating to a new settings format. Use
   *     {@link #getShowBlazeConsoleOnRun} instead.
   */
  @Deprecated
  public boolean getSuppressConsoleForRunAction() {
    return suppressConsoleForRunAction;
  }

  /**
   * @deprecated DO NOT USE: left here temporarily while migrating to a new settings format. Use
   *     {@link #setShowBlazeConsoleOnRun} instead.
   */
  @Deprecated
  public void setSuppressConsoleForRunAction(boolean suppressConsoleForRunAction) {
    this.suppressConsoleForRunAction = suppressConsoleForRunAction;
  }

  /**
   * @deprecated DO NOT USE: left here temporarily while migrating to a new settings format. Use
   *     {@link #getShowProblemsViewOnRun} instead.
   */
  @Deprecated
  public boolean getShowProblemsViewForRunAction() {
    return showProblemsViewForRunAction;
  }

  /**
   * @deprecated DO NOT USE: left here temporarily while migrating to a new settings format. Use
   *     {@link #setShowProblemsViewOnRun} instead.
   */
  @Deprecated
  public void setShowProblemsViewForRunAction(boolean showProblemsViewForRunAction) {
    this.showProblemsViewForRunAction = showProblemsViewForRunAction;
  }

  public boolean getSyncStatusPopupShown() {
    return syncStatusPopupShown;
  }

  public void setSyncStatusPopupShown(boolean syncStatusPopupShown) {
    this.syncStatusPopupShown = syncStatusPopupShown;
  }

  public boolean getExpandSyncToWorkingSet() {
    return expandSyncToWorkingSet;
  }

  public void setExpandSyncToWorkingSet(boolean expandSyncToWorkingSet) {
    this.expandSyncToWorkingSet = expandSyncToWorkingSet;
  }

  public boolean getShowPerformanceWarnings() {
    return showPerformanceWarnings;
  }

  public void setShowPerformanceWarnings(boolean showPerformanceWarnings) {
    this.showPerformanceWarnings = showPerformanceWarnings;
  }

  public String getBlazeBinaryPath() {
    return StringUtil.defaultIfEmpty(blazeBinaryPath, DEFAULT_BLAZE_PATH).trim();
  }

  public void setBlazeBinaryPath(String blazeBinaryPath) {
    this.blazeBinaryPath = StringUtil.defaultIfEmpty(blazeBinaryPath, DEFAULT_BLAZE_PATH).trim();
  }

  public String getBazelBinaryPath() {
    return StringUtil.defaultIfEmpty(bazelBinaryPath, DEFAULT_BAZEL_PATH).trim();
  }

  public void setBazelBinaryPath(String bazelBinaryPath) {
    this.bazelBinaryPath = StringUtil.defaultIfEmpty(bazelBinaryPath, DEFAULT_BAZEL_PATH).trim();
  }

  public boolean getCollapseProjectView() {
    return collapseProjectView;
  }

  public void setCollapseProjectView(boolean collapseProjectView) {
    this.collapseProjectView = collapseProjectView;
  }

  public boolean getFormatBuildFilesOnSave() {
    return formatBuildFilesOnSave;
  }

  public void setFormatBuildFilesOnSave(boolean formatBuildFilesOnSave) {
    this.formatBuildFilesOnSave = formatBuildFilesOnSave;
  }

  public boolean getShowAddFileToProjectNotification() {
    return showAddFileToProjectNotification;
  }

  public void setShowAddFileToProjectNotification(boolean showAddFileToProjectNotification) {
    if (this.showAddFileToProjectNotification == showAddFileToProjectNotification) {
      return;
    }
    this.showAddFileToProjectNotification = showAddFileToProjectNotification;
    for (Project project : ProjectManager.getInstance().getOpenProjects()) {
      EditorNotifications.getInstance(project).updateAllNotifications();
    }
  }
}