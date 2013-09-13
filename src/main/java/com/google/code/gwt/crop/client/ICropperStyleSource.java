package com.google.code.gwt.crop.client;

/**
 * Interface is used to give different styles for different device types.
 * Actual implementation could be defined through the deferred binding.
 *  
 * @author ilja.hamalainen@gmail.com (Ilja Hämäläinen)
 *
 */
public interface ICropperStyleSource {
	
	/**
	 * <p>Initiates proper implementation of the current style bundle.</p>
	 * 
	 * <p>Create inner interface extending {@link com.google.gwt.resources.client.ClientBundle ClientBundle} 
	 * with one single method, that returns the {@link com.google.code.gwt.crop.client.CropperStyleResource CropperStyleResource}
	 * type. Add the <code>@Source("yourStyleFileName.css")</code>
	 * annotation to this method to provide path to your CSS file.</p>
	 * 
	 * @return GWTCropperStyle instance of the ClientBundle style, annotated with your own CSS file name.
	 * @see <a href="http://code.google.com/p/gwt-cropper/source/browse/src/main/java/com/google/code/gwt/crop/client/StylesDesktopImpl.java" target="_blank">StylesDesktopImpl.java</a> as an example
	 */
	CropperStyleResource css();
}
