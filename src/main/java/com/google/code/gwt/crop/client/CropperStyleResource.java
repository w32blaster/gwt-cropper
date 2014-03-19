package com.google.code.gwt.crop.client;

import com.google.gwt.resources.client.CssResource;

/**
 * CssResource interface for <strong>GWT Cropper</strong> styles.
 * 
 * @author ilja.hamalainen@gmail.com (Ilja Hämäläinen)
 */
public interface CropperStyleResource extends CssResource {

	String base();
    String imageCanvas();
    String selection();
    String selectionDraggableBackground();
    String handlesContainer();
    String handle();
    
    /**
     * Declares the size of a handle. Assume, that it has the equal width and height
     * 
     * @return handle size in pixels, int
     */
    int handleSize();

    /**
     * The border width for the selection. Default value is 1px.
     *
     * @return
     */
    int borderSize();
}