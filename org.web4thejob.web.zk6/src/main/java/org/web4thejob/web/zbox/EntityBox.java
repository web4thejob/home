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

package org.web4thejob.web.zbox;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.EntityMetadata;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.query.QueryResultMode;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.dialog.QueryDialog;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.A;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Vbox;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class EntityBox extends AbstractBox<Entity> implements MessageListener {
    // ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = 1L;
    private final RenderElement renderElement;
    private Entity entity;

    // --------------------------- CONSTRUCTORS ---------------------------

    public EntityBox(PathMetadata pathMetadata) {
        renderElement = ContextUtil.getMRS().newInstance(RenderElement.class);
        renderElement.setPropertyPath(pathMetadata);
        marshallEmptyValue();
    }

    public EntityBox(RenderElement renderElement) {
        if (renderElement.getPropertyPath().isMultiStep() || !renderElement.getPropertyPath().getLastStep()
                .isAssociationType()) {
            throw new IllegalArgumentException("only local association properties are allowed");
        }
        this.renderElement = renderElement;
        marshallEmptyValue();
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface MessageListener ---------------------

    @Override
    public void processMessage(Message message) {
        if (MessageEnum.AFFIRMATIVE_RESPONSE == message.getId()) {
            setRawValue(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class));
        }
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected void marshallEmptyValue() {
        entity = null;
        super.marshallEmptyValue();
    }

    @Override
    protected void marshallToString(Entity value) {
        entity = value;
        super.marshallToString(value);
    }

    @Override
    protected void onEdit() {
        EntityMetadata entityMetadata = renderElement.getPropertyPath().getLastStep()
                .getAssociatedEntityMetadata();

        if (entity == null && !entityMetadata.getSubclasses().isEmpty()) {
            selectSubclass(entityMetadata, _novalueLink);
        } else if (entity != null) {
            queryEntity(entity.getEntityType());
        } else {
            queryEntity(entityMetadata.getEntityType());
        }
    }

    private void selectSubclass(EntityMetadata entityMetadata, Component parent) {
        EventListener<Event> listener = new PopupEventListener();

        Popup popup = new Popup();
        popup.setPage(getPage());
        popup.setParent(this);
        popup.open(parent, "end_before");
        popup.addEventListener(Events.ON_CANCEL, listener);

        Vbox vbox = new Vbox();
        vbox.setParent(popup);
        vbox.setHflex("true");
        vbox.setSpacing("10px");

        for (Class<? extends Entity> entityType : entityMetadata.getSubclasses()) {
            A a = new A(ContextUtil.getMRS().getEntityMetadata(entityType).getFriendlyName());
            a.setImage("img/LINK.png");
            a.setParent(vbox);
            a.setAttribute("entityType", entityType);
            a.addEventListener(Events.ON_CLICK, listener);
        }


    }

    protected void queryEntity(Class<? extends Entity> entityType) {
        Set<Setting<?>> settings = new HashSet<Setting<?>>(1);
        settings.add(ContextUtil.getSetting(SettingEnum.TARGET_TYPE, entityType));

        Query defaultQuery = CoreUtil.getDefaultQueryForTargetType(entityType);
        if (defaultQuery != null) {
            settings.add(ContextUtil.getSetting(SettingEnum.PERSISTED_QUERY_NAME, defaultQuery.getName()));
        }

        QueryDialog queryDialog = ContextUtil.getDefaultDialog(QueryDialog.class, settings, QueryResultMode.RETURN_ONE);

        queryDialog.setSubqueryConstraints(ORMUtil.buildUniqueKeyCriteria(ZkUtil.getOwningPanelOfComponent(this),
                renderElement));

        queryDialog.show(this);
    }


    @Override
    protected Entity unmarshallToRawValue() {
        return entity;
    }

    public void open() {
        onEdit();
    }

    protected boolean isEmpty() {
        return entity == null;
    }

    private class PopupEventListener implements EventListener<Event> {

        @Override
        @SuppressWarnings("unchecked")
        public void onEvent(Event event) throws Exception {
            if (event.getName().equals(Events.ON_CANCEL)) {
                ((Popup) event.getTarget()).close();
            } else if (event.getName().equals(Events.ON_CLICK)) {
                queryEntity((Class<? extends Entity>) event.getTarget().getAttribute("entityType"));
            }
        }
    }
}
