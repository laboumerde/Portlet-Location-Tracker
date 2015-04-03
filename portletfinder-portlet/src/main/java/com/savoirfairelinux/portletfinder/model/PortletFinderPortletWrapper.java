/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savoirfairelinux.portletfinder.model;

import com.liferay.portal.model.Portlet;

/**
 * Simple wrapper to use JSTL in the portlet selector
 *
 * @author Julien Boumard <julien.boumard@savoirfairelinux.com>
 */
public class PortletFinderPortletWrapper {

	private final Portlet portlet;
	private String title;

	/**
	 * Creates the portlet wrapper
	 *
	 * @param portlet The portlet to wrap
	 * @param portletTitle The title of the portlet
	 */
	public PortletFinderPortletWrapper(Portlet portlet, String portletTitle) {
		this.portlet = portlet;
		this.title = portletTitle;
	}

	/**
	 * Returns the wrapped portlet
	 * @return The wrapped portlet
	 */
	public Portlet getPortlet() {
		return this.portlet;
	}

	/**
	 * Returns the portlet's title
	 * @return The portlet's title
	 */
	public String getTitle() {
		return this.title;

	}

}
