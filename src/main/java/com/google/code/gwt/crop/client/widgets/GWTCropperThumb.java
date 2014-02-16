package com.google.code.gwt.crop.client.widgets;

/**
 * @author Dawid Dziewulski
 * @since 30-11-2013
 * @version $Name$ $Revision$ $Date$
 */
public interface GWTCropperThumb {
    
    void onAction(int cropShapeWidth, int cropShapeHeight, int cropCanvarWidth, int cropCanvarHeight, int cropLeft, int cropTop);
        
    void initImage(String url);
}
