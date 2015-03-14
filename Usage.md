# Introduction #

The plugin is pretty simple and it has only necessary functionality.


# Details #

You can create an instance of the plugin in this way:
```
   final GWTCropper cropper = new GWTCropper("path/to/your/uncropped/image.jpg");
   panel.add(cropper);
```

## Set the aspect ratio ##

**public void setAspectRatio(float acpectRatio)**

Sets the aspect ratio (proportion of width to height) for the selection.

Examples:

  * default is 0, it means, that the selection can have any shape.
  * ratio is 1/1=1, then the selection will be square.
  * ratio 2/1=2, then the selection will be rectangular where width is twice longer than height
  * ratio 1/2=0.5, then the selection will be rectangular where height is twice higher than width

_Usage example: You can declare a side proportion in this way:_
```
 cropper.setAspectRatio( (double) 1/2); 
```

# Initialize widget using UiBuilder #
_(since v. 0.5.0)_

You can initialize the GWT-Cropper either in Java code or in XML view as well. To add the widget to a _.ui.xml_ file, please use this code example:

```
<ui:UiBinder xmlns:ui='urn:ui:com.google.gwt.uibinder'
     xmlns:g='urn:import:com.google.gwt.user.client.ui'
     xmlns:my="urn:import:com.google.code.gwt.crop.client">

     <my:GWTCropper  ui:field="cropper" aspectRatio="1.5" imageURL="url/to/your/uncropped/image.jpg" />
```

Please notice, that the _imageUrl_ is mandatory parameter, but the _aspectRatio_ is optional (default value is 0, that means that a selection might have any shape).

## How to harvest selection coordinates ##

When you are done, you can retrieve the result coordinates of the selection. Let's say, you placed a button "Crop" near the cropper and you assigned the click handler on it. Then, inside this handler you can use next methods to yield actual information about selection:

  * cropper.getSelectionXCoordinate()
  * cropper.getSelectionYCoordinate()
  * cropper.getSelectionWidth()
  * cropper.getSelectionHeight()

To understand the public API methods, please refer to the diagram below:

<img src='http://wiki.gwt-cropper.googlecode.com/hg/gwt-cropper-diagramm.gif' alt='GWT Cropper diagram how (gwt, crop, image)' width='827' height='487' />

_(since v. 0.5.1 aforementioned methods [were calibrated](http://code.google.com/p/gwt-cropper/issues/detail?id=18))_
All these dimensions are retrieved **excluding** the selection border. Thus, if the selected area dimensions is 200x300, that means **without selection** itself. And the X,Y coordinates means _the most top-left pixel within the selection_.

_Take a look at the [DEMO](http://wiki.gwt-cropper.googlecode.com/hg/demo/Application.html)_

# Licence #
The **GWT Cropper** is distributed under [Apache Licence 2.0](http://www.apache.org/licenses/LICENSE-2.0).