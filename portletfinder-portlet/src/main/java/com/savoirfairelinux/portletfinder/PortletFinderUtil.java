package com.savoirfairelinux.portletfinder;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutConstants;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.comparator.PortletTitleComparator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Porlet Finder utils.
 * This portlet is inspired by the work of Jitendra Rajput (http://itsliferay.blogspot.com)
 *
 * @author Julien Boumard <julien.boumard@savoirfairelinux.com>
 *
 */
public class PortletFinderUtil
{
    /**
     * Get All portlets for Current Company
     * @param companyId
     * @param locale
     * @return All portlets based on the locale translation
     */
    public static List<Portlet> getPortletList(long companyId , Locale locale)
    {
        List<Portlet> portletList = null;
        try
        {
            portletList = PortletLocalServiceUtil.getPortlets(companyId, false, false);
            portletList = ListUtil.sort(portletList, new PortletTitleComparator(locale));
        } catch (SystemException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        return portletList;
    }

	public static List<PortletFinderPortletWrapper> convertToWrapper(List<Portlet> portletList, Locale locale){
		List<PortletFinderPortletWrapper> portletWrapperList = new ArrayList<PortletFinderPortletWrapper>();
		for (Portlet portlet : portletList) {
			PortletFinderPortletWrapper portletWrapper= new PortletFinderPortletWrapper(portlet);
			portletWrapper.setLocale(locale);
			portletWrapperList.add(portletWrapper);
		}
		return portletWrapperList;
	}

    /**
     * Get Portlet based on portlet id
     * @param portletId
                                 * @return a Portlet instance based on the ID.
     */
    public static Portlet getPortlet(String portletId)
    {
        return PortletLocalServiceUtil.getPortletById(portletId);
    }

    /**
     * Get Portlet locations
     * @param groupList
     * @param privateLayout
     * @param portletId
     * @return Returns the layouts containing the portlets
     */
    public static List<Layout> doGetPortletLocation(List<Group> groupList, boolean privateLayout, String portletId){
        List<Layout> portletDetailsList = new ArrayList<Layout>();
        if (Validator.isNotNull(groupList) && !groupList.isEmpty()){
            for (Group group : groupList){
                long scopeGroupId = group.getGroupId();
                long groupId = getGroupId(group);
                Set<Layout> groupLayoutSet = new HashSet<Layout>();
                try{
                    List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, privateLayout, LayoutConstants.TYPE_PORTLET);
                    for (Layout layout : layouts){
                        LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet) layout.getLayoutType();
                        List<Portlet> layoutPortlets =  layoutTypePortlet.getAllPortlets();
                        for (Portlet portlet : layoutPortlets){
                            if(portletId.equals(getOriginalPortletId(portlet.getPortletId()))){
                                if (PortalUtil.getScopeGroupId(layout, portletId) == scopeGroupId){
                                    groupLayoutSet.add(layout);
                                }
                            }
                        }
                    }
                    portletDetailsList.addAll(groupLayoutSet);
                } catch (PortalException e){
                    LOGGER.error(e.getMessage());
                } catch (SystemException e){
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return portletDetailsList;
	}

    /**
     * Get All Groups for Current Company
     * @param companyId
     * @return a list of the groups for current company
     */
    public static List<Group> getCompanyGroups(long companyId){
        List<Group> groupList = new ArrayList<Group>();
        try{
            groupList = GroupLocalServiceUtil.getCompanyGroups(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
        } catch (SystemException e){
            LOGGER.error(e.getMessage(), e);
        }
        return groupList;
    }

    /**
     * Get Group Id
     * @param group
     * @return the group ID of the group
     */
    public static long getGroupId(Group group)
    {
        long groupId = group.getGroupId();
        try{
            if (group.isLayout()){
                Layout scopeLayout = LayoutLocalServiceUtil.getLayout(group.getClassPK());
                groupId = scopeLayout.getGroupId();
            }
        } catch (PortalException e){
                    LOGGER.error(e.getMessage());
        } catch (SystemException e){
                    LOGGER.error(e.getMessage());
        }
        return groupId;
    }

    /**
     * Get Portlet Title
     * @param portlet
     * @param locale
     * @param request
     * @return the portlet Title
     */
    public static String getPortletTitle(Portlet portlet , Locale locale , HttpServletRequest request){
        String portletTile = PortalUtil.getPortletTitle(portlet,request.getSession().getServletContext(),locale);
        return portletTile;
    }

    /**
     * Get locations for selected portlet
     * @param portletRequest
     * @return The layouts containing the portlets
     */
    public static List<Layout> findPortlet(PortletRequest portletRequest)
    {
        ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
        List<Layout> layoutList = new ArrayList<Layout>();
        String portletSelect = ParamUtil.getString(portletRequest, "portletSelect");
        LOGGER.debug("portlet id--" + portletSelect);
        if(Validator.isNotNull(portletSelect)){
            Portlet portlet = getPortlet(portletSelect);
            List<Group> groupList = getCompanyGroups(themeDisplay.getCompanyId());
            List<Layout> publiclayoutList = doGetPortletLocation(groupList, false, portlet.getPortletId());
            List<Layout> privateLayoutList = doGetPortletLocation(groupList, true, portlet.getPortletId());
            if(Validator.isNotNull(publiclayoutList) && !publiclayoutList.isEmpty()){
                layoutList.addAll(publiclayoutList);
            }
            if(Validator.isNotNull(privateLayoutList) && !privateLayoutList.isEmpty()){
                layoutList.addAll(privateLayoutList);
            }
            SessionMessages.add(portletRequest, "your-request-completed-successfully");

        }
        return layoutList;
    }

    /**
     * Get Search Container Object
     * @param renderRequest
     * @param renderResponse
     * @return The search Container with the layouts containing the portlets
     */
    public static SearchContainer<Layout> getSearchContainer(RenderRequest renderRequest ,RenderResponse renderResponse){
        SearchContainer<Layout> searchContainer;
        List<Layout> layoutList = findPortlet(renderRequest);
        PortletURL portletURL = renderResponse.createRenderURL();
        String portletId = ParamUtil.getString(renderRequest, "portletSelect");
        portletURL.setParameter("portletSelect", portletId);

        searchContainer = new SearchContainer<Layout>(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, null, "no-locations-were-found");

        if (Validator.isNotNull(layoutList)) {
            List<Layout> results = ListUtil.subList(layoutList, searchContainer.getStart(), searchContainer.getEnd());
            searchContainer.setResults(results);
            searchContainer.setTotal(layoutList.size());
        }
        return searchContainer;
    }

    /**
     * Get Page URL where portlet is placed
     * @param isPrivateLayout
     * @param friendlyURL
     * @param groupFriendlyURL
     * @param themeDisplay
     * @return the page Url
     */
    public static String getPageURL(boolean isPrivateLayout, String friendlyURL,String groupFriendlyURL , ThemeDisplay themeDisplay){
        StringBuilder sb = new StringBuilder();
        sb.append(themeDisplay.getPortalURL());
        if (isPrivateLayout){
            sb.append(PortalUtil.getPathFriendlyURLPrivateGroup());
        }else{
            sb.append(PortalUtil.getPathFriendlyURLPublic());
        }
        sb.append(groupFriendlyURL);
        sb.append(friendlyURL);
        return sb.toString();
    }

    /**
     * Get the group descriptive name associated to the layout
     * @param layout
     * @return the groupe descriptive name
     */
    public static String getGroupDescriptiveName(Layout layout) {
        String groupName = StringPool.BLANK;
        if (Validator.isNotNull(layout)) {
            try {
                groupName = layout.getGroup().getDescriptiveName();
            } catch (PortalException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (SystemException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return groupName;
    }

    /**
     * Retrieves the original portlet ID
     * @param portletId
     * @return the orgininal portlet ID, without its instance
     */
    public static String getOriginalPortletId(String portletId){
        String res = portletId;
        if(portletId.contains(INSTANCE)){
            res = StringUtil.extractFirst(portletId, INSTANCE);
        }
        return res;
    }

	/**
	 * @param layout page layout
	 * @param portletId portlet Id
	 * @return the portlet full Ids in the page
	 */
	public static String getPortletInstances(Layout layout, String portletId){
		StringBuilder res = new StringBuilder();

		if (layout != null && layout.getLayoutType() instanceof LayoutTypePortlet){
			LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet) layout.getLayoutType();
			try {
				List<Portlet> portlets = layoutTypePortlet.getPortlets();
				for (Portlet portlet : portlets) {
					if(portlet.getInstanceId() != null && getOriginalPortletId(portlet.getPortletId()).equals(portletId)){
						if(!(res.length() == 0)) {
							res.append(StringPool.COMMA_AND_SPACE);
						}
						res.append(portlet.getInstanceId());
					}
				}
			} catch (SystemException ex) {
				LOGGER.error(ex);
			}
		}

		return res.toString();
	}

    /**
     * Instance String in the portlet name
     */
    public final static String INSTANCE = "_INSTANCE_";

    /**
     * Logger
     */
    private static final Log LOGGER = LogFactoryUtil.getLog(PortletFinderUtil.class);
}
