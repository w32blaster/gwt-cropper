package com.google.code.gwt.crop.client;

import com.google.code.gwt.crop.client.common.Dimension;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * <p><b>Thumbnail</b> - special widget panel that
 * shows the selected area in separate panel (i.e. "preview" area).</p>
 * 
 * @author Dawid Dziewulski
 * @author Ilja Hämäläinen
 * 
 * @since 0.5.0
 * @version %I%, %G%
 */
public class GWTCropperThumbImpl extends SimplePanel {

    /**Because crop image can be scaled. We have to remember its width and height
     * to future recounting */
    private int imageW, imageH;
    
    private int cropCanvasWidth; 
    private int cropCanvasHeight;

    // this widget dimensions
    private int width;
    private int height;

    private Image embededImage;

    private final Dimension fixedSide;
    private final int fixedValue;
    private double proportion;

    /**
     * TODO: add description here
     *
     * @param dimension - size, that will be remain constantly (width or height)
     * @param value - value of that side in px
     */
    public GWTCropperThumbImpl(Dimension dimension, int value){
        this.fixedSide = dimension;
        this.fixedValue = value;

        switch (this.fixedSide) {
            case WIDTH:
                this.width = value;
                getElement().getStyle().setWidth(value, Unit.PX);
                break;

            case HEIGHT:
                this.height = value;
                getElement().getStyle().setHeight(value, Unit.PX);
                break;
        }
    }
    
    // API available for inner usage of the GWTCropper
    
    /**
     * For internal usage. GWTCropper initializes this widget, after 
     * image will be loaded and all the dimensions will be known
     * 
     * @param imageUrl - image URL for preview
     * @param canvasWidth
     * @param canvasHeight
     */
    void init(String imageUrl, int canvasWidth, int canvasHeight) {
        this.embededImage = new Image(imageUrl);
        
        this.cropCanvasWidth = canvasWidth;
        this.cropCanvasHeight = canvasHeight;
        this.setImageStyleProperty();
        add(embededImage);
    }

    /**
     * Updates preview area regarding the current selection of GWTCropper
     * 
     * @param cropShapeWidth
     * @param cropShapeHeight
     * @param cropLeft
     * @param cropTop
     */
    void updatePreview(int cropShapeWidth, int cropShapeHeight, int cropLeft, int cropTop) {

        switch (this.fixedSide) {
            case WIDTH:
                this.proportion = (double) this.fixedValue / (double) cropShapeWidth;

                this.imageW = (int) ( (this.cropCanvasWidth * this.fixedValue) / cropShapeWidth);
                this.imageH = (int) ( (this.imageW * this.cropCanvasHeight) / this.cropCanvasWidth);
                this.height = (int) ( (cropShapeHeight * this.fixedValue) / cropShapeWidth);
                break;

            case HEIGHT:
                this.proportion = (double) this.fixedValue / (double) cropShapeHeight;

                this.imageH = (int) (this.cropCanvasHeight * this.fixedValue / cropShapeHeight);
                this.imageW = (int) (this.imageH * this.cropCanvasWidth / this.cropCanvasHeight);
                this.width = (int) (cropShapeWidth * this.fixedValue / cropShapeHeight);
                break;
        }

        final double left = this.proportion * cropLeft;
        final double top = this.proportion * cropTop;
        this.embededImage.getElement().getStyle().setMarginLeft(-left, Unit.PX);
        this.embededImage.getElement().getStyle().setMarginTop(-top, Unit.PX);

        this.embededImage.setSize(this.imageW + "px", this.imageH + "px");

        // change this widget size
        getElement().getStyle().setWidth(this.width, Unit.PX);
        getElement().getStyle().setHeight(this.height, Unit.PX);
    }
    
    // Private methods

    private void setImageStyleProperty(){
        getElement().getStyle().setOverflow(Overflow.HIDDEN);
        this.embededImage.getElement().getStyle().setProperty("maxWidth","none");
    }

    public static native void log(String msg) /*-{
        console.log(msg);
    }-*/;
}
