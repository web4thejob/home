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

package org.web4thejob.orm;

import org.hibernate.LockOptions;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.annotation.DefaultHolder;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;

import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Repository
/* package */class DataWriterServiceImpl implements DataWriterService {

    @Autowired
    private SessionFactory sessionFactory;

    public <E extends Entity> void delete(E entity) {
        sessionFactory.getCurrentSession().buildLockRequest(LockOptions.UPGRADE).lock(entity);
        sessionFactory.getCurrentSession().refresh(entity);
        sessionFactory.getCurrentSession().delete(entity);
    }

    public <E extends Entity> void save(E entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);

        for (AnnotationMetadata<DefaultHolder> defaultHolder : ContextUtil.getMRS().getAnnotationMetadata(entity
                .getEntityType(), DefaultHolder.class)) {
            processDefaultHolder(entity, defaultHolder);
        }


    }

    public <E extends Entity> void save(List<E> entities) {
        for (final E entity : entities) {
            save(entity);
        }
    }

    private void processDefaultHolder(Entity entity,
                                      AnnotationMetadata<DefaultHolder> defaultHolder) {

        Query query = ContextUtil.getEntityFactory().buildQuery(entity.getEntityType());
        for (String filter : defaultHolder.getAnnotation().filters()) {
            query.addCriterion(new Path(filter), Condition.EQ, ContextUtil.getMRS().getPropertyMetadata(entity
                    .getEntityType(), filter).getValue(entity));
        }
        List<Entity> existingEntities = ContextUtil.getDRS().findByQuery(query);
        PropertyMetadata propertyMetadata = ContextUtil.getMRS().getPropertyMetadata(entity.getEntityType(),
                defaultHolder.getField().getName());
        boolean currentValue = (Boolean) propertyMetadata.getValue(entity);

        if (existingEntities.size() > 1 && currentValue) {
            for (Entity existingEntity : existingEntities) {
                if (!existingEntity.equals(entity) && (Boolean) propertyMetadata.getValue(existingEntity)) {
                    propertyMetadata.setValue(existingEntity, false);
                    sessionFactory.getCurrentSession().saveOrUpdate(existingEntity);
                }
            }
        } else if (existingEntities.size() == 1 && !currentValue) {
            propertyMetadata.setValue(entity, true);
            sessionFactory.getCurrentSession().saveOrUpdate(entity);
        }
    }

}
