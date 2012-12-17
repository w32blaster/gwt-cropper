package com.google.code.gwt.crop.client;

/**
 * Interface is used to give changing styles for different device types.
 * It can be modified through deferring binding
 *  
 * @author ilja
 *
 */
interface ICropperStyleSource {
	
	/**
	 * Initiates proper implementation of the current style bundle.
	 * Add the 
	 * <code>@Source("yourStyleFileName.css")</code>
	 * annotation to this method to provide your CSS style
	 * file path.
	 * 
	 * @return GWTCropperStyle instance of the bundle style
	 */
	CropperStyleResource css();
}
