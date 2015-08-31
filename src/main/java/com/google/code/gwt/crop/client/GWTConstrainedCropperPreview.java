package com.google.code.gwt.crop.client;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * <p>
 * <b>GWTConstrainedCropperPreview</b> - widget that previews the selected area
 * in a panel whose size won't exceed the given dimensions.
 * </p>
 *
 * @author Timo Hoepfner
 */
public class GWTConstrainedCropperPreview extends SimplePanel implements IGWTCropperPreview {
    private int maxWidth, maxHeight, canvasWidth, canvasHeight;
    private Image image;

    /**
     * Initiates the preview widget.
     *
     * <p>
     * Usage example: <code>new GWTConstrainedCropperPreview(100, 100)</code>
     * constructs a preview, that will scale both horizontally and vertically,
     * but never exceed 100px in either dimension.
     * </p>
     *
     * <ul>
     *    <li>For a square selection, both width and height are 100px.</li>
     *    <li>For a landscape selection, the width is 100px and height is less than 100px.</li>
     *    <li>For a portrait selection, the height is 100px and width is less than 100px.</li>
     * </ul>
     *
     * @param maxWidth
     *          - maximum width in px
     * @param maxHeight
     *          - maximum height in px
     */
    @UiConstructor
    public GWTConstrainedCropperPreview(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        getElement().getStyle().setPosition(Position.RELATIVE);
        getElement().getStyle().setOverflow(Overflow.HIDDEN);
    }

    public void init(String imageUrl, int canvasWidth, int canvasHeight, double aspectRatio) {
        if (image != null)
            super.remove(image);

        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        image = new Image(imageUrl);
        image.getElement().getStyle().setPosition(Position.ABSOLUTE);

        add(image);
    }

    public void updatePreview(int selectionWidth, int selectionHeight, int cropLeft, int cropTop) {
        double selectionRatio = (double) selectionWidth / selectionHeight;
        double containerRatio = (double) maxWidth / maxHeight;

        double scale = selectionRatio < containerRatio
                ? (double) selectionHeight / maxHeight
                : (double) selectionWidth / maxWidth;

        int containerWidth = (int) (selectionWidth / scale);
        int containerHeight = (int) (selectionHeight / scale);

        setPixelSize(containerWidth, containerHeight);

        double dimensionRatioX = (double) canvasWidth / selectionWidth;
        double dimensionRatioY = (double) canvasHeight / selectionHeight;
        double positionRatioX = (double) selectionWidth / containerWidth;
        double positionRatioY = (double) selectionHeight / containerHeight;

        int imageWidth = (int) (containerWidth * dimensionRatioX);
        int imageHeight = (int) (containerHeight * dimensionRatioY);
        int imageLeft = (int) -(cropLeft / positionRatioX);
        int imageTop = (int) -(cropTop / positionRatioY);

        image.setPixelSize(imageWidth, imageHeight);
        image.getElement().getStyle().setLeft(imageLeft, Unit.PX);
        image.getElement().getStyle().setTop(imageTop, Unit.PX);
    }
}
