package com.google.code.gwt.crop.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

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
