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

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.annotation.*;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.security.RoleIdentity;
import org.web4thejob.security.RoleMembers;
import org.web4thejob.security.UserIdentity;
import org.web4thejob.util.CoreUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */


/* package */class MetaReaderServiceImpl implements MetaReaderService {
    // ------------------------------ FIELDS ------------------------------

    static final Map<String, EntityMetadata> metaCache = new HashMap<String, EntityMetadata>();
    static final Map<String, Map<String, Map<String, AnnotationMetadata<? extends Annotation>>>> annoCache = new
            HashMap<String, Map<String, Map<String, AnnotationMetadata<? extends Annotation>>>>();

    @Autowired
    private SessionFactory sessionFactory;

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface MetaReaderService ---------------------

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Entity> E deproxyEntity(E entity) {
        final E proxy = entity;

        if (proxy instanceof HibernateProxy) {
            {
                if (((HibernateProxy) proxy).getHibernateLazyInitializer().isUninitialized()) {
                    final E impl = ContextUtil.getTransactionWrapper().execute(new TransactionCallback<E>() {
                        @Override
                        public E doInTransaction(TransactionStatus status) {
                            final LazyInitializer lazy = ((HibernateProxy) proxy).getHibernateLazyInitializer();

                            lazy.setSession((SessionImplementor) sessionFactory.getCurrentSession());
                            lazy.initialize();
                            final Object impl = lazy.getImplementation();
                            lazy.unsetSession();
                            return (E) impl;
                        }
                    });
                    return impl;
                } else {
                    return (E) ((HibernateProxy) proxy).getHibernateLazyInitializer().getImplementation();
                }
            }
        }

        return entity;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <E extends Entity, A extends Annotation> Collection<AnnotationMetadata<A>>
    getAnnotationMetadata(Class<E> entityType, Class<A> annotationType) {
        final Map<String, Map<String, AnnotationMetadata<? extends Annotation>>> map = annoCache.get(entityType
                .getName());
        if (map != null && map.containsKey(annotationType.getName())) {

            List<AnnotationMetadata<A>> metadatas = new ArrayList<AnnotationMetadata<A>>();
            for (AnnotationMetadata a : map.get(annotationType.getName()).values()) {
                metadatas.add(a);
            }
            return metadatas;
        }

        return Collections.emptyList();
    }

    @Override
    public EntityMetadata getEntityMetadata(Class<? extends Entity> entityType) {
        return getEntityMetadata(entityType.getName());
    }

    @Override
    public EntityMetadata getEntityMetadata(String entityType) {
        return metaCache.get(entityType);
    }

    @Override
    public PropertyMetadata getPropertyMetadata(Class<? extends Entity> entityType, String property) {
        return getEntityMetadata(entityType).getPropertyMetadata(property);
    }

    @Override
    public PropertyMetadata getPropertyMetadata(String entityType, String property) {
        return getEntityMetadata(entityType).getPropertyMetadata(property);
    }

    @Override
    public PathMetadata getPropertyPath(Class<? extends Entity> entityType, Path path) {
        return new PathMetadataImpl(entityType, path);
    }

    @Override
    public PathMetadata getPropertyPath(Class<? extends Entity> entityType, String[] path) {
        return new PathMetadataImpl(entityType, path);
    }

    @Override
    public PathMetadata getPropertyPath(Class<? extends Entity> entityType, List<PropertyMetadata> path) {
        String[] steps = new String[path.size()];
        int index = 0;
        for (PropertyMetadata item : path) {
            steps[index] = item.getName();
            index++;
        }

        return new PathMetadataImpl(entityType, steps);
    }

    @Override
    public PathMetadata getPropertyPath(PropertyMetadata propertyMetadata) {
        return new PathMetadataImpl(propertyMetadata.getEntityMetadata().getEntityType(),
                new Path(propertyMetadata.getName()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Entity> E newInstance(Class<? extends Entity> entityType) {
        try {
            if (entityType.isInterface()) {
                entityType = getEntityMetadata(entityType).getMappedClass();
            }

            return (E) ReflectHelper.getDefaultConstructor(entityType).newInstance((Object[]) null);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void refreshMetaCache() {
        ((SessionFactoryImpl) sessionFactory).registerEntityNameResolver(EntityNameResolverImpl.INSTANCE);

        annoCache.clear();
        for (final ClassMetadata classMetadata : sessionFactory.getAllClassMetadata().values()) {
            final AnnotationReader reader = new AnnotationReader(classMetadata.getEntityName());

            ReflectionUtils.doWithFields(classMetadata.getMappedClass(), reader);

            if (reader.map.size() > 0) {
                annoCache.put(classMetadata.getEntityName(), reader.map);
            }
        }

        metaCache.clear();
        //first pass
        for (final ClassMetadata classMetadata : sessionFactory.getAllClassMetadata().values()) {
            if (!metaCache.containsKey(classMetadata.getEntityName())) {
                metaCache.put(classMetadata.getEntityName(), new EntityMetadataImpl(classMetadata));
            }
        }

        //second pass
        for (EntityMetadata entityMetadata : metaCache.values()) {
            ((EntityMetadataImpl) entityMetadata).initUniqueKeyConstraints();
        }

        ensureAdministratorExists();
    }

    @Override
    public Collection<EntityMetadata> getEntityMetadatas() {
        List<EntityMetadata> metadatas = new ArrayList<EntityMetadata>(metaCache.values());
        Collections.sort(metadatas, new Comparator<EntityMetadata>() {
            @Override
            public int compare(EntityMetadata o1, EntityMetadata o2) {
                return (o1.getSchema() + o1.getFriendlyName()).compareTo(o2.getSchema() + o2.getFriendlyName());
            }
        });
        return Collections.unmodifiableCollection(metadatas);
    }

    // -------------------------- OTHER METHODS --------------------------

    private void ensureAdministratorExists() {
        UserIdentity userAdmin = ContextUtil.getSecurityService().getAdministratorIdentity();

        Query query = ContextUtil.getEntityFactory().buildQuery(RoleIdentity.class);
        query.addCriterion(new Path(RoleIdentity.FLD_AUTHORITY), Condition.EQ, RoleIdentity.ROLE_ADMINISTRATOR);
        RoleIdentity roleAdmin = ContextUtil.getDRS().findUniqueByQuery(query);
        if (roleAdmin == null) {
            roleAdmin = ContextUtil.getEntityFactory().buildRoleIdentity();
            roleAdmin.setCode(RoleIdentity.ROLE_ADMINISTRATOR);
            ContextUtil.getDWS().save(roleAdmin);
        }

        query = ContextUtil.getEntityFactory().buildQuery(RoleMembers.class);
        query.addCriterion(new Path(RoleMembers.FLD_ROLE), Condition.EQ, roleAdmin);
        query.addCriterion(new Path(RoleMembers.FLD_USER), Condition.EQ, userAdmin);
        RoleMembers adminMembers = ContextUtil.getDRS().findUniqueByQuery(query);
        if (adminMembers == null) {
            adminMembers = ContextUtil.getEntityFactory().buildRoleMembers();
            adminMembers.setRole(roleAdmin);
            adminMembers.setUser(userAdmin);
            ContextUtil.getDWS().save(adminMembers);
        }

        CoreUtil.addSystemLock(userAdmin);
        CoreUtil.addSystemLock(roleAdmin);
        CoreUtil.addSystemLock(adminMembers);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Entity> getEntityType(String entityName) {
        try {
            return (Class<? extends Entity>) Class.forName(entityName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(entityName + " is not a valid Entity implementation");
        }
    }

    // -------------------------- INNER CLASSES --------------------------

    private class AnnotationReader implements FieldCallback {
        public final Map<String, Map<String, AnnotationMetadata<? extends Annotation>>> map = new HashMap<String,
                Map<String, AnnotationMetadata<? extends Annotation>>>();

        private final String declaringType;

        public AnnotationReader(String declaringType) {
            this.declaringType = declaringType;
        }

        private <A extends Annotation> void appendMetadata(Class<A> annotationType, AnnotationMetadata<A> metadata) {
            if (!map.containsKey(annotationType.getName())) {
                map.put(annotationType.getName(), new HashMap<String, AnnotationMetadata<? extends Annotation>>());
            }
            map.get(annotationType.getName()).put(metadata.getName(), metadata);
        }

        @Override
        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
            if (field.isAnnotationPresent(InsertTimeHolder.class)) {
                appendMetadata(InsertTimeHolder.class, new AnnotationMetadataImpl<InsertTimeHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(InsertTimeHolder.class)));
            }
            if (field.isAnnotationPresent(UpdateTimeHolder.class)) {
                appendMetadata(UpdateTimeHolder.class, new AnnotationMetadataImpl<UpdateTimeHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(UpdateTimeHolder.class)));
            }
            if (field.isAnnotationPresent(StatusHolder.class)) {
                appendMetadata(StatusHolder.class, new AnnotationMetadataImpl<StatusHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(StatusHolder.class)));
            }
            if (field.isAnnotationPresent(PropertyViewer.class)) {
                appendMetadata(PropertyViewer.class, new AnnotationMetadataImpl<PropertyViewer>(getEntityType
                        (declaringType), field, field.getAnnotation(PropertyViewer.class)));
            }
            if (field.isAnnotationPresent(PropertyEditor.class)) {
                appendMetadata(PropertyEditor.class, new AnnotationMetadataImpl<PropertyEditor>(getEntityType
                        (declaringType), field, field.getAnnotation(PropertyEditor.class)));
            }
            if (field.isAnnotationPresent(Encrypted.class)) {
                appendMetadata(Encrypted.class, new AnnotationMetadataImpl<Encrypted>(getEntityType(declaringType),
                        field, field.getAnnotation(Encrypted.class)));
            }
            if (field.isAnnotationPresent(UserIdHolder.class)) {
                appendMetadata(UserIdHolder.class, new AnnotationMetadataImpl<UserIdHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(UserIdHolder.class)));
            }
            if (field.isAnnotationPresent(HtmlHolder.class)) {
                appendMetadata(HtmlHolder.class, new AnnotationMetadataImpl<HtmlHolder>(getEntityType(declaringType),
                        field, field.getAnnotation(HtmlHolder.class)));
            }
            if (field.isAnnotationPresent(EmailHolder.class)) {
                appendMetadata(EmailHolder.class, new AnnotationMetadataImpl<EmailHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(EmailHolder.class)));
            }
            if (field.isAnnotationPresent(DefaultHolder.class)) {
                appendMetadata(DefaultHolder.class, new AnnotationMetadataImpl<DefaultHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(DefaultHolder.class)));
            }
            if (field.isAnnotationPresent(ColorHolder.class)) {
                appendMetadata(ColorHolder.class, new AnnotationMetadataImpl<ColorHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(ColorHolder.class)));
            }
            if (field.isAnnotationPresent(UrlHolder.class)) {
                appendMetadata(UrlHolder.class, new AnnotationMetadataImpl<UrlHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(UrlHolder.class)));
            }
            if (field.isAnnotationPresent(MediaHolder.class)) {
                appendMetadata(MediaHolder.class, new AnnotationMetadataImpl<MediaHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(MediaHolder.class)));
            }
            if (field.isAnnotationPresent(ImageHolder.class)) {
                appendMetadata(ImageHolder.class, new AnnotationMetadataImpl<ImageHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(ImageHolder.class)));
            }
            if (field.isAnnotationPresent(EntityTypeHolder.class)) {
                appendMetadata(EntityTypeHolder.class, new AnnotationMetadataImpl<EntityTypeHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(EntityTypeHolder.class)));
            }
            if (field.isAnnotationPresent(PanelHolder.class)) {
                appendMetadata(PanelHolder.class, new AnnotationMetadataImpl<PanelHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(PanelHolder.class)));
            }
            if (field.isAnnotationPresent(ControllerHolder.class)) {
                appendMetadata(ControllerHolder.class, new AnnotationMetadataImpl<ControllerHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(ControllerHolder.class)));
            }
            if (field.isAnnotationPresent(QueryHolder.class)) {
                appendMetadata(QueryHolder.class, new AnnotationMetadataImpl<QueryHolder>(getEntityType
                        (declaringType), field, field.getAnnotation(QueryHolder.class)));
            }
        }
    }
}
