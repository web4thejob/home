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

package org.web4thejob.print;

import org.web4thejob.orm.Entity;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderScheme;

import java.io.File;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface Printer {

/*
    public enum ExportFormat {
        CSV;
    }
*/

    public File print(String title, RenderScheme renderScheme, Query query, Entity entity);

    public File print(String title, RenderScheme renderScheme, Query query, List<Entity> entities);

    public File print(String title, RenderScheme renderScheme, Entity entity);

    public File print(String title, RenderScheme renderScheme, List<Entity> entities);

    public File print(String title, List<RenderScheme> renderSchemes, Query query, List<Entity> entities);

    public File print(String title, List<RenderScheme> renderSchemes, List<Entity> entities);


}
