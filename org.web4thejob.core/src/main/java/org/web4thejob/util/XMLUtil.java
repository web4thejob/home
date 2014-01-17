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

package org.web4thejob.util;

import nu.xom.*;

import java.io.ByteArrayOutputStream;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class XMLUtil {

    public static Element getRootElement(String xml) {
        final Builder parser = new Builder(false);
        try {
            final Document dom = parser.build(xml, null);
            return dom.getRootElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static String getRootElementId(String xml) {
        final Builder parser = new Builder(false);
        try {
            final Document dom = parser.build(xml, null);
            Attribute id = dom.getRootElement().getAttribute("id");
            if (id != null) return id.getValue();
            else throw new IllegalStateException("root element does not have an id");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toSpringBeanXmlResource(String xml) {
        final Builder parser = new Builder(false);
        try {
            Document dom = parser.build(xml, null);
            Element root = (Element) dom.getRootElement().copy();
            if (!root.getLocalName().equals("beans")) {
                final Element beans = new Element("beans", "http://www.springframework.org/schema/beans");
                beans.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
                beans.addAttribute(new Attribute("xsi:schemaLocation", "http://www.w3.org/2001/XMLSchema-instance",
                        "http://www.springframework.org/schema/beans http://www.springframework" + "" +
                                ".org/schema/beans/spring-beans.xsd"));

                beans.appendChild(root);
                dom = new Document(beans);
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final Serializer serializer = new Serializer(out, "UTF-8");
                serializer.setIndent(2);
                serializer.write(dom);
                return out.toString("UTF-8");
            }

            return xml;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String getTextualValue(Element element) {
        for (int i = 0; i < element.getChildCount(); i++) {
            if (element.getChild(i) instanceof Text) {
                return element.getChild(i).getValue();
            }
        }
        return null;
    }

}
