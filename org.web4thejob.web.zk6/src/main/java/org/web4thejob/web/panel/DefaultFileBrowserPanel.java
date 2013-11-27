/*
 * Copyright (c) 2012-2013 Veniamin Isaias.
 *
 * This file is part of web4thejob.
 *
 * Web4thejob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Web4thejob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with web4thejob.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.base.zk.AbstractZkCommandAwarePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * @author Veniamin Isaias
 * @since 3.5.2
 */
@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultFileBrowserPanel extends AbstractZkCommandAwarePanel implements FileBrowserPanel {
    private final ItemClickHandler ITEM_SELECTED_HANDLER = new ItemClickHandler();
    private final Borderlayout blayout = new Borderlayout();
    private final Tree folders = new Tree();
    private final Listbox files = new Listbox();

    public DefaultFileBrowserPanel() {
        ZkUtil.setParentOfChild((Component) base, blayout);
        new West().setParent(blayout);
        new Center().setParent(blayout);
        new East().setParent(blayout);

        blayout.getCenter().setBorder("none");

        blayout.getWest().setWidth("25%");
        blayout.getWest().setSplittable(true);
        blayout.getWest().setCollapsible(true);
        blayout.getWest().setBorder("none");
        folders.setParent(blayout.getWest());
        folders.setVflex("true");
        folders.addEventListener(Events.ON_SELECT, ITEM_SELECTED_HANDLER);

        blayout.getEast().setWidth("25%");
        blayout.getEast().setSplittable(true);
        blayout.getEast().setCollapsible(true);
        blayout.getEast().setBorder("none");
        files.setParent(blayout.getCenter());
        files.setVflex("true");

        files.setMold("paging");
        files.getPaginal().setDetailed(true);
        files.setPagingPosition("bottom");
        files.setAutopaging(true);
        files.setSpan(true);

        new Listhead().setParent(files);
        files.getListhead().setSizable(true);

        Listheader name = new Listheader("Name");
        name.setStyle("text-align:center;");
        name.setParent(files.getListhead());
        name.setSort("auto");

        Listheader size = new Listheader("Size");
        size.setStyle("text-align:center;");
        size.setParent(files.getListhead());
        size.setWidth("100px");
        size.setAlign("right");
        size.setSort("auto(2)");
        SizeComparator sizeComparator = new SizeComparator(size);
        size.setSortAscending(sizeComparator);
        size.setSortDescending(sizeComparator);

        Listheader modified = new Listheader("Modified");
        modified.setStyle("text-align:center;");
        modified.setParent(files.getListhead());
        modified.setWidth("180px");
        modified.setAlign("right");
        modified.setSort("auto(3)");
        DateComparator dateComparator = new DateComparator(modified);
        modified.setSortAscending(dateComparator);
        modified.setSortDescending(dateComparator);
    }

    @Override
    protected void registerSettings() {
        super.registerSettings();
        registerSetting(SettingEnum.ROOT_ITEM, null);
    }

    @Override
    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        if (id.equals(SettingEnum.ROOT_ITEM)) {
            if (ContextUtil.resourceExists((String) newValue)) {
                renderFolders();
            }
        } else {
            super.onSettingValueChanged(id, oldValue, newValue);
        }
    }

    protected void renderFolders() {
        if (folders.getTreechildren() == null) {
            new Treechildren().setParent(folders);
        }
        folders.getTreechildren().getChildren().clear();

        File rootFile;
        try {
            rootFile = ContextUtil.getResource(getSettingValue(SettingEnum.ROOT_ITEM, "")).getFile();
            if (!rootFile.isDirectory()) {
                throw new RuntimeException(getSettingValue(SettingEnum.ROOT_ITEM, "") + " is not a directory.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Treeitem rootItem = newTreeItem(folders.getTreechildren(), rootFile);
        renderChildFolders(rootFile, rootItem);
    }

    private Treeitem newTreeItem(Treechildren parent, File file) {
        Treeitem item = new Treeitem(file.getName());
        item.setParent(parent);
        item.setAttribute("FILE", file);
        return item;
    }

    private void renderChildFolders(File parentFile, Treeitem parentItem) {
        File[] contents = parentFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() && file.canRead();
            }
        });

        if (contents != null) {
            if (contents.length > 0) {
                if (parentItem.getTreechildren() == null) {
                    new Treechildren().setParent(parentItem);
                }
                parentItem.getTreechildren().getChildren().clear();
            }

            Arrays.sort(contents);
            for (File directory : contents) {
                Treeitem childItem = newTreeItem(parentItem.getTreechildren(), directory);
                renderChildFolders(directory, childItem);
            }
        }
    }

    protected void renderFiles(File directory) {
        files.getItems().clear();

        File[] contents = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile();
            }
        });

        if (contents != null) {
            Arrays.sort(contents);
            for (File file : contents) {
                Listitem item = newListitem(files, file);
            }
        }
    }

    private Listitem newListitem(Listbox parent, File file) {
        Listitem item = new Listitem();
        item.setValue(file);
        item.setParent(parent);
        Listcell cell;

        cell = new Listcell(file.getName());
        cell.setStyle("white-space:nowrap;");
        cell.setParent(item);

        cell = new Listcell(Long.valueOf(file.length()).toString());
        cell.setStyle("white-space:nowrap;");
        cell.setParent(item);

        cell = new Listcell(DateFormat.getDateTimeInstance().format(new Date(file.lastModified())));
        cell.setStyle("white-space:nowrap;");
        cell.setParent(item);

        return item;
    }

    private class ItemClickHandler implements EventListener<Event> {

        @Override
        public void onEvent(Event event) throws Exception {
            Treeitem item = (Treeitem) ((SelectEvent) event).getSelectedItems().iterator().next();
            renderFiles((File) item.getAttribute("FILE"));
        }
    }

    private class DateComparator implements Comparator {
        final Listheader listheader;

        public DateComparator(Listheader listheader) {
            this.listheader = listheader;
        }

        @Override
        public int compare(Object o1, Object o2) {
            File file1 = ((Listitem) o1).getValue();
            File file2 = ((Listitem) o2).getValue();

            if (listheader.getSortDirection().equals("ascending"))
                return Long.valueOf(file1.lastModified()).compareTo(file2.lastModified());
            else
                return Long.valueOf(file2.lastModified()).compareTo(file1.lastModified());
        }
    }

    private class SizeComparator implements Comparator {
        final Listheader listheader;

        public SizeComparator(Listheader listheader) {
            this.listheader = listheader;
        }

        @Override
        public int compare(Object o1, Object o2) {
            File file1 = ((Listitem) o1).getValue();
            File file2 = ((Listitem) o2).getValue();

            if (listheader.getSortDirection().equals("ascending"))
                return Long.valueOf(file1.length()).compareTo(file2.length());
            else
                return Long.valueOf(file2.length()).compareTo(file1.length());
        }
    }

}
