package com.google.code.gwt.crop.client;

import com.google.gwt.resources.client.CssResource;

/**
 * CssResource interface for styles
 * 
 * @author ilja
 *
 */
interface GWTCropperStyle extends CssResource {

	String base();
    String imageCanvas();
    String selection();
    String handlesContainer();
    String handle();
    
    /**
     * Declares the center of a handle.
     * Let's say, if handle is 30x30 px, then center will be 15px
     * 
     * @return
     */
    int handleCenter();
}
