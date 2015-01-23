<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.savoirfairelinux.portletfinder.PortletFinderUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="java.util.Locale"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@page import="com.liferay.portal.model.Portlet"%>
<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<portlet:defineObjects />
<portlet:renderURL var="portletFinderUrl">
</portlet:renderURL>

<%
ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
Locale locale = (Locale) themeDisplay.getLocale();
%>

<aui:form action="<%=portletFinderUrl%>" method="post" name="fm">
    <aui:layout>
        <aui:column first="true">
            <aui:select name="portletSelect" label="portlet-select" useNamespace="true" inlineLabel="left">
                <aui:option><liferay-ui:message key="select-a-portlet" /></aui:option>
                <c:if test="${fn:length(portletList) > 0}">
                    <c:forEach items="${portletList}" var="portletW">
                        <option <c:if test="${portletSelect == portletW.portlet.portletId}">selected="selected"</c:if>  value="${portletW.portlet.portletId}">
                            ${portletW.selectLabel}
                        </option>
                    </c:forEach>
                </c:if>
            </aui:select>
        </aui:column>
        <aui:column columnWidth="20" first="true">
            <aui:button value="search" name="search"/>
        </aui:column>
    </aui:layout>
</aui:form>

<aui:script>
    AUI().use('aui-base', function(A){
        A.one("#<portlet:namespace />search").on('click',function(){
            submitForm(document.<portlet:namespace />fm);
        })
    });
</aui:script>

<liferay-ui:search-container hover="false"  searchContainer="${searchContainer}">
	<liferay-ui:search-container-results 
		results="${searchContainer.results}"
		total="${searchContainer.total}" />
	<liferay-ui:search-container-row 
		className="com.liferay.portal.model.Layout"
		keyProperty="layoutId" modelVar="layoutObj">
		
		<liferay-ui:search-container-column-text name="page-name" property="name"/>
		<liferay-ui:search-container-column-text name="group" value="<%= layoutObj.getGroup().getDescriptiveName() %>">
			
		</liferay-ui:search-container-column-text>
		<liferay-ui:search-container-column-text name="friendly-url" property="friendlyURL"/>
		<liferay-ui:search-container-column-text name="is-private-page" value='<%=(layoutObj.isPrivateLayout() ? LanguageUtil.get(pageContext, "yes") : LanguageUtil.get(pageContext, "no")) %>' property="privateLayout"/>
		<liferay-ui:search-container-column-text name="page-url" buffer="bufferSelection">
		<%
                    bufferSelection.append("<a target='_blank' href='");
                    bufferSelection.append(PortletFinderUtil.getPageURL(layoutObj.isPrivateLayout(), layoutObj.getFriendlyURL(), layoutObj.getGroup().getFriendlyURL(), themeDisplay));
                    bufferSelection.append("'>");
                    bufferSelection.append(LanguageUtil.get(pageContext, "go-to-page"));
                    bufferSelection.append("</a>");
		%>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>
	<liferay-ui:search-iterator/>
</liferay-ui:search-container>