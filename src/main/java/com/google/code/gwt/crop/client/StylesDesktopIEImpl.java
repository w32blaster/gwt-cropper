package com.google.code.gwt.crop.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

/**
 * Implementation of CSS styles for Internet Explorer (versions 7 and 8)
 * 
 * @author ilja.hamalainen@gmail.com (Ilja Hämäläinen)
 *
 */
class StylesDesktopIEImpl implements ICropperStyleSource {

	interface IStyleDesktop extends ClientBundle {
		
		@Source("GWTCropperIE.css")
		CropperStyleResource getStyles();
	}

	private final IStyleDesktop bundleResources = GWT.create(IStyleDesktop.class);
	
	/**
	 * {@inheritDoc}
	 */
	public CropperStyleResource css() {
		return bundleResources.getStyles();
	}

}
