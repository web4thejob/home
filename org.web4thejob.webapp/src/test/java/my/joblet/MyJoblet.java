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

package my.joblet;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.web4thejob.module.AbstractJoblet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 3.4.0
 */
public class MyJoblet extends AbstractJoblet {

    @Override
    public List<Resource> getResources() {
        List<Resource> resources = new ArrayList<Resource>();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            for (Resource resource : resolver.getResources("classpath*:my/joblet/**/*.hbm.xml")) {
                resources.add(resource);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return resources;
    }

    @Override
    public String getBasePackage() {
        return "my.joblet";
    }

    @Override
    public boolean isInstalled() {
        return false;
    }

    @Override
    public String[] getSchemas() {
        return new String[]{"myjob"};
    }

    @Override
    public int getOrdinal() {
        return 99;
    }
}
