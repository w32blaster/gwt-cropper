package com.google.code.gwt.crop.client.widgets.impl;

import com.google.code.gwt.crop.client.widgets.GWTCropperThumb;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Dawid Dziewulski
 * @since 30-11-2013
 * @version $Name$ $Revision$ $Date$
 */
public class GWTCropperThumbImpl extends SimplePanel implements GWTCropperThumb {
    
    /**Because crop image can be scaled. We have to remember its width and height
     * to future recounting */
    private int imageW, imageH;
    
    private Image embededImage;
    
    private double thumbSize;
    
    private LoadHandler loadHandler = new LoadHandler() {

        @Override
        public void onLoad(LoadEvent event) {
            imageH = embededImage.getHeight();
            imageW = embededImage.getWidth();
        }
    };

    public GWTCropperThumbImpl(int thumbSize){
        this.thumbSize = thumbSize; 
        setStyleProperties();
    }
  
    public GWTCropperThumbImpl(){
        this.thumbSize = 100;
        setStyleProperties();
    }
    
    @Override
    public void initImage(String url) {
        embededImage = new Image(url);
        setImageStyleProperty();
        embededImage.addLoadHandler(loadHandler);
        add(embededImage);
    }

    @Override
    public void onAction(int cropShapeWidth, int cropShapeHeight, int cropCanvarWidth, int cropCanvarHeight, int cropLeft, int cropTop) {
        double left = (-1)*Math.round((thumbSize / cropShapeWidth) * cropLeft);
        double top = (-1)*Math.round((thumbSize / cropShapeHeight) * cropTop);
        embededImage.getElement().getStyle().setMarginLeft(left, Unit.PX);
        embededImage.getElement().getStyle().setMarginTop(top, Unit.PX);
        embededImage.setSize(Math.round((cropCanvarWidth * thumbSize) / cropShapeWidth ) + "px", Math.round((cropCanvarHeight * thumbSize) / cropShapeHeight )+ "px");
    }
    
    private void setStyleProperties(){
        getElement().getStyle().setOverflow(Overflow.HIDDEN);
        getElement().getStyle().setWidth(100, Unit.PX);
        getElement().getStyle().setHeight(100, Unit.PX);
    }
  
    private void setImageStyleProperty(){
        embededImage.getElement().getStyle().setProperty("maxWidth","none");
    }
  
    public int getRealImageHeight(){
        return imageH;
    }
  
    public int getRealImageWidth(){
        return imageW;
    }
    
}
