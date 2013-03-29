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

package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.module.Module;
import org.web4thejob.web.panel.base.zk.AbstractZkContentPanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.*;

/**
 * @author Veniamin Isaias
 * @since 2.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultModuleInfoPanel extends AbstractZkContentPanel implements ModuleInfoPanel {
    private final Grid grid = new Grid();


    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        for (Component column : grid.getColumns().getChildren()) {
            ((Column) column).setSort("auto(" + column.getAttribute("field").toString() + ")");
        }
    }

    public DefaultModuleInfoPanel() {
        ZkUtil.setParentOfChild((Component) base, grid);
//        grid.setWidth("100%");
        grid.setVflex("true");
        grid.setSpan(true);
        grid.setOddRowSclass("w4tj-listbox-oddRow");
        new Columns().setParent(grid);
        new Rows().setParent(grid);

        Column column = new Column("Name");
        column.setStyle("text-align:center;");
        column.setParent(grid.getColumns());
        column.setAttribute("field", "name");

        column = new Column("Type");
        column.setStyle("text-align:center;");
        column.setParent(grid.getColumns());
        column.setHflex("min");
        column.setAttribute("field", "type");

        column = new Column("Version");
        column.setStyle("text-align:center;");
        column.setParent(grid.getColumns());
        column.setHflex("min");
        column.setAttribute("field", "version");

        column = new Column("File Name");
        column.setStyle("text-align:center;");
        column.setParent(grid.getColumns());
        column.setAttribute("field", "packageName");

        column = new Column("License");
        column.setStyle("text-align:center;");
        column.setParent(grid.getColumns());
        column.setHflex("min");
        column.setAttribute("field", "licenseName");

        column = new Column("Publisher");
        column.setStyle("text-align:center;");
        column.setParent(grid.getColumns());
        column.setAttribute("field", "organizationName");

        column = new Column("Order");
        column.setStyle("text-align:center;");
        column.setParent(grid.getColumns());
        column.setHflex("min");
        column.setAttribute("field", "ordinal");

        grid.setModel(new ListModelList<Module>(ContextUtil.getModules()));

        grid.setRowRenderer(new RowRenderer<Module>() {
            @Override
            public void render(Row row, Module data, int index) throws Exception {
                A a;

                if (StringUtils.hasText(data.getProjectUrl())) {
                    a = new A(data.getName());
                    a.setHref(data.getProjectUrl());
                    a.setParent(row);
                    a.setTarget("_default");
                } else {
                    new Label(data.getName()).setParent(row);
                }

                new Label(data.getType().toString().toLowerCase()).setParent(row);

                new Label(data.getVersion()).setParent(row);

                new Label(data.getFileName()).setParent(row);

                if (StringUtils.hasText(data.getLicenseUrl())) {
                    a = new A(data.getLicenseName());
                    a.setHref(data.getLicenseUrl());
                    a.setParent(row);
                    a.setTarget("_default");
                } else {
                    new Label(data.getLicenseName()).setParent(row);
                }

                if (StringUtils.hasText(data.getOrganizationUrl())) {
                    a = new A(data.getOrganizationName());
                    a.setHref(data.getOrganizationUrl());
                    a.setParent(row);
                    a.setTarget("_default");
                } else {
                    new Label(data.getOrganizationName()).setParent(row);
                }

                new Label(Integer.valueOf(data.getOrdinal()).toString()).setParent(row);
            }
        });
    }


}
