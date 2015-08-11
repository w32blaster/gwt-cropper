package com.google.code.gwt.crop.client;

public interface IGWTCropperPreview {

  /**
   * GWTCropper calls this method after the image is loaded 
   * and all dimensions are known.
   * 
   * @param imageUrl - image URL for preview
   * @param canvasWidth
   * @param canvasHeight
   * @param aspectRatio
   */
  void init(String imageUrl, int canvasWidth, int canvasHeight, double aspectRatio);

  /**
   * GWTCropper calls this method whenever the selected area changes.
   * 
   * @param cropShapeWidth
   * @param cropShapeHeight
   * @param cropLeft
   * @param cropTop
   */
  void updatePreview(int cropShapeWidth, int cropShapeHeight, int cropLeft, int cropTop);

}