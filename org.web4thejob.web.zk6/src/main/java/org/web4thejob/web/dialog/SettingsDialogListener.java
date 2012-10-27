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

package org.web4thejob.web.dialog;

import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingAware;
import org.web4thejob.web.panel.Panel;
import org.web4thejob.web.panel.ParentCapable;

import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class SettingsDialogListener implements MessageListener {
    private final SettingAware settingAware;

    public SettingsDialogListener(SettingAware settingAware) {
        this.settingAware = settingAware;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processMessage(Message message) {
        if (MessageEnum.AFFIRMATIVE_RESPONSE == message.getId()) {
            Set<Setting<?>> settings = (Set<Setting<?>>) message.getArgs().get(MessageArgEnum.ARG_ITEM);
            settingAware.setSettings(settings);
            if (settingAware instanceof Panel) {
                ((Panel) settingAware).render();
            }

            if (settingAware instanceof ParentCapable) {
                //in case indices have changed
                ((ParentCapable) settingAware).getSubpanels().sort();
            }

        }
    }
}
