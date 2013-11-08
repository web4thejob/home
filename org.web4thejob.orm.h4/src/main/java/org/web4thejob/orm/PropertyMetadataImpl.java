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

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.*;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.*;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.PropertyAccessorFactory;
import org.web4thejob.context.ContextUtil;

import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.*;
import java.util.Map;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

/*package*/class PropertyMetadataImpl implements PropertyMetadata, Comparable<PropertyMetadata> {
    // ------------------------------ FIELDS ------------------------------

    private static final String META_FRIENDLY_NAME = "friendlyName";
    private static final String META_INDEX = "index";
    private static final String META_DISABLE_USER_INSERT = "disable-user-insert";
    private static final String META_DISABLE_USER_UPDATE = "disable-user-update";
    private static final String META_DEFAULT_FORMAT = "default-format";
    private static final String META_DEFAULT_STYLE = "default-style";
    private static final String META_DEFAULT_ALIGN = "default-align";
    private static final String META_DEFAULT_WIDTH = "default-width";
    private static final String META_DEFAULT_HEIGHT = "default-height";

    private final EntityMetadataImpl entityMetadata;
    private final String associatedEntityName;
    private final Property property;
    private final int maxLength;
    private final int index;
    private final boolean disableUserInsert;
    private final boolean disableUserUpdate;
    private final boolean identifier;
    private final boolean identityIdentifier;
    private final boolean formula;
    private final String friendlyName;
    private final String format;
    private final String align;
    private final String style;
    private final String width;
    private final String height;
    private final Map<String, AnnotationMetadata<? extends Annotation>> annotations;

    // --------------------------- CONSTRUCTORS ---------------------------

    public PropertyMetadataImpl(EntityMetadataImpl entityMetadata, String propertyName) {
        this(entityMetadata, propertyName, null);
    }

    @SuppressWarnings("unchecked")
    protected PropertyMetadataImpl(EntityMetadataImpl entityMetadata, String propertyName,
                                   Class<? extends Entity> subclassAssociation) {
        this.entityMetadata = entityMetadata;
        this.property = entityMetadata.getPersistentClass().getProperty(propertyName);

        if (property.getType() instanceof AssociationType) {
            String tempName = ((AssociationType) property.getType()).getAssociatedEntityName(ContextUtil
                    .getBean(SessionFactoryImplementor.class));

            if (subclassAssociation != null) {
                tempName = subclassAssociation.getCanonicalName();
            }
            associatedEntityName = tempName;
        } else {
            associatedEntityName = null;
        }


        int maxLength = 0;
        boolean hasFormula = false;
        for (final Iterator<?> iter = property.getColumnIterator(); iter.hasNext(); ) {
            final Object item = iter.next();
            if (item instanceof Column && ((Column) item).getLength() > 0) {
                maxLength += ((Column) item).getLength();
            } else if (item instanceof Formula) {
                hasFormula = true;
            }
        }
        this.formula = hasFormula;
        this.maxLength = maxLength;
        this.identifier = property.getName().equals(entityMetadata.getIdentifierName());
        this.annotations = getAnnotations();
        this.identityIdentifier = this.identifier && MetaUtil.isIdentityKey(entityMetadata.getPersistentClass()
                .getIdentifier());

        if (MetaUtil.hasMetaAttribute(property, META_FRIENDLY_NAME)) {
            friendlyName = MetaUtil.getMetaAttribute(property, META_FRIENDLY_NAME);
        } else {
            friendlyName = property.getName();
        }
        if (MetaUtil.hasMetaAttribute(property, META_INDEX)) {
            index = Integer.valueOf(MetaUtil.getMetaAttribute(property, META_INDEX));
        } else {
            index = MetaUtil.getHibernatePropertyIndex(entityMetadata.getName(), property.getName());
        }
        if (MetaUtil.hasMetaAttribute(property, META_DISABLE_USER_INSERT)) {
            disableUserInsert = Boolean.valueOf(MetaUtil.getMetaAttribute(property, META_DISABLE_USER_INSERT));
        } else {
            disableUserInsert = false;
        }
        if (MetaUtil.hasMetaAttribute(property, META_DISABLE_USER_UPDATE)) {
            disableUserUpdate = Boolean.valueOf(MetaUtil.getMetaAttribute(property, META_DISABLE_USER_UPDATE));
        } else {
            disableUserUpdate = false;
        }
        if (MetaUtil.hasMetaAttribute(property, META_DEFAULT_FORMAT)) {
            format = MetaUtil.getMetaAttribute(property, META_DEFAULT_FORMAT);
        } else {
            format = buildDefaultPattern();
        }
        if (MetaUtil.hasMetaAttribute(property, META_DEFAULT_STYLE)) {
            style = MetaUtil.getMetaAttribute(property, META_DEFAULT_STYLE);
        } else {
            style = null;
        }
        if (MetaUtil.hasMetaAttribute(property, META_DEFAULT_ALIGN)) {
            align = MetaUtil.getMetaAttribute(property, META_DEFAULT_ALIGN);
        } else {
            align = buildDefaultAlign();
        }
        if (MetaUtil.hasMetaAttribute(property, META_DEFAULT_WIDTH)) {
            width = MetaUtil.getMetaAttribute(property, META_DEFAULT_WIDTH);
        } else {
            width = null;
        }
        if (MetaUtil.hasMetaAttribute(property, META_DEFAULT_HEIGHT)) {
            height = MetaUtil.getMetaAttribute(property, META_DEFAULT_HEIGHT);
        } else {
            height = null;
        }
    }

    private Map<String, AnnotationMetadata<? extends Annotation>> getAnnotations() {
        final Map<String, AnnotationMetadata<? extends Annotation>> propAnnotations = new HashMap<String,
                AnnotationMetadata<? extends Annotation>>();
        if (MetaReaderServiceImpl.annoCache.containsKey(entityMetadata.getName())) {
            for (final String annotation : MetaReaderServiceImpl.annoCache.get(entityMetadata.getName()).keySet()) {
                if (MetaReaderServiceImpl.annoCache.get(entityMetadata.getName()).get(annotation).containsKey(getName
                        ())) {
                    propAnnotations.put(annotation, MetaReaderServiceImpl.annoCache.get(entityMetadata.getName()).get
                            (annotation).get(getName()));
                }
            }
        }

        if (propAnnotations.size() > 0) {
            return propAnnotations;
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public String getName() {
        return property.getName();
    }

    private String buildDefaultPattern() {
        if (property.getType() instanceof LongType || property.getType() instanceof IntegerType || property.getType()
                instanceof ShortType || property.getType() instanceof BigIntegerType) {
            return "number,integer";
        } else if (property.getType() instanceof DoubleType || property.getType() instanceof FloatType || property
                .getType() instanceof BigDecimalType) {
            return "number,#,##0.00";
        } else if (property.getType() instanceof CurrencyType) {
            return "number,currency";
        } else if (property.getType() instanceof DateType) {
            return "date";
        } else if (property.getType() instanceof TimeType) {
            return "time";
        } else if (property.getType() instanceof TimestampType) {
            return "date,long";
        } else {
            return null;
        }
    }

    private String buildDefaultAlign() {
        if (Number.class.isAssignableFrom(getJavaType())) {
            return "right";
        } else if (Date.class.isAssignableFrom(getJavaType())) {
            return "center";
        } else if (String.class.isAssignableFrom(getJavaType()) && getMaxLength() > 0 && getMaxLength() <= 5) {
            return "center";
        }
        return null;
    }

    @Override
    public Class<?> getJavaType() {
        return property.getType().getReturnedClass();
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public String getAlign() {
        return align;
    }

    @Override
    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public boolean isIdentifier() {
        return identifier;
    }

    @Override
    public boolean isIdentityIdentifier() {
        return identityIdentifier;
    }

    // ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(entityMetadata.getName()).append("|").append(getName());
        return sb.toString();
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface Comparable ---------------------

    @Override
    public int compareTo(PropertyMetadata other) {
        final Integer i1 = getIndex();
        final Integer i2 = other.getIndex();

        final int res = i1.compareTo(i2);
        if (res != 0) {
            return res;
        } else {
            return Integer.valueOf(hashCode()).compareTo(other.hashCode());
        }
    }

    // --------------------- Interface PropertyMetadata ---------------------

    @Override
    public <E extends Entity> void deproxyValue(E entity) {
        if (isProxyValue(entity)) {
            setValue(entity, getValue(entity));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        if (isAnnotatedWith(annotationType)) {
            return (A) annotations.get(annotationType.getName()).getAnnotation();
        }
        return null;
    }

    @Override
    public EntityMetadata getAssociatedEntityMetadata() {
        if (associatedEntityName != null) {
            return ContextUtil.getMRS().getEntityMetadata(associatedEntityName);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, E extends Entity> T getValue(E entity) {
        if (isOneToManyType()) {
            throw new RuntimeException("cannot call getValue for a Collection property");
        }

        if (entity instanceof HibernateProxy) {
            entity = ContextUtil.getMRS().deproxyEntity(entity);
        }

        T value;
        if (!isIdentifier()) {
            value = (T) entityMetadata.getClassMetadata().getPropertyValue(entity, getName());
        } else {
            value = (T) PropertyAccessorFactory.forDirectFieldAccess(entity).getPropertyValue(getName());
        }

        if (value != null && value instanceof HibernateProxy) {
            value = (T) ContextUtil.getMRS().deproxyEntity((Entity) value);
        }

        return value;
    }

    @Override
    public boolean hasColumn(String name) {
        for (final Iterator<?> iter = property.getColumnIterator(); iter.hasNext(); ) {
            final Object item = iter.next();
            if (item instanceof Selectable) {
                if (((Selectable) item).getText().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasColumns() {
        return property.getColumnSpan() > 0;
    }

    @Override
    public <A extends Annotation> boolean isAnnotatedWith(Class<A> annotationType) {
        return annotations.containsKey(annotationType.getName());
    }

    @Override
    public boolean isAssociatedWith(PropertyMetadata propertyMetadata) {
        if (!(isAssociationType() && propertyMetadata.isAssociationType())) {
            return false;
        } else if (!getAssociatedEntityMetadata().getEntityType().isAssignableFrom(propertyMetadata.getEntityMetadata
                ().getEntityType())) {
            return false;
        } else if (isOneToOneType() && propertyMetadata.isOneToOneType()) {
            // OneToOne has no columns so since the entity type is the same
            // (checked above) it is safe to assume that properties are
            // associated.
            return true;
        } else if (!propertyMetadata.hasColumns()) {
            return propertyMetadata.isAssociatedWith(this);
        }

        final Joinable j = ((AssociationType) property.getType()).getAssociatedJoinable(ContextUtil.getBean
                (SessionFactoryImplementor.class));

        for (final String p : j.getKeyColumnNames()) {
            String p2 = p.replaceAll("\"", "");
            p2 = p2.replaceAll("`", "");
            if (propertyMetadata.hasColumn(p2)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAssociationType() {
        return property.getType() instanceof AssociationType;
    }

    @Override
    public boolean isSubclassType() {
        return false;
    }

    @Override
    public boolean isBlobType() {
        return property.getType() instanceof MaterializedBlobType;
    }

    @Override
    public boolean isClobType() {
        return property.getType() instanceof MaterializedClobType;
    }

    @Override
    public boolean isTextType() {
        return property.getType() instanceof TextType;
    }

    @Override
    public PropertyMetadata castForSubclass(Class<? extends Entity> subclass) {
        if (isAssociationType()) {
            for (Class<? extends Entity> subtype : getAssociatedEntityMetadata().getSubclasses()) {
                if (subtype.equals(subclass)) {
                    return new SubclassPropertyMetadataImpl(entityMetadata, subtype, getName());
                }
            }
        }
        return null;
    }

    @Override
    public boolean isCompositeType() {
        return property.getType() instanceof CompositeType;
    }

    @Override
    public boolean isInsertable() {
        return !entityMetadata.isReadOnly() && property.isInsertable() && !disableUserInsert && !formula &&
                !isIdentityIdentifier() && PropertyGeneration.NEVER == property.getGeneration();
    }

    @Override
    public boolean isManyToOneType() {
        return property.getType() instanceof ManyToOneType;
    }

    @Override
    public boolean isUniqueKeyWith(String propertyName) {

        if (getName().equals(propertyName)) {
            return false;
        }

        PersistentClass pc = entityMetadata.getPersistentClass();
        while (pc != null) {
            Table table = pc.getTable();
            for (Iterator<?> iterUK = table.getUniqueKeyIterator(); iterUK.hasNext(); ) {
                UniqueKey uniqueKey = (UniqueKey) iterUK.next();

                if (!(uniqueKey.getColumnSpan() > 1)) {
                    continue;
                }

                int totcols = 0;
                boolean keyMembers = true;

                for (PropertyMetadataImpl propertyMetadata : Arrays.asList(this,
                        (PropertyMetadataImpl) entityMetadata.getPropertyMetadata(propertyName))) {
                    if (!propertyMetadata.hasColumns()) {
                        return false;
                    }

                    for (final Iterator<?> iterCol = propertyMetadata.property.getColumnIterator(); iterCol.hasNext()
                            ; ) {
                        Column column = (Column) iterCol.next();
                        keyMembers &= column != null && uniqueKey.containsColumn(column);
                    }
                    totcols += propertyMetadata.property.getColumnSpan();
                }

                if (keyMembers && totcols == uniqueKey.getColumnSpan()) {
                    return true;
                }

            }

            pc = pc.getSuperclass();

        }

        return false;
    }

    @Override
    public boolean isMemberOfUniqueKey() {
        if (!hasColumns()) {
            return false;
        }

        PersistentClass pc = entityMetadata.getPersistentClass();
        while (pc != null) {
            Table table = pc.getTable();
            for (Iterator<?> iterUK = table.getUniqueKeyIterator(); iterUK.hasNext(); ) {
                UniqueKey uniqueKey = (UniqueKey) iterUK.next();
                boolean member = true;
                for (final Iterator<?> iterCol = property.getColumnIterator(); iterCol.hasNext(); ) {
                    Column column = (Column) iterCol.next();
                    member &= column != null && uniqueKey.containsColumn(column);
                }

                if (member) {
                    return true;
                }
            }
            pc = pc.getSuperclass();
        }

        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isOfJavaType(Class<?> type) {
        return property.getType().getReturnedClass().isAssignableFrom(type);
    }

    @Override
    public boolean isOneToManyType() {
        return property.getType() instanceof CollectionType;
    }

    @Override
    public boolean isOneToOneType() {
        return property.getType() instanceof OneToOneType;
    }

    @Override
    public boolean isNumericType() {
        return Number.class.isAssignableFrom(property.getType().getReturnedClass());
    }

    @Override
    public boolean isOptional() {
        return property.isOptional() && !isMandatory();
    }


    protected boolean isMandatory() {
        final Validator validator = ContextUtil.getBean(Validator.class);
        final BeanDescriptor beanDescriptor = validator.getConstraintsForClass(getEntityMetadata().getMappedClass());

        if (!beanDescriptor.isBeanConstrained()) {
            return false;
        }
        PropertyDescriptor propertyDescriptor = beanDescriptor.getConstraintsForProperty(property.getName());
        if (propertyDescriptor == null) {
            return false;
        }
        Set<ConstraintDescriptor<?>> constraints = propertyDescriptor.findConstraints().declaredOn(ElementType.FIELD)
                .unorderedAndMatchingGroups(Default.class).getConstraintDescriptors();

        for (ConstraintDescriptor<?> constraint : constraints) {
            if (NotNull.class.isInstance(constraint.getAnnotation()) || NotBlank.class.isInstance(constraint
                    .getAnnotation()) || NotEmpty.class.isInstance(constraint.getAnnotation())) {
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean isTimestampType() {
        return property.getType() instanceof TimestampType;
    }

    @Override
    public <E extends Entity> boolean isProxyValue(E entity) {
        if (!isIdentifier()) {
            if (entity instanceof HibernateProxy) {
                entity = ContextUtil.getMRS().deproxyEntity(entity);
            }

            Object value = entityMetadata.getClassMetadata().getPropertyValue(entity, getName());
            return value != null && value instanceof HibernateProxy;
        } else {
            return false;
        }
    }

    @Override
    public boolean isUpdateable() {
        return !entityMetadata.isReadOnly() && property.isUpdateable() && !disableUserUpdate && !formula &&
                !isIdentityIdentifier() && (PropertyGeneration.NEVER == property.getGeneration() ||
                PropertyGeneration.INSERT == property.getGeneration());
    }

    @Override
    public <E extends Entity> void setValue(E entity, Object value) {
        if (!identifier) {
            entityMetadata.getClassMetadata().setPropertyValue(entity, getName(), value);
        } else {
            PropertyAccessorFactory.forDirectFieldAccess(entity).setPropertyValue(getName(), value);
        }
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!PropertyMetadata.class.isInstance(obj)) {
            return false;
        } else {
            final PropertyMetadata other = (PropertyMetadata) obj;
            return entityMetadata.equals(other.getEntityMetadata()) && getName().equals(other.getName());
        }
    }

    @Override
    public String getWidth() {
        return width;
    }

    @Override
    public String getHeight() {
        return height;
    }
}
