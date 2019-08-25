package com.mega.matrimony.Model;

import java.util.List;

public class MenuGroup {
    public String menuName,clickTag;
    public int icon;
    public List<MenuChild> children;

    public MenuGroup(String menuName, int icon, List<MenuChild> children,String clickTag) {
        this.menuName = menuName;
        this.icon = icon;
        this.children = children;
        this.clickTag = clickTag;
    }

    public String getClickTag() {
        return clickTag;
    }

    public void setClickTag(String clickTag) {
        this.clickTag = clickTag;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public List<MenuChild> getChildren() {
        return children;
    }

    public void setChildren(List<MenuChild> children) {
        this.children = children;
    }
}
