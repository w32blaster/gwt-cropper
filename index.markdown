---
layout: main
title: GWT-Cropper is a plugin for GWT
header: GWT-Cropper
version: 0.5.3.1
description: GWT-Cropper is a widget for GWT, that allows you to select an area of a picture and get the coordinates of this selection.
---


<img src="http://w32blaster.github.io/gwt-cropper/images/wiki/gwt-cropper.png" width="280" height="105" alt="GWT Cropper logo" title="GWT Cropper logo" />

*GWT Cropper* is a widget for [Google Web Toolkit](http://www.gwtproject.org/), that allows you to select an area of a picture and get the coordinates of this selection. It is useful, if you want to crop a picture on the server side.

# Demo

Try the working demo:
 * Simple cropper: [demo](http://wiki.gwt-cropper.googlecode.com/hg/demo/Application.html)
 * Cropper with preview: [demo](http://wiki.gwt-cropper.googlecode.com/hg/demo/Application2.html)

<img src="http://w32blaster.github.io/gwt-cropper/images/wiki/gwt-cropper-1.jpg" width="669" height="530" alt="Gwt Cropper plugin to crop an image" />

# Get started

The plugin is very simple. In order to use it, you should follow next steps:

## 1. Import plugin to your project

If you use [Maven](http://maven.apache.org/), you can add this dependency to your POM:

```XML
      <dependency>
            <groupId>com.googlecode.gwt-cropper</groupId>
            <artifactId>gwt-crop</artifactId>
            <version>{{ page.version }}</version>
      </dependency>
```

If you need JAR files, you can download them from the [GWT-Cropper Maven Repository](http://search.maven.org/#search|gav|1|g%3A%22com.googlecode.gwt-cropper%22%20AND%20a%3A%22gwt-crop%22|) page. Just select any link *jar*, *javadoc.jar* either *sources.jar* next to the each version to download an archive you need.

## 2. Declare the plugin in your project

Add this line to your *.gwt.xml* file:

```XML
      <!-- Inherit the GWT-Cropper -->
      <inherits name='com.google.code.gwt.crop.GWTCropper'/>
```

## 3. Use it in your project

Add *GWT Cropper* to any panel in your code. Simple example (on Java):

```java
	  final GWTCropper crop = new GWTCropper("url/to/your/uncropped/image.jpg");
	  crop.setAspectRatio(1); // square selection (optional)
	  panel.add(crop);
```

Or sample code in _.ui.xml_ (since [v. 0.5.0](https://github.com/w32blaster/gwt-cropper/issues/17)):

```XML
	  <ui:UiBinder xmlns:ui='urn:ui:com.google.gwt.uibinder'
			 xmlns:g='urn:import:com.google.gwt.user.client.ui'
			 xmlns:my="urn:import:com.google.code.gwt.crop.client">

	  <my:GWTCropper  ui:field="cropper"
	           aspectRatio="1.5" imageURL="url/to/your/uncropped/image.jpg" />
```

# Tablets

*GWT Cropper* supports tablets since v.0.4.0. Tested on the IPad (2, 3, 4, mini), several Android devices and since v.0.4.1 the widget supports also Win8 tablets (including Microsoft Surface).

<img src="http://w32blaster.github.io/gwt-cropper/images/wiki/gwt-cropper-magnifier.jpg" width="650" height="480" alt="Gwt Cropper plugin to crop an image, works also on tablets" />

# Documentation
The documentation is pretty short, but in any case, please, refer to [Wiki-page](https://github.com/w32blaster/gwt-cropper/wiki/Usage) or [JavaDoc](apidocs/index.html) for details. 

Take a look at the **TipsAndTricks Tips & tricks page** to see the most common issues.
