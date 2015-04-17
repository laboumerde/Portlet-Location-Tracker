package com.savoirfairelinux.portletfinder;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.savoirfairelinux.portletfinder.model.PortletFinderLayoutWrapper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Porlet Finder controller. This portlet is inspired by the work of Jitendra
 * Rajput (http://itsliferay.blogspot.com)
 *
 * @author Julien Boumard <julien.boumard@savoirfairelinux.com>
 *
 */
public class PortletController extends MVCPortlet {

    private static final String PARAM_SC_CUR = "cur";
    private static final String PARAM_SC_DELTA = "delta";
    private static final String PARAM_SELECTED_PORTLET = "portletSelect";

    private static final String RQ_ATTR_SELECTED_PORTLET = PARAM_SELECTED_PORTLET;
    private static final String RQ_ATTR_PORTLET_LIST = "portletList";
    private static final String RQ_ATTR_SEARCH_RESULTS = "searchResults";
    private static final String RQ_ATTR_SEARCH_RESULTS_SIZE = "searchResultsSize";

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        // Get locale and company info
        Locale locale = PortalUtil.getLocale(request);
        long companyId = PortalUtil.getCompanyId(request);
        String portalURL = ((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY)).getPortalURL();

        // Get a list of all the available portlets in the given company
        request.setAttribute(
            RQ_ATTR_PORTLET_LIST,
            PortletFinderUtil.convertToWrapper(PortletFinderUtil.getPortletList(companyId, locale), locale)
        );

        // Get the ID of the portlet on which we perform a search
        String portletId = ParamUtil.getString(request, PARAM_SELECTED_PORTLET);
        request.setAttribute(RQ_ATTR_SELECTED_PORTLET, portletId);

        try {
            // Find the portlet instances
            List<PortletFinderLayoutWrapper> results = PortletFinderUtil.findPortletInstances(companyId, portletId, portalURL);
            request.setAttribute(RQ_ATTR_SEARCH_RESULTS_SIZE, results.size());

            // Paginate
            int[] searchScope = this.calculateSearchScope(request);
            results = ListUtil.subList(results, searchScope[0], searchScope[1]);

            request.setAttribute(RQ_ATTR_SEARCH_RESULTS, results);
        } catch (Exception e) {
            throw new PortletException(e);
        }

        super.doView(request, response);
    }

    /**
     * Calculates the beginning and ending index of requests that make use of
     * Liferay's built-in search container
     *
     * @param request The portlet request
     * @return An array with the start index at the first position and the end
     *         index at the second position
     */
    private int[] calculateSearchScope(PortletRequest request) {
        int[] scope = new int[2];

        int currentPage = ParamUtil.get(request, PARAM_SC_CUR, 1);
        int delta = ParamUtil.get(request, PARAM_SC_DELTA, 5);

        scope[0] = ((currentPage - 1) * delta);
        scope[1] = scope[0] + delta;

        return scope;
    }

}