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

package org.web4thejob.print;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.annotation.HtmlHolder;
import org.web4thejob.orm.parameter.Category;
import org.web4thejob.orm.parameter.Key;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Criterion;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.orm.scheme.SchemeType;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nMessages;

import java.io.*;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Component
public class CsvPrinter implements Printer {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Printer ---------------------

    private static void writeLine(CSVWriter csv, ConversionService conversionService, Entity entity,
                                  RenderScheme renderScheme) {
        List<String> line = new ArrayList<String>();
        if (renderScheme.getSchemeType() == SchemeType.ENTITY_SCHEME) {
            for (RenderElement item : renderScheme.getElements()) {
                if (item.getPropertyPath().getLastStep().isBlobType()) continue;

                line.clear();
                line.add(item.getFriendlyName());
                Object value = item.getPropertyPath().getValue(entity);
                if (value != null) {
                    if (item.getPropertyPath().getLastStep().isAnnotatedWith(HtmlHolder.class)) {
                        line.add(getActualTextFromHtml(value.toString()));
                    } else {
                        line.add(conversionService.convert(value, String.class));
                    }
                } else {
                    line.add("");
                }
                csv.writeNext(line.toArray(new String[line.size()]));
            }
        } else {
            line.clear();
            for (RenderElement item : renderScheme.getElements()) {
                if (item.getPropertyPath().getLastStep().isBlobType()) continue;

                Object value = item.getPropertyPath().getValue(entity);
                if (value != null) {
                    if (item.getPropertyPath().getLastStep().isAnnotatedWith(HtmlHolder.class)) {
                        line.add(getActualTextFromHtml(value.toString()));
                    } else {
                        line.add(conversionService.convert(value, String.class));
                    }
                } else {
                    line.add("");
                }
            }
            csv.writeNext(line.toArray(new String[line.size()]));
        }
    }

    @Override
    public File print(String title, RenderScheme renderScheme, Query query, Entity entity) {
        List<RenderScheme> renderSchemes = new ArrayList<RenderScheme>(1);
        renderSchemes.add(renderScheme);
        List<Entity> entities = new ArrayList<Entity>(1);
        entities.add(entity);
        return print(title, renderSchemes, query, entities);
    }

