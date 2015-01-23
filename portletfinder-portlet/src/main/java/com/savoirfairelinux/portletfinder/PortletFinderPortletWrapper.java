/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savoirfairelinux.portletfinder;

import com.liferay.portal.model.Portlet;
import com.liferay.portal.util.PortalUtil;
import java.util.Locale;

/**
 *
 * @author etudiant
 */
public class PortletFinderPortletWrapper {

	private Portlet portlet;
	private Locale locale;

	public PortletFinderPortletWrapper(Portlet portlet) {
		this.portlet = portlet;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Portlet getPortlet() {
		return this.portlet;
	}

	public String getSelectLabel() {
		String portletTitle = PortalUtil.getPortletTitle(portlet, locale);
		return (portletTitle + " (" + portlet.getPortletId() + ")");

	}

}
