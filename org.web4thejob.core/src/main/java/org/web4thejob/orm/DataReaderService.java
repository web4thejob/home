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

package org.web4thejob.orm;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.web4thejob.orm.query.Query;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Service for reading {@link Entity} instances from the application datastore. Usually invoked through
 * {@link org.web4thejob.context.ContextUtil#getDRS() ContextUtil.getDRS()}.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Transactional
@Repository
public interface DataReaderService {

    public <E extends Entity> E findById(Class<E> entityType, Serializable id);

    public <E extends Entity> List<E> findByQuery(Query query);

    public <E extends Entity> E findFirstByQuery(Query query);

    public <E extends Entity> E findUniqueByQuery(Query query);

    public <E extends Entity> E get(Class<E> entityType, Serializable id);

    public <E extends Entity> List<E> getAll(Class<E> entityType);

    public <E extends Entity> E getOne(Class<E> entityType);

    public <E extends Entity> E refresh(E entity);

    public void evictCache();

}
