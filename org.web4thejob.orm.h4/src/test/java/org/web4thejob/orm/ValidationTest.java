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

import junit.framework.Assert;
import org.junit.Test;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.mapping.DummyEntity;
import org.web4thejob.orm.mapping.Master1;
import org.web4thejob.orm.mapping.Master2;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class ValidationTest extends AbstractHibernateDependentTest {

    @Test
    public void validate() {
        final Master1 master1 = new Master1();
        final Set<ConstraintViolation<Entity>> violations = master1.validate();
        Assert.assertEquals(violations.size(), 2);
    }

    @Test(expected = ConstraintViolationException.class)
    public void validateException() {
        ContextUtil.getDWS().save(new Master1());
    }

    @Test
    public void validateWithExtraGroups() {
        final Master2 master2 = new Master2();
        Set<ConstraintViolation<Entity>> violations = master2.validate();
        Assert.assertEquals(violations.size(), 2);

        master2.setName("test_name");
        violations = master2.validate();
        Assert.assertEquals(violations.size(), 3);
    }

    @Test
    public void testAdhocViolations() {
        DummyEntity dummyEntity = new DummyEntity();
        Set<ConstraintViolation<Entity>> violations = dummyEntity.validate();

        Assert.assertEquals(1, violations.size());
    }

}
