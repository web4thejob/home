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

package org.web4thejob.web.panel.base;

import org.springframework.util.StringUtils;
import org.web4thejob.command.ArbitraryDropdownItems;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Criterion;
import org.web4thejob.orm.query.Query;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.panel.*;

import java.util.Collections;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public abstract class AbstractBindablePanel extends AbstractMasterDetailTypeAwarePanel implements BindCapable,
        ArbitraryDropdownItems {

    private Entity masterEntity;

    @Override
    public boolean canBind(Entity entity) {
        if (entity == null) {
            return false;
        } else if (hasMasterType() && getMasterType().isInstance(entity)) {
            return true;
        } else if (hasTargetType() && getTargetType().isInstance(entity)) {
            if (hasMasterType()) {
                if (hasMasterEntity()) {
                    Entity master = ContextUtil.getMRS().getPropertyMetadata(getTargetType(),
                            getBindProperty()).getValue(entity);
                    return getMasterEntity().equals(master);
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isBoundOn(Entity entity) {
        if (hasMasterType() && getMasterType().isInstance(entity) && hasMasterEntity())
            return getMasterEntity().equals(entity);
        if (hasTargetType() && getTargetType().isInstance(entity) && hasTargetEntity()) {
            return getTargetEntity().equals(entity);
        }
        return false;
    }

    @Override
    public void setTargetEntity(Entity targetEntity) {
        Entity currentEntity = getTargetEntity();
        if (currentEntity == null && targetEntity == null && hasMasterEntity()) {
            arrangeForState(PanelState.READY);
        } else {
            arrangeForTargetEntity(targetEntity);
        }

    }

    @Override
    public boolean hasMasterEntity() {
        return getMasterEntity() != null;
    }

    @Override
    public Entity getMasterEntity() {
        return masterEntity;
    }

    protected abstract void arrangeForMasterEntity();

    protected abstract void arrangeForTargetEntity(Entity targetEntity);

    @Override
    public void setMasterEntity(Entity masterEntity) {
        if (this.masterEntity == null && masterEntity == null) {
            if (isMasterDetail()) {
                arrangeForState(PanelState.UNDEFINED);
            } else {
                arrangeForState(PanelState.READY);
            }
        } else if (this.masterEntity != null && this.masterEntity.equals(masterEntity)) {
            if (hasTargetEntity()) {
                arrangeForState(PanelState.FOCUSED);
            } else {
                if (this instanceof MutablePanel) {
                    arrangeForState(PanelState.READY);
                } else {
                    arrangeForState(PanelState.BROWSING);
                }
            }
        } else {
            this.masterEntity = masterEntity;
            arrangeForMasterEntity();
        }
    }

    protected void arrangeForNullMasterType() {
        setMasterEntity(null);
    }

    protected Entity prepareMutableInstance(MutableMode mode) {
        Entity mutableEntity;
        if (mode == MutableMode.INSERT) {
            mutableEntity = ContextUtil.getMRS().newInstance(getTargetType());
            if (isMasterDetail()) {
                if (hasMasterEntity()) {
                    PropertyMetadata propertyMetadata = ContextUtil.getMRS().getPropertyMetadata(getTargetType(),
                            getBindProperty());
                    propertyMetadata.setValue(mutableEntity, getMasterEntity());
                } else {
                    throw new IllegalStateException("master entity is missing");
                }
            }
        } else if (mode == MutableMode.UPDATE) {
            mutableEntity = getTargetEntity().clone();
        } else {
            throw new IllegalArgumentException("illegal mutable mode " + mode.name());
        }

        return mutableEntity;
    }

    abstract protected boolean processEntityDeselection(Entity entity);

    abstract protected boolean processEntityInsertion(Entity entity);

    abstract protected boolean processEntityUpdate(Entity entity);

    abstract protected boolean processEntityDeletion(Entity entity);

    @Override
    public void processMessage(Message message) {
        if (this.equals(message.getSender()) || (isBindingSuspended() && CoreUtil.isSelectionMessage(message))) {
            return;
        }

        switch (message.getId()) {
            case ENTITY_SELECTED:
                bind(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class));
                break;
            case ENTITY_DESELECTED:
                processEntityDeselection(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class));
                break;
            case ENTITY_INSERTED:
                processEntityInsertion(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class));
                break;
            case ENTITY_UPDATED:
                processEntityUpdate(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class));
                break;
            case ENTITY_DELETED:
                processEntityDeletion(message.getArg(MessageArgEnum.ARG_ITEM, Entity.class));
                break;
            default:
                super.processMessage(message);
                break;
        }
    }

    @Override
    public String toString() {
        String name = getSettingValue(SettingEnum.PANEL_NAME, null);
        if (!StringUtils.hasText(name)) {
            name = super.toString();
        }
        if (hasTargetEntity()) {
            name += ": " + getTargetEntity().toString();
        }

        return name;
    }

    protected void applyCurrentCritriaValues(Query query, Entity templEntity) {
        if (query != null) {
            for (Criterion criterion : query.getCriteria()) {
                if (criterion.getPropertyPath() != null && !criterion.getPropertyPath().isMultiStep() &&
                        criterion.getCondition() != null && criterion.getValue() != null) {
                    if (Condition.EQ.equals(criterion.getCondition())) {
                        criterion.getPropertyPath().getLastStep().setValue(templEntity, criterion.getValue());
                    }
                }
            }
            templEntity.calculate();
        }
    }

    protected Query getPersistedQuery() {
        String queryName = getSettingValue(SettingEnum.PERSISTED_QUERY_NAME, null);
        if (StringUtils.hasText(queryName)) {
            Query lookup = ContextUtil.getEntityFactory().buildQuery(Query.class);
            lookup.addCriterion(new Path(Query.FLD_FLAT_TARGET_TYPE), Condition.EQ, getTargetType().getCanonicalName());
            lookup.addCriterion(new Path(Query.FLD_NAME), Condition.EQ, queryName);

            Query query = ContextUtil.getDRS().findUniqueByQuery(lookup);
            if (query != null) {
                return query;
            }
        }

        return null;
    }

    @Override
    public Map<String, String> getDropdownItems() {
        if (!hasTargetType()) return Collections.emptyMap();
        return CoreUtil.getRelatedPanelsMap(getTargetType(), MutableEntityViewPanel.class);
    }

    @Override
    public void onItemClicked(String key) {
        if (hasTargetEntity()) {
            Panel entityPanel = CoreUtil.getEntityViewPanel(getTargetEntity(), key);

            if (entityPanel != null) {
                dispatchMessage(ContextUtil.getMessage(MessageEnum.ADOPT_ME,
                        entityPanel));
            }
        }
    }

}
