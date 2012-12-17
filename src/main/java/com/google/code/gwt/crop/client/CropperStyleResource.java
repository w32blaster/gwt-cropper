package com.google.code.gwt.crop.client;

import com.google.gwt.resources.client.CssResource;

/**
 * CssResource interface for <strong>GWT Cropper</strong> styles
 * 
 * @author ilja
 *
 */
interface CropperStyleResource extends CssResource {

	String base();
    String imageCanvas();
    String selection();
    String handlesContainer();
    String handle();
    
    /**
     * Declares the size of a handle. Assume, that it has the equal width and height
     * 
     * @return handle size in pixels
     */
    int handleSize();
}
