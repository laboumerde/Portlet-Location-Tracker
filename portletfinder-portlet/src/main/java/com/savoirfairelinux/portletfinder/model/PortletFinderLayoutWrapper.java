package com.savoirfairelinux.portletfinder.model;

import com.liferay.portal.model.Layout;

import java.util.List;


/**
 * Wraps the portlet finder's search results so they can be used without
 * scriptlets in JSP pages
 *
 * @author Nicolas Juneau <nicolas.juneau@savoirfairelinux.com>
 */
public class PortletFinderLayoutWrapper {

    private final Layout layout;
    private long layoutId;
    private String pageURL;
    private List<String> portletInstances;

    /**
     * Creates the layout wrapper
     *
     * @param layout The layout to wrap
     * @param pageURL The URL of the page on which we can find the portlet
     * @param portletInstances The full portlet IDs that are on the page
     */
    public PortletFinderLayoutWrapper(Layout layout, String pageURL, List<String> portletInstances) {
        this.layout = layout;
        this.layoutId = layout.getLayoutId();
        this.pageURL = pageURL;
        this.portletInstances = portletInstances;
    }

    /**
     * Returns the layout's ID
     * @return The layout's ID
     */
    public long getLayoutId() {
        return this.layoutId;
    }

    /**
     * Returns the wrapped layout
     * @return The wrapped layout
     */
    public Layout getLayout() {
        return this.layout;
    }

    /**
     * Returns the URL of the page on which we can find the portlet
     * @return The URL of the page on which we can find the portlet
     */
    public String getPageURL() {
        return this.pageURL;
    }

    /**
     * Returns the full portlet IDs that are on the page
     * @return The full portlet IDs that are on the page
     */
    public List<String> getPortletInstances() {
        return this.portletInstances;
    }

}
