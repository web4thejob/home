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
package org.web4thejob.web.zbox;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageAware;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.EntityHierarchyItem;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.annotation.EntityHierarchyHolder;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.web.panel.EntityHierarchyPanel;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;

import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 3.5.2
 */
public class EntityHierarchyTreeBox extends AbstractBox<EntityHierarchyItem<?>> implements MessageAware {
    private static final long serialVersionUID = 1L;
    private final RenderElement renderElement;
    private Popup popup;
    private EntityHierarchyItem entityHierarchyItem;
    private EntityHierarchyPanel entityHierarchyPanel;

    public EntityHierarchyTreeBox(PathMetadata pathMetadata) {
        super();
        renderElement = ContextUtil.getMRS().newInstance(RenderElement.class);
        renderElement.setPropertyPath(pathMetadata);
        renderElement.setFriendlyName(pathMetadata.getFriendlyName());
        marshallEmptyValue();
        addEventListener(Events.ON_CANCEL, this);
    }

    public EntityHierarchyTreeBox(RenderElement renderElement) {
        super();
        this.renderElement = renderElement;
        marshallEmptyValue();
        addEventListener(Events.ON_CANCEL, this);
    }

    @Override
    protected void onEdit() {
        popup();
    }

    private void popup() {
        if (!renderElement.getPropertyPath().getLastStep().isAnnotatedWith(EntityHierarchyHolder.class)) {
            return;
        }

        if (popup == null) {
            popup = new Popup();
            popup.addEventListener(Events.ON_CANCEL, this);
            popup.setStyle("overflow:auto");
            popup.setWidth("250px");
            popup.setHeight("350px");

            entityHierarchyPanel = ContextUtil.getDefaultPanel(EntityHierarchyPanel.class, Boolean.TRUE);
            entityHierarchyPanel.setTargetType(renderElement.getPropertyPath().getLastStep().getAnnotation
                    (EntityHierarchyHolder.class).hierarchyType());
            entityHierarchyPanel.attach(popup);
            entityHierarchyPanel.addMessageListener(this);


        }

        if (entityHierarchyPanel.getItemCount() == 0) {
            Messagebox.show(L10nMessages.L10N_EMPTY_LIST.toString(), L10nMessages.L10N_MSGBOX_TITLE_INFO.toString(),
                    new Messagebox.Button[]{Messagebox.Button.OK}, Messagebox.INFORMATION, null);
            return;
        }

        popup.setPage(getPage());
        popup.setParent(this);
        popup.open(this, "after_start");
    }

    @Override
    public boolean addMessageListener(MessageAware messageAware) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dispatchMessage(Message message) {
        processMessage(message);
    }

    @Override
    public boolean removeMessageListener(MessageAware messageAware) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<MessageAware> getListeners() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void processMessage(Message message) {
        if (message.getId() == MessageEnum.ENTITY_ACCEPTED && message.getSender().equals(entityHierarchyPanel)) {
            setRawValue(message.getArg(MessageArgEnum.ARG_ITEM, EntityHierarchyItem.class));
        }
    }

    @Override
    protected void marshallEmptyValue() {
        entityHierarchyItem = null;
        super.marshallEmptyValue();
    }

    @Override
    public EntityHierarchyItem<?> getRawValue() {
        if (!isEmpty()) {
            return unmarshallToRawValue();
        } else {
            return null;
        }
    }

    @Override
    public void setRawValue(EntityHierarchyItem<?> value) {
        if (!Objects.equals(value, getRawValue())) {
            if (value != null) {
                marshallToString(value);
            } else {
                marshallEmptyValue();
            }

            Events.sendEvent(Events.ON_CHANGE, this, null);
        }
        if (popup != null) {
            popup.close();
        }
    }

    @Override
    protected void marshallToString(EntityHierarchyItem value) {
        entityHierarchyItem = value;
        super.marshallToString(entityHierarchyItem);
    }

    @Override
    protected EntityHierarchyItem unmarshallToRawValue() {
        return entityHierarchyItem;
    }

    protected boolean isEmpty() {
        return entityHierarchyItem == null;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getName().equals(Events.ON_CANCEL)) {
            popup.close();
            event.stopPropagation();
        } else {
            super.onEvent(event);
        }
    }

}

