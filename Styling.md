

# Introduction #

In this article I will show you how to create your own styles for the selection area in cropper.

**Since version 0.4.5!**

<img height='492' width='575' title='Memphis May Fire in GWT-Cropper' src='http://wiki.gwt-cropper.googlecode.com/hg/custom_styles.jpeg' />

By default GWT-Cropper uses three different styles:
  * [GWTCropper.css](http://code.google.com/p/gwt-cropper/source/browse/src/main/java/com/google/code/gwt/crop/client/GWTCropper.css) as default for desktop computers
  * [GWTCropperTablet.css](http://code.google.com/p/gwt-cropper/source/browse/src/main/java/com/google/code/gwt/crop/client/GWTCropperTablet.css) for tablets
  * [GWTCropperIE.css](http://code.google.com/p/gwt-cropper/source/browse/src/main/java/com/google/code/gwt/crop/client/GWTCropperIE.css) for Internet Explorer (9 and lower)
You could overwrite all of them or only some files

# 1. Create CSS file #
In this tutorial we are going to overwrite all the styles with only one CSS file.

Create a **.css** file in your package. This file should be accessible in the classpath. In my case it is `MyCustomStyle.css`.

Put the content into it. Copy the whole content from the [original GWTCropper.css file](http://code.google.com/p/gwt-cropper/source/browse/src/main/java/com/google/code/gwt/crop/client/GWTCropper.css) and paste it to your just created new file. Then adjust styles that you wish to modify.

Please remember,
  * **Do not change the style class names!** This file **must** represent the source of the [CropperStyleResource](http://code.google.com/p/gwt-cropper/source/browse/src/main/java/com/google/code/gwt/crop/client/CropperStyleResource.java). So if you changed any style name, you would not be able to build your project.
  * Please pay attention to the handle size definition:
```
@def handleSize 10px; 
```
This value will be used in many inner calculation, so do not remove it or change this variable name. You can modify only its value, if you need. It is recommended to choose value that is divisible by 2 for styling purposes, because GWT-Cropper will divide this value by 2 in order to find a handle centre.

In my example I've changed the selection colour, handles size and borders.

## Hint: use patterns encoded in base64 scheme ##
Do not forget, that you can set the background image using encoded binary image data in base64. Try [Patternify](http://www.patternify.com/) for that.

<img height='429' width='553' title='Bring Me The Horizon in the GWT-Cropper' src='http://wiki.gwt-cropper.googlecode.com/hg/custom_styles2.jpeg' />

In this short example I used this CSS-class:
```
.base {
	background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAADCAYAAABWKLW/AAAAFElEQVQImWNggILYgs3/cTAYGBgAju8HfloVoUQAAAAASUVORK5CYII=) repeat;
	position: relative;
}
```
Looks great, right? :)

# 2. Create initializer of your style #
Create new class implementing [com.google.code.gwt.crop.client.ICropperStyleSource](http://code.google.com/p/gwt-cropper/source/browse/src/main/java/com/google/code/gwt/crop/client/ICropperStyleSource.java) interface. In my case its name is `MyCustomStyle.java`. Put this code into it:

```
package com.google.code.gwt.cropper.demo.client;

import com.google.code.gwt.crop.client.CropperStyleResource;
import com.google.code.gwt.crop.client.ICropperStyleSource;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;


public class MyCustomStyle implements ICropperStyleSource {
	
	interface IStyle extends ClientBundle {
		
		@Source("MyCustomStyle.css") // <-- this is my custom CSS file path
		CropperStyleResource getStyles();
	}

	private final IStyle bundleResources = GWT.create(IStyle.class);
	
	/**
	 * {@inheritDoc}
	 */
	public CropperStyleResource css() {
		return bundleResources.getStyles();
	}
}
```

Do not forget to provide correct name of your CSS file in `@Source` annotation!

If you want to rewrite all three default CSS files, then you should create three different [com.google.code.gwt.crop.client.ICropperStyleSource](http://code.google.com/p/gwt-cropper/source/browse/src/main/java/com/google/code/gwt/crop/client/ICropperStyleSource.java) implementations, like in example above.

# 3. Declare it in your .gwt.xml file #
Check the original file [GWTCropper.gwt.xml](http://code.google.com/p/gwt-cropper/source/browse/src/main/java/com/google/code/gwt/crop/GWTCropper.gwt.xml) and pay your attention at the `<replace-with>` section. Decide what you are going to override in your project and put according lines to your **.gwt.xml** file.

For example, in this example I wish to replace all the styles with one new CSS file:
```
	<inherits name='com.google.code.gwt.crop.FormFactor'/>
	
	<replace-with class="com.google.code.gwt.cropper.demo.client.MyCustomStyle">
		<when-type-is class="com.google.code.gwt.crop.client.ICropperStyleSource" />
	</replace-with>
```

# 4. Inject styles #
Add this lines somewhere in your project. It could be the enter point, for instance. The most appropriate place for it is a constructor of the file where you are going to use your custom style:
```
	public Application() {
		super();
		
		final ICropperStyleSource bundleResources = GWT.create(ICropperStyleSource.class);
		bundleResources.css().ensureInjected();
	}
```

Doing that, GWT will bind appropriate style to GWT-Cropper in runtime.