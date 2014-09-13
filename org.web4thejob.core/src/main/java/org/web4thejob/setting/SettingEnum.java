/*
 * Copyright (c) 2012-2014 Veniamin Isaias.
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

package org.web4thejob.setting;

import org.web4thejob.orm.Entity;
import org.web4thejob.orm.scheme.SchemeType;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.dialog.SettingsDialog;

import java.util.*;

/**
 * <p>The eligible setting ids of the framework.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class SettingEnum implements Comparable<SettingEnum> {
    SettingEnum(String name, Class<?> type) {
        this(name, type, SettingsDialog.L10N_CATEGORY_0000_GENERAL);
    }

    SettingEnum(String name, Class<?> type, L10nString category) {
        this(name, type, null, category);
    }

    SettingEnum(String name, Class<?> type, Class<?> subType, L10nString category) {
        this.name = name;
        this.type = type;
        this.subType = subType;
        this.category = category;

        this.ordinal = addToRegistry(this);
    }

    SettingEnum(String name, Class<?> type, Class<?> subType) {
        this.name = name;
        this.type = type;
        this.subType = subType;
        this.category = SettingsDialog.L10N_CATEGORY_0000_GENERAL;

        this.ordinal = addToRegistry(this);
    }

    public static final SettingEnum OBSOLETE_SETTING_PLACEHOLDER = new SettingEnum("OBSOLETE_SETTING_PLACEHOLDER",
            String.class);
    public static final SettingEnum TARGET_TYPE = new SettingEnum("TARGET_TYPE", Class.class, Entity.class);
    public static final SettingEnum MASTER_TYPE = new SettingEnum("MASTER_TYPE", Class.class, Entity.class);
    public static final SettingEnum PANEL_NAME = new SettingEnum("PANEL_NAME", String.class);
    public static final SettingEnum BIND_PROPERTY = new SettingEnum("BIND_PROPERTY", String.class);
    public static final SettingEnum HTML_PROPERTY = new SettingEnum("HTML_PROPERTY", String.class);
    public static final SettingEnum URL_PROPERTY = new SettingEnum("URL_PROPERTY", String.class);
    public static final SettingEnum MEDIA_PROPERTY = new SettingEnum("MEDIA_PROPERTY", String.class);
    public static final SettingEnum SUPRESS_COMMANDS = new SettingEnum("SUPRESS_COMMANDS", Boolean.class);
    public static final SettingEnum SCHEME_TYPE = new SettingEnum("SCHEME_TYPE", SchemeType.class);
    public static final SettingEnum PANEL_STYLE = new SettingEnum("PANEL_STYLE", String.class);
    public static final SettingEnum ASSUME_DETAIL_BEHAVIOR = new SettingEnum("ASSUME_DETAIL_BEHAVIOR", Boolean.class);
    public static final SettingEnum MOLD = new SettingEnum("MOLD", String.class);
    public static final SettingEnum SCLASS = new SettingEnum("SCLASS", String.class);
    public static final SettingEnum RENDER_SCHEME_FOR_VIEW = new SettingEnum("RENDER_SCHEME_FOR_VIEW", String.class);
    public static final SettingEnum RENDER_SCHEME_FOR_UPDATE = new SettingEnum("RENDER_SCHEME_FOR_UPDATE",
            String.class);
    public static final SettingEnum RENDER_SCHEME_FOR_INSERT = new SettingEnum("RENDER_SCHEME_FOR_INSERT",
            String.class);
    public static final SettingEnum PERSISTED_QUERY_DIALOG = new SettingEnum("PERSISTED_QUERY_DIALOG", String.class);
    public static final SettingEnum PERSISTED_QUERY_NAME = new SettingEnum("PERSISTED_QUERY_NAME", String.class);
    public static final SettingEnum RUN_QUERY_ON_STARTUP = new SettingEnum("RUN_QUERY_ON_STARTUP", Boolean.class);
    public static final SettingEnum TARGET_URL = new SettingEnum("TARGET_URL", String.class);
    public static final SettingEnum DISPATCH_DOUBLE_CLICK = new SettingEnum("DISPATCH_DOUBLE_CLICK", Boolean.class);
    public static final SettingEnum CHILDREN_COUNT = new SettingEnum("CHILDREN_COUNT", Integer.class,
            SettingsDialog.L10N_CATEGORY_1000_TABS);
    public static final SettingEnum HONOR_ADOPTION_REQUEST = new SettingEnum("HONOR_ADOPTION_REQUEST", Boolean.class,
            SettingsDialog.L10N_CATEGORY_1000_TABS);
    public static final SettingEnum SELECTED_INDEX = new SettingEnum("SELECTED_INDEX", Integer.class,
            SettingsDialog.L10N_CATEGORY_1000_TABS);
    public static final SettingEnum SHOW_STARTUP_TAB = new SettingEnum("SHOW_STARTUP_TAB", Boolean.class,
            SettingsDialog.L10N_CATEGORY_1000_TABS);
    public static final SettingEnum CLOSEABLE_TABS = new SettingEnum("CLOSEABLE_TABS", Boolean.class,
            SettingsDialog.L10N_CATEGORY_1000_TABS);
    public static final SettingEnum FIXED_TABS = new SettingEnum("FIXED_TABS", Integer.class,
            SettingsDialog.L10N_CATEGORY_1000_TABS);
    public static final SettingEnum DISABLE_DYNAMIC_TAB_TITLE = new SettingEnum("DISABLE_DYNAMIC_TAB_TITLE",
            Boolean.class,
            SettingsDialog.L10N_CATEGORY_1000_TABS);
    public static final SettingEnum DISABLE_CROSS_TAB_BINDING = new SettingEnum("DISABLE_CROSS_TAB_BINDING",
            Boolean.class,
            SettingsDialog.L10N_CATEGORY_1000_TABS);
    public static final SettingEnum NORTH_ENABLED = new SettingEnum("NORTH_ENABLED", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2000_NORTH);
    public static final SettingEnum NORTH_OPEN = new SettingEnum("NORTH_OPEN", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2000_NORTH);
    public static final SettingEnum NORTH_HEIGHT = new SettingEnum("NORTH_HEIGHT", String.class,
            SettingsDialog.L10N_CATEGORY_2000_NORTH);
    public static final SettingEnum NORTH_SPLITTABLE = new SettingEnum("NORTH_SPLITTABLE", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2000_NORTH);
    public static final SettingEnum NORTH_COLLAPSIBLE = new SettingEnum("NORTH_COLLAPSIBLE", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2000_NORTH);
    public static final SettingEnum NORTH_MERGE_COMMANDS = new SettingEnum("NORTH_MERGE_COMMANDS", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2000_NORTH);
    public static final SettingEnum NORTH_EXCLUDE_CRUD_COMMANDS = new SettingEnum("NORTH_EXCLUDE_CRUD_COMMANDS",
            Boolean.class,
            SettingsDialog.L10N_CATEGORY_2000_NORTH);
    public static final SettingEnum NORTH_CHILD_INDEX = new SettingEnum("NORTH_CHILD_INDEX", Integer.class,
            SettingsDialog.L10N_CATEGORY_2000_NORTH);
    public static final SettingEnum SOUTH_ENABLED = new SettingEnum("SOUTH_ENABLED", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2100_SOUTH);
    public static final SettingEnum SOUTH_OPEN = new SettingEnum("SOUTH_OPEN", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2100_SOUTH);
    public static final SettingEnum SOUTH_HEIGHT = new SettingEnum("SOUTH_HEIGHT", String.class,
            SettingsDialog.L10N_CATEGORY_2100_SOUTH);
    public static final SettingEnum SOUTH_SPLITTABLE = new SettingEnum("SOUTH_SPLITTABLE", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2100_SOUTH);
    public static final SettingEnum SOUTH_COLLAPSIBLE = new SettingEnum("SOUTH_COLLAPSIBLE", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2100_SOUTH);
    public static final SettingEnum SOUTH_MERGE_COMMANDS = new SettingEnum("SOUTH_MERGE_COMMANDS", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2100_SOUTH);
    public static final SettingEnum SOUTH_EXCLUDE_CRUD_COMMANDS = new SettingEnum("SOUTH_EXCLUDE_CRUD_COMMANDS",
            Boolean.class,
            SettingsDialog.L10N_CATEGORY_2100_SOUTH);
    public static final SettingEnum SOUTH_CHILD_INDEX = new SettingEnum("SOUTH_CHILD_INDEX", Integer.class,
            SettingsDialog.L10N_CATEGORY_2100_SOUTH);
    public static final SettingEnum CENTER_ENABLED = new SettingEnum("CENTER_ENABLED", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2200_CENTER);
    public static final SettingEnum CENTER_MERGE_COMMANDS = new SettingEnum("CENTER_MERGE_COMMANDS", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2200_CENTER);
    public static final SettingEnum CENTER_EXCLUDE_CRUD_COMMANDS = new SettingEnum("CENTER_EXCLUDE_CRUD_COMMANDS",
            Boolean.class,
            SettingsDialog.L10N_CATEGORY_2200_CENTER);
    public static final SettingEnum CENTER_CHILD_INDEX = new SettingEnum("CENTER_CHILD_INDEX", Integer.class,
            SettingsDialog.L10N_CATEGORY_2200_CENTER);
    public static final SettingEnum WEST_ENABLED = new SettingEnum("WEST_ENABLED", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2300_WEST);
    public static final SettingEnum WEST_OPEN = new SettingEnum("WEST_OPEN", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2300_WEST);
    public static final SettingEnum WEST_WIDTH = new SettingEnum("WEST_WIDTH", String.class,
            SettingsDialog.L10N_CATEGORY_2300_WEST);
    public static final SettingEnum WEST_SPLITTABLE = new SettingEnum("WEST_SPLITTABLE", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2300_WEST);
    public static final SettingEnum WEST_COLLAPSIBLE = new SettingEnum("WEST_COLLAPSIBLE", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2300_WEST);
    public static final SettingEnum WEST_MERGE_COMMANDS = new SettingEnum("WEST_MERGE_COMMANDS", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2300_WEST);
    public static final SettingEnum WEST_EXCLUDE_CRUD_COMMANDS = new SettingEnum("WEST_EXCLUDE_CRUD_COMMANDS",
            Boolean.class,
            SettingsDialog.L10N_CATEGORY_2300_WEST);
    public static final SettingEnum WEST_CHILD_INDEX = new SettingEnum("WEST_CHILD_INDEX", Integer.class,
            SettingsDialog.L10N_CATEGORY_2300_WEST);
    public static final SettingEnum EAST_ENABLED = new SettingEnum("EAST_ENABLED", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2400_EAST);
    public static final SettingEnum EAST_OPEN = new SettingEnum("EAST_OPEN", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2400_EAST);
    public static final SettingEnum EAST_WIDTH = new SettingEnum("EAST_WIDTH", String.class,
            SettingsDialog.L10N_CATEGORY_2400_EAST);
    public static final SettingEnum EAST_SPLITTABLE = new SettingEnum("EAST_SPLITTABLE", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2400_EAST);
    public static final SettingEnum EAST_COLLAPSIBLE = new SettingEnum("EAST_COLLAPSIBLE", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2400_EAST);
    public static final SettingEnum EAST_MERGE_COMMANDS = new SettingEnum("EAST_MERGE_COMMANDS", Boolean.class,
            SettingsDialog.L10N_CATEGORY_2400_EAST);
    public static final SettingEnum EAST_EXCLUDE_CRUD_COMMANDS = new SettingEnum("EAST_EXCLUDE_CRUD_COMMANDS",
            Boolean.class,
            SettingsDialog.L10N_CATEGORY_2400_EAST);
    public static final SettingEnum EAST_CHILD_INDEX = new SettingEnum("EAST_CHILD_INDEX", Integer.class,
            SettingsDialog.L10N_CATEGORY_2400_EAST);
    private static Map<String, SettingEnum> settingsRegistry = new HashMap<String, SettingEnum>();
    private final String name;
    private final L10nString category;
    private final Class<?> type;
    private final Class<?> subType;
    private final int ordinal;

    private synchronized static int addToRegistry(SettingEnum settingEnum) {
        int ordinal;
        settingsRegistry.put(settingEnum.name(), settingEnum);
        ordinal = settingsRegistry.size();

        return ordinal;
    }

    public static Collection<SettingEnum> values() {
        List<SettingEnum> list = new ArrayList<SettingEnum>(settingsRegistry.values());
        Collections.sort(list);
        return Collections.unmodifiableList(list);
    }

    public static SettingEnum valueOf(String name) {
        return settingsRegistry.get(name);
    }

    public String name() {
        return name;
    }

    public L10nString getCategory() {
        return category;
    }

    public Class<?> getType() {
        return type;
    }

    public Class<?> getSubType() {
        return subType;
    }

    public int compareTo(SettingEnum o) {
        return Integer.valueOf(ordinal).compareTo(o.ordinal());
    }

    public int ordinal() {
        return ordinal;
    }

    @Override
    public String toString() {
        return name;
    }
}
