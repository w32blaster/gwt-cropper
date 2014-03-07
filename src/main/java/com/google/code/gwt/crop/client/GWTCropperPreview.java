package com.google.code.gwt.crop.client;

import com.google.code.gwt.crop.client.common.Dimension;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * <p><b>GWTCropperPreview</b> - special widget that
 * shows the selected area in separate panel (i.e. "cropping preview" area).</p>
 * 
 * @author Dawid Dziewulski
 * @author Ilja Hämäläinen
 * 
 * @since 0.5.0
 * @version %I%, %G%
 */
public class GWTCropperPreview extends SimplePanel {

    /**Because crop image can be scaled. We have to remember its width and height
     * to future recounting */
    private int imageW, imageH;
    
    private int cropCanvasWidth; 
    private int cropCanvasHeight;

    // this widget dimensions
    private int width = 0;
    private int height = 0;

    private Image embeddedImage;

    private final Dimension fixedSide;
    private final int fixedValue;
    private double proportion;
    private boolean isSquare = false;

    /**
     * <p>Initiates the preview widget, that shows only selected area (i.e. "cropping preview").</p>
     *
     * <p>Usage examples:
     * 		<ul>
     * 			<li><pre>GWTCropperPreview(Dimension.WIDTH, 100)</pre> 
     * 				- in this case the width of preview widget will be constantly 100px, but the
     * 				height will be adjusted according of the selection proportion.<br />
     * 
     * 				<img width='95' height='161' src='doc-files/preview-fixed-width.jpg'/></li>
     * 
     *      	<li><pre>GWTCropperPreview(Dimension.HEIGHT, 100)</pre> 
     * 				- in this case the height of preview widget will be constantly 100px, but the
     * 				width will be adjusted accordingly of the selection proportion.<br />
     * 
     * 				<img width='161' height='95' src='doc-files/preview-fixed-height.jpg'/></li>
     * 
     * 			<li>If you set the <strong>aspect ratio = 1</strong> (square selection shape), then you can specify any
     * 				dimension either width or height. Both variants will give you the same result.</li>
     * 		<ul>
     * </p>
     *
     * @param dimension the widget side,
     *                  that will be remain constantly (Dimension.WIDTH or Dimension.HEIGHT)
     * @param value length of that side in px
     */
    @UiConstructor
    public GWTCropperPreview(Dimension dimension, int value){
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
     * For internal usage only.
     * GWTCropper initializes this widget, after
     * image will be loaded and all the dimensions will be known
     * 
     * @param imageUrl - image URL for preview
     * @param canvasWidth
     * @param canvasHeight
     * @param aspectRatio
     */
    void init(String imageUrl, int canvasWidth, int canvasHeight, double aspectRatio) {
        this.embeddedImage = new Image(imageUrl);
        
        this.cropCanvasWidth = canvasWidth;
        this.cropCanvasHeight = canvasHeight;
        this.isSquare = (aspectRatio == 1);

        if (this.isSquare) {
            if (0 == this.width) this.width = this.height;
            if (0 == this.height) this.height = this.width;
        }

        this.setImageStyleProperty();
        add(embeddedImage);
    }

    /**
     * For internal usage only.
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

                this.imageW = (int) (this.cropCanvasWidth * this.proportion);
                this.imageH = this.imageW * this.cropCanvasHeight / this.cropCanvasWidth;
                if (!this.isSquare) {
                    this.height = (int) (cropShapeHeight * this.proportion);
                }
                break;

            case HEIGHT:
                this.proportion = (double) this.fixedValue / (double) cropShapeHeight;

                this.imageH = (int) (this.cropCanvasHeight * this.proportion);
                this.imageW = this.imageH * this.cropCanvasWidth / this.cropCanvasHeight;
                if (!this.isSquare) {
                    this.width = (int) (cropShapeWidth * this.proportion);
                }
                break;
        }

        final double left = this.proportion * cropLeft;
        final double top = this.proportion * cropTop;
        this.embeddedImage.getElement().getStyle().setMarginLeft(-left, Unit.PX);
        this.embeddedImage.getElement().getStyle().setMarginTop(-top, Unit.PX);

        this.embeddedImage.setSize(this.imageW + "px", this.imageH + "px");

        // change this widget size
        getElement().getStyle().setWidth(this.width, Unit.PX);
        getElement().getStyle().setHeight(this.height, Unit.PX);
    }
    
    // Private methods

    private void setImageStyleProperty(){
        getElement().getStyle().setOverflow(Overflow.HIDDEN);
        this.embeddedImage.getElement().getStyle().setProperty("maxWidth","none");
    }
}
