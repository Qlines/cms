package smsgateway.webadmin.bean;

import hippoping.smsgw.api.db.Group;
import hippoping.smsgw.api.db.User;

public class TabMenuAccess {

    protected String tabs;
    protected String links;
    protected String titles;

    public TabMenuAccess(String[][] menu, User user) {
        try {
            Group group = new Group(user.getGid());

            this.tabs = (this.links = this.titles = "");

            for (int i = 0; i < menu.length; i++) {
                if ((group.isAllowPages(menu[i][2])) && (!group.isBlockPages(menu[i][2]))) {
                    this.tabs = (this.tabs + "'" + menu[i][0] + "',");
                    this.titles = (this.titles + "'" + menu[i][1] + "',");
                    this.links = (this.links + "'" + menu[i][2] + "',");
                }
            }
            if (!this.tabs.isEmpty()) {
                this.tabs = this.tabs.substring(0, this.tabs.length() - 1);
            }
            if (!this.titles.isEmpty()) {
                this.titles = this.titles.substring(0, this.titles.length() - 1);
            }
            if (!this.links.isEmpty()) {
                this.links = this.links.substring(0, this.links.length() - 1);
            }
        } catch (Exception e) {
        }
    }

    public String getTitles() {
        return this.titles;
    }

    public String getLinks() {
        return this.links;
    }

    public String getTabs() {
        return this.tabs;
    }
}