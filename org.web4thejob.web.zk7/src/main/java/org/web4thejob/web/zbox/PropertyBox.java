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

package org.web4thejob.web.zbox;

import org.springframework.util.StringUtils;
import org.web4thejob.command.ArbitraryDropdownItems;
import org.web4thejob.command.CommandAware;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.command.DefaultArbitraryDropdownCommandDecorator;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.MessageAware;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.*;
import org.web4thejob.orm.annotation.*;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.print.CsvPrinter;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.L10nUtil;
import org.web4thejob.web.panel.MutableEntityViewPanel;
import org.web4thejob.web.panel.Panel;
import org.web4thejob.web.util.MediaUtil;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class PropertyBox extends HtmlMacroComponent {
    private static final long serialVersionUID = 1L;
    private static final String ON_CLICK_ECHO = Events.ON_CLICK + "Echo";
    public static final int TOOLTIP_LIMIT = -1;
    private final Html html = new Html();
    private RenderElement renderElement;
    private boolean emailHolder;
    private boolean colorHolder;
    private boolean urlHolder;
    private boolean mediaHolder;
    private boolean imageHolder;
    private boolean entityTypeHolder;
    private boolean panelHolder;
    private boolean queryHolder;
    private MessageFormat formatter;
    private Entity entity;
    private A navigateLink;
    private A downloadLink;
    private Image tooltipLink;
    private Popup tooltipPopup;
    private boolean disableNavigateLink;
    private Image image;
    @Wire
    private Hbox hbox;

    public int getTooltipLimit() {
        return tooltipLimit;
    }

    public void setTooltipLimit(int tooltipLimit) {
        this.tooltipLimit = tooltipLimit;
    }

    private int tooltipLimit = TOOLTIP_LIMIT;

    public PropertyBox() {
        this((RenderElement) null);
    }

    public PropertyBox(PathMetadata pathMetadata) {
        this(ContextUtil.getEntityFactory().buildRenderElement(pathMetadata));
    }


    public PropertyBox(RenderElement renderElement) {
        compose();
        setRenderElement(renderElement);
    }


    public void setRenderElement(RenderElement renderElement){
        if (renderElement != null) {
            PropertyMetadata pm = renderElement.getPropertyPath().getLastStep();
            this.renderElement = renderElement;
            this.emailHolder = pm.isAnnotatedWith(EmailHolder.class);
            this.urlHolder = pm.isAnnotatedWith(UrlHolder.class);
            this.colorHolder = pm.isAnnotatedWith(ColorHolder.class);
            this.mediaHolder = pm.isAnnotatedWith(MediaHolder.class);
            this.imageHolder = pm.isAnnotatedWith(ImageHolder.class);
            this.entityTypeHolder = pm.isAnnotatedWith(EntityTypeHolder.class);
            this.panelHolder = pm.isAnnotatedWith(PanelHolder.class);
            this.queryHolder = pm.isAnnotatedWith(QueryHolder.class);
        } else {
            this.renderElement = null;
            this.emailHolder = false;
            this.urlHolder = false;
            this.colorHolder = false;
            this.mediaHolder = false;
            this.imageHolder = false;
            this.entityTypeHolder = false;
            this.panelHolder = false;
            this.queryHolder = false;
        }

        initStyle();
        initFormat();
    }

    @Override
    protected void compose() {
        setMacroURI("/WEB-INF/zbox.zul");
        setWidth("100%");
        super.compose();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setValue(Object value) {
        html.setContent(prepareContent(value));
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        if (this.entity != null) {
            html.setContent(prepareContent(renderElement.getPropertyPath().getValue(entity)));
        } else {
            html.setContent("");
            if (navigateLink != null) {
                navigateLink.setVisible(false);
            }
            if (downloadLink != null) {
                downloadLink.setVisible(false);
            }
        }
    }

    public String getContent() {
        return html.getContent();
    }

    @SuppressWarnings("unchecked")
    private String prepareContent(Object value) {
        String content;
        setTooltiptext("");

        if (navigateLink != null) {
            navigateLink.setVisible(false);
        }
        if (downloadLink != null) {
            downloadLink.setVisible(false);
        }

        if (value == null) {
            return "";
        } else if (value.getClass().isEnum()) {
            content = L10nUtil.getMessage(L10nString.class, value.getClass().getSimpleName() + "." + value.toString()
                    , value.toString());
        } else if (value instanceof Boolean) {
            if (((Boolean) value)) {
                content = ContextUtil.resourceExists("img/OK.png") ? "<img src=\"img/OK.png\"/>" : "Ok";
            } else {
                content = ContextUtil.resourceExists("img/NOT_OK.png") ? "<img src=\"img/NOT_OK.png\"/>" : "Not Ok";
            }
        } else if (value instanceof Entity) {
            if (navigateLink != null) {
                navigateLink.setVisible(!disableNavigateLink);
            }
            content = ContextUtil.getMRS().deproxyEntity((Entity) value).toRichString();
        } else if (colorHolder) {
            content = "                  ";
            String style = getStyle();
            style = ZkUtil.replaceStyleElement(style, "background-color", (String) value);
            style = ZkUtil.replaceStyleElement(style, "white-space", "pre-wrap");
            setStyle(style);
        } else if (mediaHolder || imageHolder) {

            if (imageHolder && MediaUtil.isImage(MediaUtil.getMediaFormat((byte[]) value))) {
                if (image != null) {
                    image.detach();
                }
                content = "";
                image = new Image();
                image.setParent(this);
                image.setTooltiptext(MediaUtil.getMediaDescription((byte[]) value));
                image.setContent(MediaUtil.createThumbnail((byte[]) value));
                image.setAttribute("value", value);
                image.addEventListener(Events.ON_CLICK, new DownloadLinkListener());
            } else {
                if (downloadLink != null) {
                    downloadLink.setVisible(true);
                    downloadLink.setAttribute("value", value);
                }
                content = MediaUtil.getMediaDescription((byte[]) value);
            }
        } else if (entityTypeHolder) {
            if (value instanceof String) {
                content = ContextUtil.getMRS().getEntityMetadata((String) value).getFullFriendlyName();
            } else {
                content = ContextUtil.getMRS().getEntityMetadata((Class<? extends Entity>) value).getFullFriendlyName();
            }
        } else if (panelHolder) {
            PanelDefinition panelDefinition = ORMUtil.getPanelDefinition((String) value);
            if (panelDefinition != null) {
                content = panelDefinition.getName();
                ZkUtil.setInactive(html, false);
            } else {
                content = (String) value;
                ZkUtil.setInactive(html, true);
            }
        } else if (queryHolder) {
            Query query = ContextUtil.getDRS().findById(Query.class, Long.valueOf(value.toString()));
            if (query != null) {
                content = query.getName();
                ZkUtil.setInactive(html, false);
            } else {
                content = value.toString();
                ZkUtil.setInactive(html, true);
            }
        } else {
            content = applyFormat(value);
        }


        if (emailHolder) {
            content = "<a href=\"mailto:" + content + "\">" + content + "</a>";
        } else if (urlHolder) {
            content = "<a href=\"" + content + "\" target=\"_blank\">" + content + "</a>";
        } else if (isTooltipRequired(content)) {
            setTooltiptext(content);
        }

        return content;

    }

    private boolean isTooltipRequired(String content) {
        return content != null && getTooltipLimit() > 0 &&
                (StringUtils.countOccurrencesOf(content, "<p>") > 1 ||
                        StringUtils.countOccurrencesOf(content, "<ol>") > 0 ||
                        StringUtils.countOccurrencesOf(content, "<image>") > 0 ||
                        CsvPrinter.getActualTextFromHtml(content).length() > getTooltipLimit());
    }

    @Override
    public void setTooltiptext(String tooltiptext) {
        if (StringUtils.hasText(tooltiptext)) {
            if (tooltipPopup == null) {
                tooltipPopup = new Popup();
                tooltipPopup.setParent(this);

                tooltipLink = new Image("img/COMMENT.png");
                insertBefore(tooltipLink, html);

                Html tooltipHtml = new Html();
                tooltipHtml.setZclass("z-label");
                tooltipHtml.setParent(tooltipPopup);
                tooltipHtml.setHflex("true");
                tooltipHtml.setVflex("true");
            }
            ((Html) tooltipPopup.getFirstChild()).setContent(tooltiptext);
            tooltipLink.setTooltip(tooltipPopup);
        } else {
            if (tooltipPopup != null) {
                tooltipPopup.detach();
                tooltipPopup = null;

                tooltipLink.detach();
                tooltipLink = null;
            }
        }
    }

    public void initStyle() {
        if (!colorHolder) {
            this.html.setParent(this);
        } else {
            Div div = new Div();
            div.setParent(this);
            this.html.setParent(div);

            div.setStyle("border-width: 1px; border-color: black; border-style: solid;");
        }

        hbox.setSpacing("3px");
        html.setZclass("z-label");
        html.setStyle("white-space:nowrap;");
        if (renderElement != null) {
            if (renderElement.getStyle() != null) {
                html.setStyle(html.getStyle() + renderElement.getStyle());
            }
            if (renderElement.getPropertyPath().getLastStep().isAssociationType()) {
                buildNavigationLink();
            } else if (renderElement.getPropertyPath().getLastStep().isBlobType()) {
                buildDownloadLink();
            }

            hbox.setPack(renderElement.getAlign());
            setWidth(renderElement.getWidth());
        }
    }

    private void buildDownloadLink() {
        downloadLink = new A();
        this.insertBefore(downloadLink, html);
        downloadLink.setVisible(false);
        downloadLink.setImage("img/DOWNLINK.png");
        //downloadLink.setTooltiptext(L10nMessages.L10N_TOOLTIP_NAVIGATE.toString());
        DownloadLinkListener listener = new DownloadLinkListener();
        downloadLink.addEventListener(Events.ON_CLICK, listener);
        //downloadLink.addEventListener(ON_CLICK_ECHO, listener);
    }

    private void buildNavigationLink() {

        if (!ContextUtil.getSessionContext().getSecurityContext().isAdministrator()) {
            String beanid = CoreUtil.getDefaultEntityViewName(renderElement.getPropertyPath()
                    .getLastStep().getAssociatedEntityMetadata().getEntityType());
            if (beanid == null && !ContextUtil.getSessionContext().hasPanel(beanid, Panel.class)) {
                return;
            }
        }

        navigateLink = new A();
        this.insertBefore(navigateLink, html);
        navigateLink.setVisible(false);
        navigateLink.setImage("img/LINK.png");
        navigateLink.setTooltiptext(L10nMessages.L10N_TOOLTIP_NAVIGATE.toString());
        NavigateLinkListener listener = new NavigateLinkListener();
        navigateLink.addEventListener(Events.ON_CLICK, listener);
        navigateLink.addEventListener(ON_CLICK_ECHO, listener);
    }

    private String applyFormat(Object value) {
        if (formatter != null) {
            return formatter.format(new Object[]{value});
        }
        return value.toString();
    }


    @Override
    public void setStyle(String style) {
        html.setStyle(style);
    }

    @Override
    public String getStyle() {
        return html.getStyle();
    }

    public void initFormat() {
        try {
            if (renderElement != null && renderElement.getFormat() != null) {
                formatter = new MessageFormat("");
                formatter.setLocale(CoreUtil.getUserLocale());
                formatter.applyPattern("{0," + renderElement.getFormat() + "}");
            } else {
                formatter = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            formatter = null;
        }
    }

    private class DownloadLinkListener implements EventListener<Event> {

        @Override
        public void onEvent(Event event) throws Exception {
            if (Events.ON_CLICK.equals(event.getName())) {
                if (byte[].class.isInstance(event.getTarget().getAttribute("value"))) {
                    byte[] value = (byte[]) event.getTarget().getAttribute("value");
                    String mediaFormat = MediaUtil.getMediaFormat(value);
                    Filedownload.save(new AMedia(MediaUtil.buildName(PropertyBox.this, mediaFormat),
                            mediaFormat.toLowerCase(), null,
                            MediaUtil.getMediaBytes(value)));
                }
            }
        }
    }

    private class NavigateLinkListener implements EventListener<Event>, ArbitraryDropdownItems {

        @Override
        public void onEvent(Event event) throws Exception {
            if (Events.ON_CLICK.equals(event.getName())) {
                if (((MouseEvent) event).getKeys() != (MouseEvent.LEFT_CLICK + MouseEvent.SHIFT_KEY)) {
                    Clients.showBusy(null);
                    Events.echoEvent(ON_CLICK_ECHO, event.getTarget(), null);
                } else {
                    Panel panel = ZkUtil.getOwningPanelOfComponent(PropertyBox.this);
                    if (panel instanceof CommandAware && ((CommandAware) panel).hasCommand
                            (CommandEnum.RELATED_PANELS)) {
                        Menupopup menupopup = new Menupopup();
                        DefaultArbitraryDropdownCommandDecorator.renderSubCommands(this, menupopup);
                        if (!menupopup.getChildren().isEmpty()) {
                            menupopup.setParent(navigateLink);
                            menupopup.open(navigateLink);
                        }
                    }
                }
            } else if (ON_CLICK_ECHO.equals(event.getName())) {
                Clients.clearBusy();
                onItemClicked(null);
            }
        }

        @Override
        public Map<String, String> getDropdownItems() {
            final Entity bindValue = (Entity) renderElement.getPropertyPath().getValue(entity);
            if (bindValue == null) return Collections.emptyMap();
            return CoreUtil.getRelatedPanelsMap(bindValue.getEntityType(), MutableEntityViewPanel.class);
        }

        @Override
        public void onItemClicked(String key) {
            Panel panel = ZkUtil.getOwningPanelOfComponent(PropertyBox.this);
            if (panel instanceof MessageAware && entity != null) {
                Entity bindValue = (Entity) renderElement.getPropertyPath().getValue(entity);
                if (bindValue != null) {
                    Panel entityPanel = CoreUtil.getEntityViewPanel(bindValue, key);
                    if (entityPanel != null) {
                        ((MessageAware) panel).dispatchMessage(ContextUtil.getMessage(MessageEnum.ADOPT_ME,
                                entityPanel));
                    }
                }
            }
        }

    }

    public void disableNavigateLink(boolean disable) {
        this.disableNavigateLink = disable;
        if (navigateLink != null && disable) {
            navigateLink.detach();
            navigateLink = null;
        } else if (navigateLink == null && !disable && renderElement != null && renderElement.getPropertyPath()
                .getLastStep().isAssociationType()) {
            buildNavigationLink();
        }
    }

    private boolean isNavigationAllowed() {
        Panel panel = ZkUtil.getOwningPanelOfComponent(this);
        return panel instanceof CommandAware && ((CommandAware) panel).hasCommand(CommandEnum.RELATED_PANELS);
    }

    @Override
    public void setParent(Component parent) {
        super.setParent(parent);
        disableNavigateLink(ZkUtil.isDialogContained(this) || !isNavigationAllowed());
    }


}
