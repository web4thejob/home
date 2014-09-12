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

package org.web4thejob.web.panel.base;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import org.web4thejob.command.CommandAware;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageCache;
import org.web4thejob.message.MessageListener;
import org.web4thejob.setting.SettingAware;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.panel.*;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractLayoutPanel extends AbstractCommandAwarePanel implements LayoutPanel {
// ------------------------------ FIELDS ------------------------------

    private final MessageCache messageCache = new MessageCache();
    protected final Subpanels subpanels = new SubpanelHolder(this);

// --------------------- GETTER / SETTER METHODS ---------------------

    protected static boolean isContained(ParentCapable parent, Panel child) {
        boolean contained = false;

        for (Panel subpanel : parent.getSubpanels()) {
            if (subpanel instanceof ParentCapable) {
                contained = isContained((ParentCapable) subpanel, child);
            } else {
                contained = subpanel.equals(child);
            }

            if (contained) {
                break;
            }
        }

        return contained;
    }

// ------------------------ CANONICAL METHODS ------------------------

    public Subpanels getSubpanels() {
        return subpanels;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DesignModeAware ---------------------

    protected abstract Collection<Panel> getRenderedOrderOfChildren();

// --------------------- Interface I18nAware ---------------------

    @Override
    public void setInDesignMode(boolean designMode) {
        super.setInDesignMode(designMode);
        for (Panel panel : subpanels) {
            if (panel instanceof DesignModeAware) {
                ((DesignModeAware) panel).setInDesignMode(designMode);
            }
        }
    }

// --------------------- Interface LayoutPanel ---------------------

    @Override
    public void setL10nMode(boolean l10nMode) {
        super.setL10nMode(l10nMode);
        for (Panel panel : subpanels) {
            if (panel instanceof I18nAware) {
                ((I18nAware) panel).setL10nMode(getL10nMode());
            }
        }
    }

// --------------------- Interface MessageListener ---------------------

    public boolean unregisterCommand(CommandEnum id, boolean recursive) {
        boolean result = super.unregisterCommand(id);

        if (recursive) {
            for (Panel panel : subpanels) {
                if (panel instanceof LayoutPanel) {
                    result |= ((LayoutPanel) panel).unregisterCommand(id, recursive);
                } else if (panel instanceof CommandAware) {
                    result |= ((CommandAware) panel).unregisterCommand(id);
                }
            }
        }

        return result;
    }

    @Override
    public void processMessage(Message message) {
        if (subpanels.equals(message.getSender())) {
            switch (message.getId()) {
                case BEFORE_ADD:
                    beforeAdd(message.getArg(MessageArgEnum.ARG_ITEM, Panel.class));
                    break;
                case BEFORE_REMOVE:
                    beforeRemove(message.getArg(MessageArgEnum.ARG_ITEM, Panel.class));
                    break;
                case BEFORE_REPLACE:
                    beforeReplace(message.getArg(MessageArgEnum.ARG_OLD_ITEM, Panel.class),
                            message.getArg(MessageArgEnum.ARG_NEW_ITEM, Panel.class));
                    break;
                case AFTER_ADD:
                    afterAdd(message.getArg(MessageArgEnum.ARG_ITEM, Panel.class));
                    break;
                case AFTER_REMOVE:
                    afterRemove(message.getArg(MessageArgEnum.ARG_ITEM, Panel.class));
                    break;
                case AFTER_REPLACE:
                    afterReplace(message.getArg(MessageArgEnum.ARG_OLD_ITEM, Panel.class),
                            message.getArg(MessageArgEnum.ARG_NEW_ITEM, Panel.class));
                    break;
                default:
                    super.processMessage(message);
                    break;
            }
        } else {
            if ((isBindingSuspended() && CoreUtil.isSelectionMessage(message)) &&
                    !isContained(this, (Panel) message.getSender()) && !this.equals(message.getSender())) {
                return;
            }

            super.processMessage(message);
            for (final Panel panel : subpanels) {
                if (panel instanceof MessageListener) {

                    if (cancelDispatchForSubpanel(panel, message)) {
                        continue;
                    }

                    switch (message.getId()) {
                        case ENTITY_SELECTED:
                            if (isActive(panel)) {
                                ((MessageListener) panel).processMessage(message);
                            } else {
                                messageCache.put((MessageListener) panel, message);
                            }
                            break;
                        case ENTITY_DESELECTED:
                            if (isActive(panel)) {
                                ((MessageListener) panel).processMessage(message);
                            } else {
                                messageCache.put((MessageListener) panel, message);
                            }
                            break;
                        case ENTITY_DELETED:
                            messageCache.put((MessageListener) panel, message);
                            ((MessageListener) panel).processMessage(message);
                            break;
                        default:
                            ((MessageListener) panel).processMessage(message);
                            break;
                    }
                }
            }
        }
    }

// --------------------- Interface Panel ---------------------

    protected boolean cancelDispatchForSubpanel(Panel panel, Message message) {
        return false;
    }

    @Override
    public void render() {
        for (final Panel panel : subpanels) {
            panel.render();
        }
        super.render();
    }

// --------------------- Interface ParentCapable ---------------------

    @Override
    public String toSpringXml() {
        final Builder parser = new Builder(false);
        try {
            final Document dom = parser.build(super.toSpringXml(), null);
            final Element bean = dom.getRootElement();

            if (!subpanels.isEmpty()) {
                final Element property = new Element("property", BEANS_NAMESPACE);
                bean.appendChild(property);
                property.addAttribute(new Attribute("name", "children"));

                final Element list = new Element("list", BEANS_NAMESPACE);
                property.appendChild(list);

                for (final Panel subpanel : getRenderedOrderOfChildren()) {
                    if (subpanel.isPersisted()) {
                        final Element childRef = new Element("ref", BEANS_NAMESPACE);
                        list.appendChild(childRef);
                        childRef.addAttribute(new Attribute("bean", subpanel.getBeanName()));
                    } else {
                        final Element childBean = (Element) parser.build(subpanel.toSpringXml(),
                                null).getRootElement().copy();
                        final Attribute id = childBean.getAttribute("id");
                        if (id != null) {
                            childBean.removeAttribute(id);
                        }
                        list.appendChild(childBean);
                    }
                }
            }

            return bean.toXML();
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Collection<Panel> getChildren() {
        return Collections.unmodifiableCollection(subpanels);
    }

// --------------------- Interface SettingAware ---------------------

    public void setChildren(Collection<Panel> panels) {
        if (isInitialized() && !subpanels.isEmpty()) {
            throw new IllegalStateException("cannot use this method on an initialized collection.");
        }

        subpanels.addAll(panels);
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public void hideSetting(SettingEnum id, boolean hide) {
        super.hideSetting(id, hide);
        for (final Panel panel : subpanels) {
            if (panel instanceof SettingAware) {
                ((SettingAware) panel).hideSetting(id, hide);
            }
        }
    }

    protected void afterAdd(Panel panel) {
        // override
    }

    protected void afterRemove(Panel panel) {
        if (panel instanceof MessageListener) {
            messageCache.remove((MessageListener) panel);
        }
    }

    protected void afterReplace(Panel oldItem, Panel newItem) {
        // override
    }

    protected void beforeAdd(Panel panel) {
        if (panel instanceof I18nAware) {
            ((I18nAware) panel).setL10nMode(getL10nMode());
        }
        if (panel instanceof DesignModeAware) {
            ((DesignModeAware) panel).setInDesignMode(isInDesignMode());
        }
        panel.hightlightPanel(isHighlighted());
    }

    protected void beforeRemove(Panel panel) {
        // override
    }

    protected void beforeReplace(Panel oldItem, Panel newItem) {
        // override
    }

    protected void flushCache(Panel panel) {
        if (panel instanceof MessageListener) {
            messageCache.flush((MessageListener) panel);
        }
    }

    protected abstract boolean isActive(Panel panel);

    @Override
    public void setUnsavedSettings(boolean unsavedSettings) {
        if (hasUnsavedSettings() != unsavedSettings) {
            super.setUnsavedSettings(unsavedSettings);

            if (!hasUnsavedSettings()) {
                for (Panel subpanel : subpanels) {
                    if (subpanel instanceof SettingAware && !subpanel.isPersisted()) {
                        ((SettingAware) subpanel).setUnsavedSettings(false);
                    }
                }
            }
        }
    }


}
