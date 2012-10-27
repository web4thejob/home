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

package org.web4thejob.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import org.web4thejob.command.*;
import org.web4thejob.message.DefaultMessage;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.module.Module;
import org.web4thejob.orm.DataReaderService;
import org.web4thejob.orm.DataWriterService;
import org.web4thejob.orm.EntityFactory;
import org.web4thejob.orm.MetaReaderService;
import org.web4thejob.security.AuthorizationBeanPostProcessor;
import org.web4thejob.security.SecurityService;
import org.web4thejob.setting.DefaultSetting;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.dialog.Dialog;
import org.web4thejob.web.panel.Panel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>Utility class providing convinient methods for invoking fundamental framework beans.</p>
 * <p>The class is used extentively throughout the framework.</p>
 *
 * @author Veniamin Isaias
 * @see MetaReaderService
 * @see DataReaderService
 * @see DataWriterService
 * @see EntityFactory
 * @see SecurityService
 * @see SessionContext
 * @see Panel
 * @see Command
 * @see Setting
 * @see Dialog
 * @see Message
 * @since 1.0.0
 */

@Service
public class ContextUtil implements ApplicationContextAware {
    // ------------------------------ FIELDS ------------------------------

    private static final String DEFAULT_PREFFIX = "default";
    private static ApplicationContext rootContext;
    private static EntityFactory entityFactory;

    // -------------------------- STATIC METHODS --------------------------

    public static <T> T getBean(String id, Class<T> clazz) {
        return rootContext.getBean(id, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Dialog> T getDialog(Class<T> type, Object... args) {
        return (T) ContextUtil.getBean(StringUtils.uncapitalize(type.getSimpleName()), args);
    }

    private static Object getBean(String name, Object... args) {
        return rootContext.getBean(name, args);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Dialog> T getDefaultDialog(Class<T> type, Object... args) {
        return (T) ContextUtil.getBean(StringUtils.uncapitalize(DEFAULT_PREFFIX + type.getSimpleName()), args);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Command> T getDefaultCommand(CommandEnum id, CommandAware owner) {
        try {
            T command = (T) rootContext.getBean(StringUtils.uncapitalize(DefaultCommand.class.getSimpleName()), id,
                    owner);
            if (id.getValue() != null) {
                command.setValue(id.getValue());
            }

            return command;
        } catch (BeanCreationException e) {
            return null;
        }
    }

    public static <T extends Command> T getSubcommand(CommandEnum id, Command command) {
        return getSubcommand(id, command, null);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Command> T getSubcommand(CommandEnum id, Command command,
                                                      CommandListener alternateListener) {
        try {
            T subcommand = (T) rootContext.getBean(StringUtils.uncapitalize(DefaultSubcommand.class.getSimpleName()),
                    id, command, alternateListener);
            if (id.getValue() != null) {
                subcommand.setValue(id.getValue());
            }

            return subcommand;
        } catch (BeanCreationException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Setting<Serializable>> T getSetting(SettingEnum id, Serializable value) {
        return (T) rootContext.getBean(StringUtils.uncapitalize(DefaultSetting.class.getSimpleName()), id, value);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Message> T getMessage(MessageEnum id, Object sender) {
        return (T) rootContext.getBean(StringUtils.uncapitalize(DefaultMessage.class.getSimpleName()), id, sender);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Message> T getMessage(MessageEnum id, Object sender, Map<MessageArgEnum, Object> args) {
        return (T) rootContext.getBean(StringUtils.uncapitalize(DefaultMessage.class.getSimpleName()), id, sender,
                args);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Message> T getMessage(MessageEnum id, Object sender, MessageArgEnum key, Object value) {
        return (T) rootContext.getBean(StringUtils.uncapitalize(DefaultMessage.class.getSimpleName()), id, sender,
                key, value);
    }

    public static <T extends Panel> T getDefaultPanel(Class<T> clazz) {
        return rootContext.getBean(DEFAULT_PREFFIX + clazz.getSimpleName(), clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Panel> T getDefaultPanel(Class<T> clazz, Object... args) {
        return (T) rootContext.getBean(DEFAULT_PREFFIX + clazz.getSimpleName(), args);
    }

    public static Panel getPanel(String id) {
        return getSessionContext().getBean(id, Panel.class);
    }

    public static Panel getPanelSafe(String id) {
        try {
            return getSessionContext().getBean(id, Panel.class);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DataReaderService getDRS() {
        return rootContext.getBean(DataReaderService.class);
    }

    public static DataWriterService getDWS() {
        return rootContext.getBean(DataWriterService.class);
    }

    public static MetaReaderService getMRS() {
        return rootContext.getBean(MetaReaderService.class);
    }

    public static TransactionTemplate getTransactionWrapper() {
        return new TransactionTemplate(rootContext.getBean(PlatformTransactionManager.class));
    }

    public static SessionContext getSessionContext() {
        SessionContext sessionContext = rootContext.getBean(SessionContext.class);
        if (!sessionContext.isActive()) {

            //SettingEnum PropertyEditor registration
            CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
            customEditorConfigurer.setPropertyEditorRegistrars(new PropertyEditorRegistrar[]{new
                    CustomEditorRegistrar()});
            sessionContext.addBeanFactoryPostProcessor(customEditorConfigurer);

            sessionContext.refresh();
            BeanPostProcessor beanPostProcessor = ContextUtil.getBean(AuthorizationBeanPostProcessor.class);
            if (beanPostProcessor != null) {
                sessionContext.getBeanFactory().addBeanPostProcessor(beanPostProcessor);
            }
        }
        return sessionContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return rootContext.getBean(clazz);
    }

    public static EntityFactory getEntityFactory() {
        if (entityFactory == null) {
            entityFactory = rootContext.getBean(EntityFactory.class);
        }
        return entityFactory;
    }

    public static void publishEvent(ApplicationEvent event) {
        rootContext.publishEvent(event);
    }

    public static SecurityService getSecurityService() {
        return rootContext.getBean(SecurityService.class);
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface ApplicationContextAware
    // ---------------------

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        rootContext = applicationContext;
    }

    public static boolean resourceExists(String location) {
        return rootContext.getResource(location).exists();
    }

    public static List<Module> getModules() {
        final List<Module> modules = new ArrayList<Module>();

        for (String bean : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(getSessionContext(), Module.class)) {
            try {
                modules.add(rootContext.getBean(bean, Module.class));
            } catch (BeansException e) {
                //ignore
            }
        }
        Collections.sort(modules);
        return Collections.unmodifiableList(modules);
    }


}