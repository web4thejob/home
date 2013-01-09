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

package org.web4thejob.util;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.module.LocalizableModule;
import org.web4thejob.module.Module;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.DesktopLayoutPanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@SuppressWarnings("rawtypes")
public class L10nUtil {
    private static MessageSource source;

    public static MessageSource getMessageSource() {
        return source;
    }

    public static String getMessage(String className, String code, String defaultValue) {
        Class clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return defaultValue;
        }

        return getMessage(clazz, code, null, defaultValue);
    }

    public static String getMessage(Class<?> clazz, String code, String defaultValue) {
        return getMessage(clazz, code, null, defaultValue);
    }

    public static String getMessage(Class<?> clazz, String code, Object[] args, String defaultValue) {
        if (source == null) {
            source = ContextUtil.getBean(StringUtils.uncapitalize(MessageSource.class.getSimpleName()),
                    MessageSource.class);
            try {
                source.getMessage("just_to_initialize_message_source_properly", null, CoreUtil.getUserLocale());
            } catch (NoSuchMessageException ignore) {
            }
        }

        if (source != null) {
            try {
                return source.getMessage(clazz.getName() + "." + code, args, CoreUtil.getUserLocale());
            } catch (NoSuchMessageException e) {
                if (!CoreUtil.getUserLocale().equals(Locale.ENGLISH)) {
                    logMissingMessage(clazz.getName() + "." + code, defaultValue);
                }
            }
        }

        if (args != null) {
            return MessageFormat.format(defaultValue, args);
        } else {
            return defaultValue;
        }

    }

    public static void logMissingMessage(String code, String defaultValue) {
        File file = new File(System.getProperty("user.home"), "w4tj_" + CoreUtil.getUserLocale().toString() +
                ".log");
        FileWriter out;
        try {
            out = new FileWriter(file, true);
            out.write(code + "=" + defaultValue + System.getProperty("line.separator"));
            out.close();
            // logger.warn("Missiing message: " + code + "=" + defaultValue);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<L10nString> getLocalizableResources(final Class<?> localizable) {
        final List<L10nString> strings = new ArrayList<L10nString>();
        final Set<Class> classes = new HashSet<Class>();
        classes.add(localizable);
        classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(localizable));

        for (Class<?> clazz : classes) {
            ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                        @Override
                        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                            strings.add((L10nString) field.get(null));
                        }
                    }, new ReflectionUtils.FieldFilter() {
                        @Override
                        public boolean matches(Field field) {
                            return ReflectionUtils.isPublicStaticFinal(field) && L10nString.class.equals(field
                                    .getType());
                        }
                    }
            );
        }

        //get localizable resources from extension modules
        for (Module module : ContextUtil.getModules()) {
            if (module instanceof LocalizableModule) {
                strings.addAll(((LocalizableModule) module).getLocalizableStrings(classes));
            }
        }

        // add commands,settings and global strings here...
        if (DesktopLayoutPanel.class.isAssignableFrom(localizable)) {
            for (CommandEnum commandEnum : CommandEnum.values()) {
                L10nString l10nString = new L10nString(CommandEnum.class, commandEnum.name(),
                        L10nUtil.getMessage(commandEnum.getClass(), commandEnum.name(), commandEnum.name()));
                strings.add(l10nString);
            }

            for (SettingEnum settingEnum : SettingEnum.values()) {
                L10nString l10nString = new L10nString(SettingEnum.class, settingEnum.name(),
                        L10nUtil.getMessage(settingEnum.getClass(), settingEnum.name(), settingEnum.name()));
                strings.add(l10nString);
            }

            for (Condition condition : Condition.getConditions()) {
                L10nString l10nString = new L10nString(Condition.class, condition.getKey(), condition.getKey());
                strings.add(l10nString);
            }
        }

        return strings;
    }

}
