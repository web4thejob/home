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

package org.web4thejob.orm;

import org.apache.log4j.Logger;
import org.hibernate.mapping.*;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

/*package*/class EntityMetadataImpl implements EntityMetadata {
    // ------------------------------ FIELDS ------------------------------
    private static final String META_FRIENDLY_NAME = "friendlyName";
    private static final String META_TABLE_SUBSET = "tableSubset";
    private static final String META_TABLE_CACHED = "cached";
    private static final String META_DENY_ADDNEW = "deny-add-new";
    private static final String META_DENY_UPDATE = "deny-update";
    private static final String META_DENY_DELETE = "deny-delete";
    private static final Logger logger = Logger.getLogger(EntityMetadataImpl.class);
    final private SortedSet<PropertyMetadata> propertySet = new TreeSet<PropertyMetadata>();
    final private HashMap<String, PropertyMetadata> propertyMap = new HashMap<String, PropertyMetadata>();
    final private String identifier;
    final private Type identifierType;
    final private Class<? extends Entity> entityType;
    final private ClassMetadata classMetadata;
    final private boolean versioned;
    final private PersistentClass persistentClass;
    final private String friendlyName;
    final private List<Class<? extends Entity>> subclasses;
    final private boolean tableSubset;
    final private boolean cached;
    final private boolean denyAddNew;
    final private boolean denyDelete;
    final private boolean denyUpdate;
    private List<UniqueKeyConstraint> uniqueKeyConstraints;

    // --------------------------- CONSTRUCTORS ---------------------------

    @SuppressWarnings("unchecked")
    public EntityMetadataImpl(ClassMetadata classMetadata) {
        this.classMetadata = classMetadata;
        this.persistentClass = ContextUtil.getBean(HibernateConfiguration.class).getConfiguration().getClassMapping
                (classMetadata.getEntityName());
        entityType = getEntityType(classMetadata.getEntityName());
        identifier = classMetadata.getIdentifierPropertyName();
        identifierType = classMetadata.getIdentifierType();
        versioned = classMetadata.getVersionProperty() >= 0;
        if (MetaUtil.hasMetaAttribute(this.persistentClass, META_FRIENDLY_NAME)) {
            friendlyName = MetaUtil.getMetaAttribute(this.persistentClass, META_FRIENDLY_NAME);
        } else {
            friendlyName = classMetadata.getEntityName();
        }
        if (MetaUtil.hasMetaAttribute(this.persistentClass, META_TABLE_SUBSET)) {
            tableSubset = Boolean.parseBoolean(MetaUtil.getMetaAttribute(this.persistentClass, META_TABLE_SUBSET));
        } else {
            tableSubset = false;
        }
        if (MetaUtil.hasMetaAttribute(this.persistentClass, META_TABLE_CACHED)) {
            cached = Boolean.parseBoolean(MetaUtil.getMetaAttribute(this.persistentClass, META_TABLE_CACHED));
        } else {
            cached = false;
        }

        boolean isAbstract = persistentClass.isAbstract() != null ? persistentClass.isAbstract() : false;
        if (MetaUtil.hasMetaAttribute(this.persistentClass, META_DENY_ADDNEW)) {
            denyAddNew = isAbstract || Boolean.parseBoolean(MetaUtil
                    .getMetaAttribute(this
                            .persistentClass, META_DENY_ADDNEW));
        } else {
            denyAddNew = isAbstract;
        }

        if (MetaUtil.hasMetaAttribute(this.persistentClass, META_DENY_UPDATE)) {
            denyUpdate = Boolean.parseBoolean(MetaUtil.getMetaAttribute(this.persistentClass, META_DENY_UPDATE));
        } else {
            denyUpdate = false;
        }

        if (MetaUtil.hasMetaAttribute(this.persistentClass, META_DENY_DELETE)) {
            denyDelete = Boolean.parseBoolean(MetaUtil.getMetaAttribute(this.persistentClass, META_DENY_DELETE));
        } else {
            denyDelete = false;
        }

        // id
        PropertyMetadata propertyMetadata = new PropertyMetadataImpl(this, classMetadata.getIdentifierPropertyName());
        propertySet.add(propertyMetadata);
        propertyMap.put(propertyMetadata.getName(), propertyMetadata);

        // properties
        int i = 0;
        for (final String propertyName : classMetadata.getPropertyNames()) {
            if (!isBackref(propertyName) && !isVersionProperty(i) && !isDiscriminatorDuplicate(propertyName)) {
                propertyMetadata = new PropertyMetadataImpl(this, propertyName);
                propertySet.add(propertyMetadata);
                propertyMap.put(propertyMetadata.getName(), propertyMetadata);
            } else {
                logger.debug("ignoring property of " + classMetadata.getEntityName() + ": " + propertyName);
            }
            i++;
        }

        // subclasses
        if (persistentClass.hasSubclasses()) {
            subclasses = new ArrayList<Class<? extends Entity>>();
            for (final Iterator<?> iter = persistentClass.getSubclassIterator(); iter.hasNext(); ) {
                Subclass subclass = (Subclass) iter.next();
                try {
                    subclasses.add((Class<? extends Entity>) Class.forName(subclass.getEntityName()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            subclasses = Collections.emptyList();
        }
    }

    private boolean isDiscriminatorDuplicate(String propertyName) {
        if (persistentClass instanceof RootClass) return false;
        if (persistentClass.getRootClass().getDiscriminator() == null) return false;

        Object dcolumn = persistentClass.getRootClass().getDiscriminator().getColumnIterator().next();
        Object pcolumn = persistentClass.getProperty(propertyName).getColumnIterator().next();
        if (dcolumn instanceof Column && pcolumn instanceof Column) {
            return ((Column) dcolumn).getName().equals(((Column) pcolumn).getName());
        }

        return false;
    }


    @SuppressWarnings("unchecked")
    private Class<? extends Entity> getEntityType(String entityName) {
        try {
            return (Class<? extends Entity>) Class.forName(entityName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(entityName + " is not a valid Entity implementation");
        }
    }

    private boolean isBackref(String propertyName) {
        return propertyName.startsWith("_") && propertyName.toLowerCase().endsWith("backref");
    }

    private boolean isVersionProperty(int index) {
        return classMetadata.getVersionProperty() == index;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    public ClassMetadata getClassMetadata() {
        return classMetadata;
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return entityType;
    }

    @Override
    public String getIdentifierName() {
        return identifier;
    }

    public Type getIdentifierType() {
        return identifierType;
    }

    public PersistentClass getPersistentClass() {
        return persistentClass;
    }

    @Override
    public boolean isVersioned() {
        return versioned;
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface EntityMetadata ---------------------

    @Override
    public String getSchema() {
        if (StringUtils.hasText(persistentClass.getTable().getSchema())) {
            return persistentClass.getTable().getSchema();
        } else {
            return ContextUtil.getBean(HibernateConfiguration.class).getConfiguration().getProperty("hibernate" +
                    ".default_schema");
        }
    }

    @Override
    public String getName() {
        return classMetadata.getEntityName();
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public <E extends Entity> Integer getVersion(E entity) {
        return (Integer) classMetadata.getVersion(entity);
    }

    @Override
    public SortedSet<PropertyMetadata> getPropertiesMetadata() {
        return propertySet;
    }

    @Override
    public PropertyMetadata getPropertyMetadata(String property) {
        PropertyMetadata propertyMetadata = propertyMap.get(property);

        if (propertyMetadata == null) {
            StringTokenizer tokenizer = new StringTokenizer(property, "()", false);
            if (tokenizer.hasMoreTokens()) {
                String type = tokenizer.nextToken();
                String name = tokenizer.nextToken();

                propertyMetadata = propertyMap.get(name);
                return propertyMetadata.castForSubclass(ContextUtil.getEntityFactory().toEntityType(type));
            }
        }

        return propertyMetadata;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends Entity> getMappedClass() {
        return classMetadata.getMappedClass();
    }

    @Override
    public boolean isReadOnly() {
        return !persistentClass.isMutable();
    }

    @Override
    public <A extends Annotation> PropertyMetadata getAnnotatedProperty(Class<A> annotation) {
        for (PropertyMetadata propertyMetadata : getPropertiesMetadata()) {
            if (propertyMetadata.getAnnotation(annotation) != null) {
                return propertyMetadata;
            }
        }
        return null;
    }

    @Override
    public List<UniqueKeyConstraint> getUniqueConstraints() {
        return uniqueKeyConstraints;
    }

    // --------------------- Interface SecuredResource ---------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!EntityMetadata.class.isInstance(obj)) {
            return false;
        } else {
            final EntityMetadata other = (EntityMetadata) obj;
            return getName().equals(other.getName());
        }
    }

    @Override
    public boolean isCached() {
        return cached || persistentClass.getCacheConcurrencyStrategy() != null;
    }

    @Override
    public boolean isTableSubset() {
        return tableSubset;
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(classMetadata.getMappedClass().getModifiers());
    }

    @Override
    public String toString() {
        return entityType.toString();
    }

    @Override
    public boolean isDenyAddNew() {
        return denyAddNew;
    }

    @Override
    public boolean isDenyDelete() {
        return denyDelete;
    }

    @Override
    public boolean isDenyUpdate() {
        return denyUpdate;
    }

    @Override
    public Object getVersionValue(Entity entity) {
        if (entity == null) return null;
        if (!versioned) return null;
        if (!entity.getEntityType().equals(getEntityType())) return null;

        return classMetadata.getPropertyValue(entity, persistentClass.getVersion().getName());
    }

    @Override
    public void setVersionValue(Entity entity, Object value) {
        if (entity == null) return;
        if (!versioned) return;
        if (!entity.getEntityType().equals(getEntityType())) return;

        classMetadata.setPropertyValue(entity, persistentClass.getVersion().getName(), value);
    }

    @Override
    public List<Class<? extends Entity>> getSubclasses() {
        return subclasses;
    }

    /*package*/ void initUniqueKeyConstraints() {
        // unique key constraints
        uniqueKeyConstraints = new ArrayList<UniqueKeyConstraint>();
        PersistentClass pc = persistentClass;
        while (pc != null) {
            for (final Iterator<?> iter = pc.getTable().getUniqueKeyIterator(); iter.hasNext(); ) {

                if (persistentClass.equals(pc)) {
                    uniqueKeyConstraints.add(new UniqueKeyConstraintImpl(this, (UniqueKey) iter.next()));
                } else {
                    uniqueKeyConstraints.add(new UniqueKeyConstraintImpl(MetaReaderServiceImpl.metaCache.get(pc
                            .getEntityName()), (UniqueKey) iter.next()));
                }

            }

            if (!RootClass.class.isInstance(pc)) {
                for (final Iterator<?> iter = pc.getJoinIterator(); iter.hasNext(); ) {
                    Join join = (Join) iter.next();
                    for (final Iterator<?> iter2 = join.getTable().getUniqueKeyIterator(); iter2.hasNext(); ) {
                        uniqueKeyConstraints.add(new UniqueKeyConstraintImpl(this, (UniqueKey) iter2.next()));
                    }
                }
            }

            pc = pc.getSuperclass();
        }
        uniqueKeyConstraints = Collections.unmodifiableList(uniqueKeyConstraints);

    }

    @Override
    public String getFullFriendlyName() {
        return new StringBuilder().append("[").append(getSchema()).append("] ").append(getFriendlyName()).toString();
    }
}