    @Override
    public File print(String title, RenderScheme renderScheme, Query query, List<Entity> entities) {
        Assert.notNull(renderScheme);
        Assert.isTrue(renderScheme.getSchemeType() == SchemeType.LIST_SCHEME);

        if (entities == null) {
            Assert.notNull(query);
            entities = ContextUtil.getDRS().findByQuery(query);
        }

        File file;
        try {
            String crlf = System.getProperty("line.separator");
            file = createTempFile();
            BufferedWriter writer = createFileStream(file);
            writer.write(title + crlf);
            writer.newLine();


            if (query != null && query.hasMasterCriterion()) {
                writer.write(describeMasterCriteria(query));
                writer.newLine();
            }

            if (query != null) {
                writer.write(describeCriteria(query));
                writer.newLine();
            }

            CSVWriter csv = new CSVWriter(writer);
            List<String> header = new ArrayList<String>();
            for (RenderElement item : renderScheme.getElements()) {
                if (item.getPropertyPath().getLastStep().isBlobType()) continue;
                header.add(item.getFriendlyName());
            }
            csv.writeNext(header.toArray(new String[header.size()]));

            ConversionService conversionService = ContextUtil.getBean(ConversionService.class);
            for (final Entity entity : entities) {
                writeLine(csv, conversionService, entity, renderScheme);
            }

            writer.newLine();

            //timestamp
            List<String> line = new ArrayList<String>();
            line.add(L10nMessages.L10N_LABEL_TIMESTAMP.toString());
            MessageFormat df = new MessageFormat("");
            df.setLocale(CoreUtil.getUserLocale());
            df.applyPattern("{0,date,yyyy-MM-dd hh:mm:ss}");
            line.add(df.format(new Object[]{new Date()}));
            csv.writeNext(line.toArray(new String[line.size()]));

            writer.newLine();

            writer.write("powered by web4thejob.org");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    @Override
    public File print(String title, RenderScheme renderScheme, Entity entity) {
        Assert.notNull(entity);
        return print(title, renderScheme, null, entity);
    }

    @Override
    public File print(String title, RenderScheme renderScheme, List<Entity> entities) {
        Assert.notNull(entities);
        return print(title, renderScheme, null, entities);
    }

    @Override
    public File print(String title, List<RenderScheme> renderSchemes, Query query, List<Entity> entities) {
        Assert.notNull(renderSchemes);

        int i = 0;
        File file;
        try {
            String crlf = System.getProperty("line.separator");
            file = createTempFile();
            BufferedWriter writer = createFileStream(file);
            writer.write(title + crlf);
            writer.newLine();

            CSVWriter csv = new CSVWriter(writer);

            for (Entity entity : entities) {

                if (entity == null) {
                    Assert.notNull(query);
                    entity = ContextUtil.getDRS().findUniqueByQuery(query);
                }

                if (query != null && query.hasMasterCriterion()) {
                    writer.write(describeMasterCriteria(query));
                    writer.newLine();
                }

                if (query != null) {
                    writer.write(describeCriteria(query));
                    writer.newLine();
                }

                writeLine(csv, ContextUtil.getBean(ConversionService.class), entity, renderSchemes.get(i));
                writer.newLine();
                writer.newLine();
                i++;
            }

            //timestamp
            List<String> line = new ArrayList<String>();
            line.add(L10nMessages.L10N_LABEL_TIMESTAMP.toString());
            MessageFormat df = new MessageFormat("");
            df.setLocale(CoreUtil.getUserLocale());
            df.applyPattern("{0,date,yyyy-MM-dd hh:mm:ss}");
            line.add(df.format(new Object[]{new Date()}));
            csv.writeNext(line.toArray(new String[line.size()]));

            writer.newLine();

            writer.write("powered by web4thejob.org");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;

    }

    @Override
    public File print(String title, List<RenderScheme> renderSchemes, List<Entity> entities) {
        return print(title, renderSchemes, null, entities);
    }

// -------------------------- OTHER METHODS --------------------------

    private File createTempFile() throws IOException {
        File file = File.createTempFile("w4tj_", ".csv");
        file.deleteOnExit();
        return file;
    }

    private BufferedWriter createFileStream(File file) throws IOException {
        final FileOutputStream out = new FileOutputStream(file);

        String charset = CoreUtil.getParameterValue(Category.PRINTER_PARAM, Key.CHARSET, String.class, "UTF-8");
        if (charset.toUpperCase().equals("UTF-8")) {
            // UTF-8 BOM
            out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            return new BufferedWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
        } else {
            return new BufferedWriter(new OutputStreamWriter(out, Charset.forName(charset)));
        }
    }

    private String describeCriteria(Query query) {
        final StringBuilder buffer = new StringBuilder();

        if (query.getCriteria() != null) {
            for (final Criterion criterion : query.getCriteria()) {
                if (criterion.isMaster()) {
                    continue;
                }

                final Condition condition = criterion.getCondition();

                if (condition.getOperandsNo() == 0 || criterion.getValue() != null && criterion.getValue().toString()
                        .length() > 0) {
                    if (buffer.length() > 0) {
                        buffer.append(" /+/ ");
                    }

                    String line = criterion.getPropertyPath().getFriendlyName() + " " + condition.toString();
                    if (condition.getOperandsNo() > 0) {
                        line += " " + criterion.getValue().toString();
                    }

                    buffer.append(line);
                }
            }
        }

        if (buffer.length() > 0) {
            buffer.append(System.getProperty("line.separator"));
        }
        return buffer.toString();
    }

    private String describeMasterCriteria(Query query) {
        final StringBuilder buffer = new StringBuilder();

        if (query.getCriteria() != null) {
            for (final Criterion criterion : query.getCriteria()) {
                if (!criterion.isMaster()) {
                    continue;
                }

                if (criterion.getValue() != null && criterion.getValue().toString().length() > 0) {
                    if (buffer.length() > 0) {
                        buffer.append(" /+/ ");
                    }

                    buffer.append(criterion.getValue().toString());
                }
            }
        }

        if (buffer.length() > 0) {
            buffer.append(System.getProperty("line.separator"));
        }
        return buffer.toString();
    }

    public static String getActualTextFromHtml(String html) {
        String s = html.replaceAll("\\<.*?>", "");
        s = StringEscapeUtils.unescapeHtml(s);
        return s;
    }

}
