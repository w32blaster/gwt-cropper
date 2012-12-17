package com.google.code.gwt.crop.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

/**
 * Implementation of the style bundle initializer for desktop
 * 
 * @author ilja
 *
 */
class StylesDesktopImpl implements ICropperStyleSource {

	interface IStyleDesktop extends ClientBundle {
		
		@Source("GWTCropper.css")
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
