package com.android.visitlog.DialogFileMenu;

class DirEntry {
  private String name;
  private boolean isDir;
  private boolean levelUp;

  DirEntry() {
    this.isDir = false;
    this.levelUp = true;
  }

  DirEntry(String name, boolean isDir) {
    this.name = name;
    this.isDir = isDir;
    this.levelUp = false;
  }

  String getName() {
    return name;
  }

  boolean isDir() {
    return isDir;
  }

  boolean isLevelUp() {
    return levelUp;
  }
}
