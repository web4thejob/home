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

package org.web4thejob.web.dialog;

import org.web4thejob.util.L10nString;

/**
 * <p>Dialog for managing {@link org.web4thejob.setting.Setting Setting} instances.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface SettingsDialog extends Dialog {
    public static final L10nString L10N_CATEGORY_0000_GENERAL = new L10nString(SettingsDialog.class,
            "category_0000_general", "General");
    public static final L10nString L10N_CATEGORY_1000_TABS = new L10nString(SettingsDialog.class,
            "category_1000_tabs", "Tabs");
    public static final L10nString L10N_CATEGORY_2000_NORTH = new L10nString(SettingsDialog.class,
            "category_2000_north", "North");
    public static final L10nString L10N_CATEGORY_2100_SOUTH = new L10nString(SettingsDialog.class,
            "category_2100_south", "South");
    public static final L10nString L10N_CATEGORY_2200_CENTER = new L10nString(SettingsDialog.class,
            "category_2200_center", "Center");
    public static final L10nString L10N_CATEGORY_2300_WEST = new L10nString(SettingsDialog.class,
            "category_2300_west", "West");
    public static final L10nString L10N_CATEGORY_2400_EAST = new L10nString(SettingsDialog.class,
            "category_2400_east", "East");


}
