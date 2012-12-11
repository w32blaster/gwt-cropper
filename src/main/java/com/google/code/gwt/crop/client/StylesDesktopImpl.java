package com.google.code.gwt.crop.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

/**
 * Implementation of the style bundle initializer for desktops
 * 
 * @author ilja
 *
 */
class StylesDesktopImpl implements IStyleSource {

	interface IStyleDesktop extends ClientBundle {
		
		@Source("GWTCropper.css")
		GWTCropperStyle getStyles();
	}

	private final IStyleDesktop bundleResources = GWT.create(IStyleDesktop.class);
	
	/**
	 * {@inheritDoc}
	 */
	public GWTCropperStyle css() {
		return bundleResources.getStyles();
	}

}
