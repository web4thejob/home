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
import org.web4thejob.command.*;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.dialog.Dialog;
import org.web4thejob.web.dialog.SelectPanelDialog;
import org.web4thejob.web.panel.base.zk.AbstractZkContentPanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultPlaceholderPanel extends AbstractZkContentPanel implements PlaceholderPanel {
    public static final L10nString L10N_LABEL_EMPTY_REGION = new L10nString(DefaultPlaceholderPanel.class,
            "label_empty_region", "This region is empty.");
    public static final L10nString L10N_LABEL_PLACE_PANEL = new L10nString(DefaultPlaceholderPanel.class,
            "label_place_panel", "to place a new content or layout panel.");
    public static final L10nString L10N_LABEL_DISCARD_PANEL = new L10nString(DefaultPlaceholderPanel.class,
            "label_discard_panel", "to discard the region.");
    private Vbox vbox;

    @Override
    protected void registerSettings() {
        // no settings to register
    }

    @Override
    public void render() {
        super.render();

        if (vbox != null) {
            vbox.detach();
            vbox = null;
        }

        vbox = new Vbox();
        ZkUtil.setParentOfChild((Component) base, vbox);
        ((org.zkoss.zul.Panel) base).setBorder(true);
        vbox.setWidth("100%");
        vbox.setHeight("100%");
        vbox.setPack("center");
        vbox.setAlign("center");

        Label label = new Label(L10N_LABEL_EMPTY_REGION.toString());
        label.setMultiline(false);
        label.setParent(vbox);

        if (hasCommand(CommandEnum.DESIGN_PANEL)) {
            Hlayout hlayout = new Hlayout();
            hlayout.setParent(vbox);

            label = new Label(L10nMessages.L10N_LABEL_CLICK.toString() + " ");
            label.setParent(hlayout);

            HyperlinkCommandDecorator decorator = new DefaultHyperlinkCommandDecorator(getCommand(CommandEnum
                    .DESIGN_PANEL));
            decorator.attach(hlayout);

            new Label(L10N_LABEL_PLACE_PANEL.toString()).setParent(hlayout);
        }

        if (hasCommand(CommandEnum.DESTROY_PANEL)) {
            Hlayout hlayout = new Hlayout();
            hlayout.setParent(vbox);

            label = new Label(L10nMessages.L10N_LABEL_CLICK.toString() + " ");
            label.setParent(hlayout);

            HyperlinkCommandDecorator decorator = new DefaultHyperlinkCommandDecorator(getCommand(CommandEnum
                    .DESTROY_PANEL));
            decorator.attach(hlayout);

            new Label(L10N_LABEL_DISCARD_PANEL.toString()).setParent(hlayout);
        }
    }

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(super.getSupportedCommands());
        supported.add(CommandEnum.DESIGN_PANEL);
        return Collections.unmodifiableSet(supported);
    }

    @Override
    public void setInDesignMode(boolean designMode) {
        super.setInDesignMode(designMode);
        if (designMode) {
            unregisterCommand(CommandEnum.CONFIGURE_SETTINGS);
            unregisterCommand(CommandEnum.SAVE_PANEL);
            unregisterCommand(CommandEnum.SAVE_PANEL_AS);
            unregisterCommand(CommandEnum.CUT);

            Command command = registerCommand(ContextUtil.getDefaultCommand(CommandEnum.DESIGN_PANEL, this));
            if (command != null) {
                activateCommand(command.getId(), true, true);
            }

            if (hasCommand(CommandEnum.DESIGN) && !hasCommand(CommandEnum.PASTE)) {
                command = registerCommand(ContextUtil.getSubcommand(CommandEnum.PASTE,
                        getCommand(CommandEnum.DESIGN)));
                if (command != null) {
                    command.setActivated(ContextUtil.getSessionContext().hasAttribute(Attributes
                            .ATTRIB_CUT_PASTE_PANEL));
                }
            }
        } else {
            unregisterCommand(CommandEnum.DESIGN_PANEL);
        }
    }

    @Override
    protected void processValidCommand(Command command) {
        if (CommandEnum.DESIGN_PANEL.equals(command.getId()) || CommandEnum.DESIGN_PANEL_OTHER.equals(command.getId()
        )) {
            Dialog dialog = ContextUtil.getDefaultDialog(SelectPanelDialog.class);
            dialog.setInDesignMode(isInDesignMode());
            dialog.setL10nMode(getL10nMode());
            dialog.show(new SelectPanelResponse());
        } else if (CommandEnum.DESIGN_PANEL_ENTITY_VIEW.equals(command.getId())) {
            new SelectPanelResponse().processMessage(ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this,
                    MessageArgEnum.ARG_ITEM, ContextUtil.getDefaultPanel(MutableEntityViewPanel.class)));
        } else if (CommandEnum.DESIGN_PANEL_LIST_VIEW.equals(command.getId())) {
            new SelectPanelResponse().processMessage(ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this,
                    MessageArgEnum.ARG_ITEM, ContextUtil.getDefaultPanel(ListViewPanel.class)));
        } else if (CommandEnum.DESIGN_PANEL_HTML_VIEW.equals(command.getId())) {
            new SelectPanelResponse().processMessage(ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this,
                    MessageArgEnum.ARG_ITEM, ContextUtil.getDefaultPanel(HtmlViewPanel.class)));
        } else if (CommandEnum.DESIGN_PANEL_IFRAME_VIEW.equals(command.getId())) {
            new SelectPanelResponse().processMessage(ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this,
                    MessageArgEnum.ARG_ITEM, ContextUtil.getDefaultPanel(FramePanel.class)));
        } else if (CommandEnum.DESIGN_PANEL_TABBED_VIEW.equals(command.getId())) {
            new SelectPanelResponse().processMessage(ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this,
                    MessageArgEnum.ARG_ITEM, ContextUtil.getDefaultPanel(TabbedLayoutPanel.class)));
        } else if (CommandEnum.DESIGN_PANEL_BORDERED_VIEW.equals(command.getId())) {
            new SelectPanelResponse().processMessage(ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this,
                    MessageArgEnum.ARG_ITEM, ContextUtil.getDefaultPanel(BorderedLayoutPanel.class)));
        } else if (CommandEnum.DESTROY_PANEL.equals(command.getId())) {
            if (getParent() != null) {
                ParentCapable ref = getParent();
                setParent(null);
                if (ref instanceof Panel) {
                    ((Panel) ref).render();
                }
            } else if (isAttached()) {
                detach();
            }
        } else if (CommandEnum.PASTE.equals(command.getId())) {
            Panel panel = ContextUtil.getSessionContext().getAttribute(Attributes.ATTRIB_CUT_PASTE_PANEL);
            if (panel instanceof Panel) {
                new SelectPanelResponse().processMessage(ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this,
                        MessageArgEnum.ARG_ITEM, panel));
            }
            ContextUtil.getSessionContext().clarAttribute(Attributes.ATTRIB_CUT_PASTE_PANEL);
            dispatchMessage(ContextUtil.getMessage(MessageEnum.PANEL_COPY_END, this));
        } else {
            super.processValidCommand(command);
        }
    }

    @Override
    public void processMessage(Message message) {
        if (message.getId() == MessageEnum.PANEL_COPY_START || message.getId() == MessageEnum.PANEL_COPY_END) {
            Command command = getCommand(CommandEnum.PASTE);
            if (command != null) {
                command.setActivated(message.getId() == MessageEnum.PANEL_COPY_START);
            }
        } else {
            super.processMessage(message);
        }
    }

    private class SelectPanelResponse implements MessageListener {

        @Override
        public void processMessage(Message message) {
            if (MessageEnum.AFFIRMATIVE_RESPONSE == message.getId()) {
                Panel panel;
                Object arg = message.getArgs().get(MessageArgEnum.ARG_ITEM);
                if (arg instanceof Panel) {
                    panel = (Panel) arg;
                } else {
                    panel = ContextUtil.getPanel(message.getArgs().get(MessageArgEnum.ARG_ITEM).toString());
                }

                if (panel instanceof DesignModeAware) {
                    ((DesignModeAware) panel).setInDesignMode(true);
                }
                if (panel instanceof I18nAware) {
                    ((I18nAware) panel).setL10nMode(getL10nMode());
                }

                panel.render();
                replace(panel);

                if (panel instanceof CommandAware && !panel.isPersisted()) {
                    Command command = ((CommandAware) panel).getCommand(CommandEnum.CONFIGURE_SETTINGS);
                    if (command != null) {
                        command.process();
                    }
                }
            }
        }
    }

    private void replace(Panel panel) {

        if (getParent() != null) {
            getParent().getSubpanels().replace(this, panel);
            if (panel.getParent() instanceof Panel) {
                ((Panel) panel.getParent()).render();
            }
        } else if (((Component) base).getParent() != null) {
            Component ref = ((Component) base).getParent();
            detach();
            panel.attach(ref);
            panel.render();
        }

    }
}
