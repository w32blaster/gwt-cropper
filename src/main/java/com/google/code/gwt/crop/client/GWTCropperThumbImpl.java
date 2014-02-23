package com.google.code.gwt.crop.client;

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
    
    private int cropCanvasWidth; 
    private int cropCanvasHeight;
    
    private int heightScaleDivisor = -1;
    private int widthScaleDivisor = -1;
    
    private Image embededImage;
    
    private final double proportion;
    
    public GWTCropperThumbImpl(double proportion){
        this.proportion = proportion; 
        setStyleProperties();
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
        setImageStyleProperty();
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
        double left = (-1) * Math.round(this.proportion * cropLeft);
        double top = (-1) * Math.round(this.proportion * cropTop);
        this.embededImage.getElement().getStyle().setMarginLeft(left, Unit.PX);
        this.embededImage.getElement().getStyle().setMarginTop(top, Unit.PX);
        this.embededImage.setSize(
        		Math.round((cropCanvasWidth * this.proportion) / cropShapeWidth ) + "px", 
        		Math.round((cropCanvasHeight * this.proportion) / cropShapeHeight )+ "px");
    }
    
    // Private methods
    
    /**
     * Applies necessary styling for the widget
     */
    private void setStyleProperties(){
        getElement().getStyle().setOverflow(Overflow.HIDDEN);
        getElement().getStyle().setWidth(100, Unit.PX);
        getElement().getStyle().setHeight(100, Unit.PX);
    }
  
    private void setImageStyleProperty(){
        this.embededImage.getElement().getStyle().setProperty("maxWidth","none");
    }    
}
