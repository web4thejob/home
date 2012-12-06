/*
 * Copyright (c) 2012 Veniamin Isaias.
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

package org.web4thejob.util;

import org.web4thejob.orm.scheme.SchemeType;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public interface L10nMessages {
    public static final L10nString L10N_PROCESSING = new L10nString("msg_processing", "Processing...");
    public static final L10nString L10N_MSGBOX_TITLE_QUESTION = new L10nString("msgbox_title_question", "Question");
    public static final L10nString L10N_MSGBOX_TITLE_WARNING = new L10nString("msgbox_title_warning", "Warning");
    public static final L10nString L10N_MSGBOX_TITLE_INFO = new L10nString("msgbox_title_info", "Information");
    public static final L10nString L10N_BLOCK_SYSTEM_PROTECTED_DELETION = new L10nString
            ("block_system_protected_deletion", "This entry is system-protected thus can not be deleted.");
    public static final L10nString L10N_EMPTY_LIST = new L10nString("msg_empty_list", "The list is empty");

    public static final L10nString L10N_SCHEME_ENTITY = new L10nString(SchemeType.class.getSimpleName() + "." +
            SchemeType.ENTITY_SCHEME.name(), "Entity");
    public static final L10nString L10N_SCHEME_LIST = new L10nString(SchemeType.class.getSimpleName() + "." +
            SchemeType.LIST_SCHEME.name(), "List");

    public static final L10nString L10N_LABEL_TIMESTAMP = new L10nString("label_timestamp", "Timestamp");
    public static final L10nString L10N_TOOLTIP_NAVIGATE = new L10nString("tooltip_navigate",
            "Navigate. Press SHIFT+Click for more options.");
    public static final L10nString L10N_LABEL_CLICK = new L10nString("label_click",
            "Click");
    public static final L10nString L10N_MSG_PREPARE_FIRST_USE = new L10nString("label_prepare_first_use",
            "Preparing environment for first use...");
    public static final L10nString L10N_NAME_DEFAULT_SECURITY_MENU = new L10nString("name_default_security_menu",
            "System Security");
    public static final L10nString L10N_NAME_DEFAULT_PANELS_MENU = new L10nString("name_default_panels_menu",
            "System Panels");
    public static final L10nString L10N_NAME_DEFAULT_PARAMETERS_MENU = new L10nString("name_default_parameters_menu",
            "System Parameters");
    public static final L10nString L10N_DEVELOPER_SIGNATURE = new L10nString("label_developer_signature",
            "Designed and developed by <a href=\"mailto:bissaias@hotmail.com\"> Veniamin Isaias</a>");
    public static final L10nString L10N_ICONS_SIGNATURE = new L10nString("label_icons_signature",
            "Icons by <a href=\"http://iconza.com\">Iconza.com</a>");
    public static final L10nString L10N_UNSAVED_CHANGES = new L10nString("msg_unsaved_changes",
            "There are unsaved changes!");
    public static final L10nString L10N_DISCARD_CHANGES_WARNING = new L10nString("msg_discard_changes_warning",
            "There are unsaved changes which will be permanently discarded if you move to another record.");
    public static final L10nString L10N_VALIDATION_ERRORS = new L10nString("msg_validation_errorrs",
            "The commit of changes failed due to invalid data input.");
    public static final L10nString L10N_INVALID_IMAGE_FORMAT_ERROR = new L10nString("msg_invalid_image_format_error",
            "Invalid image format.");
    public static final L10nString L10N_NOTHING_SELECTED_ERROR = new L10nString("msg_nothing_selected_error",
            "Nothing was selected.");
}