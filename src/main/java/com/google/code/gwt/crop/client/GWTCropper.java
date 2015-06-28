/**
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.code.gwt.crop.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

/**
 * 
 * <h1>GWT Cropper</h1>
 * <p><b>GWT Cropper</b> - widget that allows you to select an area of a picture and get the coordinates of this selection. It is useful, if you want to crop a picture.</p>
 * 
 * <h1>Example</h1>
 * <p>Usage example:
 * <pre>
 * final GWTCropper crop = new GWTCropper("url/to/your/uncropped/image.jpg");
 * crop.setAspectRatio(1); // square selection (optional)
 * panel.add(crop);
 * </pre>
 * <br />
 * Or, from UI XML document:
 * <pre>
 *     <ui:UiBinder ...
 *     xmlns:my="urn:import:com.google.code.gwt.crop.client">
 *
 *     <g:HTMLPanel>
 *         <my:GWTCropper imageURL="my-image-url.jpg" aspectRatio='1.5' />
 *     </g:HTMLPanel>
 * </pre>
 * </p>
 *
 * 
 * @author ilja.hamalainen@gmail.com (Ilja Hämäläinen)
 * 
 * @see <a href="http://code.google.com/p/gwt-cropper/">GWT Cropper home page</a>
 * 
 */
public class GWTCropper extends HTMLPanel implements MouseMoveHandler, MouseUpHandler, MouseOutHandler,
													TouchMoveHandler, TouchEndHandler {
	
	private final ICropperStyleSource bundleResources = GWT.create(ICropperStyleSource.class);
	
	// canvas sizes
	private int nOuterWidth = -1;
	private int nOuterHeight = -1;
	
	// selection coordinates
	private int nInnerX = -1;
	private int nInnerY = -1;
	private int nInnerWidth = -1;
	private int nInnerHeight = -1;
	
	private boolean isDown = false;
	private byte action = Constants.DRAG_NONE;
	
	// initials to provide crop actions
	private int initW = -1;
	private int initH = -1;

	// X and Y coordinates of cursor before dragging
	private int initX = -1;
	private int initY = -1;
	
	private int offsetX = -1;
	private int offsetY = -1;
	
	// instances to canvas and selection area, available for the cropper
	private final AbsolutePanelImpl _container;
	private AbsolutePanel handlesContainer;
	private HTML draggableBackground;
	private LoadHandler onCanvasLoadHandler;
	
	// settings
	private double aspectRatio = 0;
	
	// minimum size of height or width. Just to prevent selection area to be shrunk to a dot
	private final int HANDLE_SIZE = this.bundleResources.css().handleSize();
    private final int SELECTION_BORDER_SIZE = this.bundleResources.css().borderSize();
	private int MIN_WIDTH = this.HANDLE_SIZE;
	private int MIN_HEIGHT = this.HANDLE_SIZE;
	
	private IGWTCropperPreview previewWidget;
	
	private AbsolutePanelImpl selectionContainer = new AbsolutePanelImpl();

	// used by UIBuinder
	private final String imageURL;

	/**
	 * Constructor with mandatory parameter of image's URL.
	 *
	 * @param imageURL - URL of an uncropped image
	 */
	@UiConstructor
	public GWTCropper(String imageURL) {
		super("");
		this.imageURL = imageURL;

		bundleResources.css().ensureInjected();
		this._container = new AbsolutePanelImpl();
		this.addCanvas(imageURL);
		
		addDomHandler(this, MouseMoveEvent.getType());
		addDomHandler(this, MouseUpEvent.getType());
		addDomHandler(this, MouseOutEvent.getType());
		
		addDomHandler(this, TouchMoveEvent.getType());
		addDomHandler(this, TouchEndEvent.getType());
	}

	/**
	 * <p></p>Used by UiBinder to instantiate GWTCropper</p>
	 */
	@UiFactory
	GWTCropper createCropperInstanceFromUiBuilder() {
		return new GWTCropper(this.imageURL);
	}


	// ---------- Public API ------------------
	
	/**
	 * <p>Sets the fixed aspect ratio (proportion of width to height) for the selection. 
	 * User is able to change the size of a selected area, but the proportion of its 
	 * dimension will be kept unchanged.</p>
	 * 
	 * <p> Examples:
	 * <ul>
	 * <li><b>Default</b> is 0 that means the selection can have any shape.</li>
	 * 
	 * <li>Ratio is 1/1=1 that means the selection has a square shape.<br />
	 * <img width='185' height='130' src='doc-files/square.jpeg'/></li>
	 * 
	 * <li>Ratio 2/1=2 the selection has a rectangular shape where width is twice as longer as height<br />
	 * <img width='184' height='130' src='doc-files/rec21.jpeg'/></li>
	 * 
	 * <li>Ratio 1/2=0.5 the selection has a rectangular shape where height is twice as higher as width<br />
	 * <img width='188' height='131' src='doc-files/rec12.jpeg'/></li>
	 * </ul>
	 * </p>
	 * 
	 * <p><i>Usage example:</i> You can declare a side proportion in this way: <br/>
	 * <pre> cropper.setAspectRatio( (double) 1/2); </pre>
	 * </p>
	 * 
	 * @param aspectRatio - double value, proportion width/height
	 */
	public void setAspectRatio(double aspectRatio) {
		this.aspectRatio = aspectRatio;
	}
	
	public double getAspectRatio() {
		return this.aspectRatio;
	}
	
	/**
	 * <p>Get the X coordinate of the selection top left corner</p>
	 * 
	 * <p>
	 * <img width='204' height='151' src='doc-files/selection-x-coordinate.jpeg'/>
	 * </p>
	 * 
	 * @return X coordinate
	 */
	public int getSelectionXCoordinate() {
		return (this.nInnerX + this.SELECTION_BORDER_SIZE);
	}
	
	/**
	 * <p>Get the Y coordinate of the selection top left corner</p>
	 * 
	 * <p>
	 * <img width='211' height='150' src='doc-files/selection-y-coordinate.jpeg'/>
	 * </p>
	 * 
	 * @return Y coordinate
	 */
	public int getSelectionYCoordinate() {
		return (this.nInnerY + this.SELECTION_BORDER_SIZE);
	}
	
	/**
	 * <p>Get the width of the selection area</p>
	 * 
	 * <p>
	 * <img width='192' height='133' src='doc-files/selection_width.jpeg'/>
	 * </p>
	 * 
	 * @return width in pixels
	 */
	public int getSelectionWidth() {
		return this.nInnerWidth;
	}
	
	/**
	 * <p>Get the height of the selection area</p>
	 * 
	 * <p>
	 * <img width='208' height='150' src='doc-files/selection_height.jpeg'/>
	 * </p>
	 * @return height in pixels
	 */
	public int getSelectionHeight() {
		return this.nInnerHeight;
	}
	
	/**
	 * <p>Get the canvas height (original image you wish to crop)</p>
	 * 
	 * <p>
	 * <img width='252' height='175' src='doc-files/canvas_height.jpeg'/>
	 * </p>
	 * 
	 * @return height in PX
	 */
	public int getCanvasHeight() {
		return this.nOuterHeight;
	}
	
	/**
	 * <p>Get the canvas width (original image you wish to crop)</p>
	 * 
	 * <p>
	 * <img width='189' height='171' src='doc-files/canvas_width.jpeg'/>
	 * </p>
	 * 
	 * @return width in PX
	 */
	public int getCanvasWidth() {
		return this.nOuterWidth;
	}
	
	/**
	 * Shortcut for the {@link com.google.code.gwt.crop.client.GWTCropper#setInitialSelection(int, int, int, int, boolean) setInitialSelection(x, y, width, height, boolean)}
	 * method. This method sets aspect ratio 0, that means the selection may have any shape.
	 * 
	 * @param x initial X coordinate. Will be ignored if it is out of a canvas.
	 * @param y initial Y coordinate. Will be ignored if it is out of a canvas.
	 * @param width initial selection width in pixels (will be ignored, if bigger, than canvas width)
	 * @param height initial selection height in pixels (will be ignored if higher, than canvas height) 
	 */
	public void setInitialSelection(int x, int y, int width, int height) {
		this.setInitialSelection(x, y, width, height, false);
	}
	
	/**
	 * <p>Sets the initial size and position for the selected area.</p>
	 * 
	 * <p><i>Note, that all the incoming data will be validated. Thus, these requirements *must* be fulfilled:</i><br/><br/>
	 * <code>
	 *       canvas width > (initial selection X + initial selection width) <br />
	 *       canvas height > (initial selection Y + initial selection height)
	 * </code>
	 * <br /><br/>
	 * Otherwise default values will be used.
	 * </p>
	 * 
	 * @param x initial X coordinate. Will be ignored if it is out of a canvas.
	 * @param y initial Y coordinate. Will be ignored if it is out of a canvas.
	 * @param width initial selection width in pixels (will be ignored, if bigger, than canvas width)
	 * @param height initial selection height in pixels (will be ignored if higher, than canvas height) 
	 * @param shouldKeepAspectRatio if <code>true</code>, then initial aspect ratio will be used for the selection and it will keep it's shape;
	 * if <code>false</code> then the selection could have any shape.
	 */
	public void setInitialSelection(int x, int y, int width, int height, boolean shouldKeepAspectRatio) {
		
		if (shouldKeepAspectRatio) 
			this.setAspectRatio(width/height);
		
		if (width > MIN_WIDTH) 
			this.nInnerWidth = width;
		
		if (height > MIN_HEIGHT)
			this.nInnerHeight = height;
		
		if (x >= 0)
			this.nInnerX = x;
		
		if (y >= 0)
			this.nInnerY = y;
	}
	
	/**
	 * <p>Sets the minimal width for the selection (default value is 30px). <br />
	 * <i>Will be ignored if the value is greater than the initial selection width</i></p>
	 * 
	 * @param width in pixels
	 */
	public void setMinimalWidth(int width) {
		if (width > 30) this.MIN_WIDTH = width;
	}
	
	/**
	 * <p>Sets the minimal height for the selection (default value is 30px). <br />
	 * <i>Will be ignored if the value is greater than the initial selection height.</i></p>
	 * @param height in pixels
	 */
	public void setMinimalHeight(int height) {
		if (height > 30) this.MIN_HEIGHT = height;
	}
	
	/**
	 * Adds the {@link LoadEvent} handler to canvas. This event will be fired, when the canvas
	 * image is loaded and its dimensions are available.
	 * 
	 * @param handler
	 */
	public void addCanvasLoadHandler(LoadHandler handler) {
		this.onCanvasLoadHandler = handler;
	}
	
	/**
	 * Sets the cropper's size. 
	 * 
	 * @param width integer in px
	 * @param height integer in px
	 */
	public void setSize(int width, int height) {
		
		// size of parent panel, that holds this widget
		super.setSize(width + "px", height + "px");
		
		this.nOuterWidth = width;
		this.nOuterHeight = height;
	};
	
	
	/**
	 * <i><b>Deprecated.</b> This method sets the size only for parent element, but not for the whole widget.
	 * Use method {@link com.google.code.gwt.crop.client.GWTCropper#setSize(int, int) setSize(int width, int height)} instead.</i>
	 * 
	 * <p />
	 * 
	 * {@inheritDoc}
	 * 
	 * @see com.google.gwt.user.client.ui.UIObject#setSize(java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	public void setSize(String width, String height) {
		super.setSize(width, height);
	};
	
	/**
	 * Registers the {@link com.google.code.gwt.crop.client.GWTCropperPreview} widget.
	 * 
	 * @param previewWidget
	 */
    public void registerPreviewWidget(IGWTCropperPreview previewWidget){
        this.previewWidget = previewWidget;
    };
	
	// --------- private methods ------------
	
	/**
	 * Adds a canvas with background image.
	 * 
	 * @param src - image URL
	 */
	private void addCanvas(final String src) {
		
		super.setStyleName(bundleResources.css().base());
		
		final Image image = new Image(src);
		image.setStyleName(bundleResources.css().imageCanvas());
		image.addLoadHandler(new LoadHandler() {

			public void onLoad(LoadEvent event) {

				//this is a bug in IE since v.8 - maxWidth collapse image 
	            //and you cannot read its width - in some cases depends from CSS image extensions
	            image.getElement().getStyle().setProperty("maxWidth","none");
	            
				// get original image size
				if (nOuterWidth == -1) nOuterWidth = image.getWidth();
				if (nOuterHeight == -1) nOuterHeight = image.getHeight();
				
				DOM.setElementProperty(image.getElement(), "width", nOuterWidth + "");
				DOM.setElementProperty(image.getElement(), "height", nOuterHeight + "");
				image.getElement().getStyle().setPropertyPx("maxWidth", nOuterWidth);
				image.getElement().getStyle().setPropertyPx("maxHeight", nOuterHeight);
				
				_container.setWidth(nOuterWidth + "px");
				_container.setHeight(nOuterHeight + "px");
				addSelection(src);
				
				setSize(nOuterWidth, nOuterHeight);
				
				if (null != onCanvasLoadHandler)
					onCanvasLoadHandler.onLoad(event);
				
				if (null != previewWidget) {
                    previewWidget.init(src, nOuterWidth, nOuterHeight, aspectRatio);
				}

                updatePreviewWidget();
			}
			
		});
		this._container.add(image, 0, 0);
		this.add(this._container);
	}
	
	/**
	 * Adds initial selection
	 * 
	 */
	private void addSelection(final String src) {
		
		selectionContainer.addStyleName(this.bundleResources.css().selection());

		this.validateInitialData();

		selectionContainer.setWidth(this.nInnerWidth + "px");
		selectionContainer.setHeight(this.nInnerHeight + "px");
		
		// add background image for the selection
		Image imgSelectionBg = new Image(src);
		if (nOuterWidth != -1) {
			DOM.setElementProperty(imgSelectionBg.getElement(), "width", nOuterWidth + "");
			imgSelectionBg.getElement().getStyle().setPropertyPx("maxWidth", nOuterWidth);
		}
		
		if (nOuterHeight != -1) {
			DOM.setElementProperty(imgSelectionBg.getElement(), "height", nOuterHeight + "");
			imgSelectionBg.getElement().getStyle().setPropertyPx("maxHeight", nOuterHeight);
		}
		
		selectionContainer.add(imgSelectionBg, -this.nInnerX - 1,  -this.nInnerY - 1);
		this._container.add(selectionContainer, this.nInnerX, this.nInnerY);
		
		this.buildSelectionArea();
		
		this._container.add(this.handlesContainer, this.nInnerX, this.nInnerY);
	}

	/**
	 * Validates all initial data. This method is called after the canvas image becomes loaded and we know, what are its actual 
	 * dimensions. If any of data are incorrect, then set the default values.
	 */
	private void validateInitialData() {
		
		final boolean isDefaultWidth = this.nInnerX == -1 && this.nInnerWidth == -1;
		final boolean isInvalidWidthAndX = this.nOuterWidth < (this.nInnerX + this.nInnerWidth);
		
		if (isDefaultWidth || isInvalidWidthAndX) {
			this.nInnerX = (int) (nOuterWidth * 0.2);
			this.nInnerWidth = (int) (nOuterWidth * 0.2);
		}

		// minimal width couldn't be more, than initial selection.
		if (this.MIN_WIDTH > this.nInnerWidth) this.MIN_WIDTH = this.HANDLE_SIZE;
		
		final boolean isDefaultHeight = this.nInnerY == -1 && this.nInnerHeight == -1;
		final boolean isInvalidHeightY = this.nOuterHeight < (this.nInnerY + this.nInnerHeight);
		
		if (isDefaultHeight || isInvalidHeightY) {
			this.nInnerY = (int) (nOuterHeight * 0.2);
			this.nInnerHeight = (int) ( (this.aspectRatio == 0) ? (nOuterHeight * 0.2) : (nInnerWidth / aspectRatio) );
		}
		
		// minimal height couldn't be more, than initial selection.
		if (this.MIN_HEIGHT > this.nInnerHeight) this.MIN_HEIGHT = this.HANDLE_SIZE;
	}

	/**
	 * Add special handles. User can drag these handle and change form of the selected area
	 * 
	 * @return container with handles and needed attached event listeners
	 */
	private AbsolutePanel buildSelectionArea() {
		
		// add selection handles
		this.handlesContainer = new AbsolutePanel();
		
		this.handlesContainer.setWidth(this.nInnerWidth + "px");
		this.handlesContainer.setHeight(this.nInnerHeight + "px");
		
		this.handlesContainer.setStyleName(this.bundleResources.css().handlesContainer());
		this.handlesContainer.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		
		// append background
		this.draggableBackground = this.appendDraggableBackground();
		
		// find the center of draggable handle to make an offset for the positioning
		final int h = this.HANDLE_SIZE / 2;

		/*
			1px and 2px below are correction because of 1px border. 
			We need to position handle exactly on the center of the selection corner.
		*/

		// append top left corner handler. 
		this.appendHandle(Cursor.NW_RESIZE, Constants.DRAG_TOP_LEFT_CORNER, -h, 0, 0, -(h + 1));
		
		// append top right corner handler
		this.appendHandle(Cursor.NE_RESIZE, Constants.DRAG_TOP_RIGHT_CORNER, -h, -(h + 2), 0, 0);
		
		// append bottom left corner handler
		this.appendHandle(Cursor.SW_RESIZE, Constants.DRAG_BOTTOM_LEFT_CORNER, 0, 0, -(h + 2), -(h + 1));
		
		// append bottom right corner handler
		this.appendHandle(Cursor.SE_RESIZE, Constants.DRAG_BOTTOM_RIGHT_CORNER, 0, -(h + 2), -(h + 2), 0);
		
		return handlesContainer;
	}

	/**
	 * Creates one small draggable selection handle and appends it to the corner of selection area. 
	 * User can drag this handle to change shape of the selection area
	 * 
	 * @param cursor cursor type for the CSS
	 * @param actionType action type for the event processor
	 * @param top  top value in PX
	 * @param right right value in PX
	 * @param bottom bottom value in PX
	 * @param left left value in PX
	 */
	private void appendHandle(Cursor cursor, final byte actionType, int top, int right, int bottom, int left) {
		
		HTML handle = new HTML();
		handle.setStyleName(this.bundleResources.css().handle());
		handle.getElement().getStyle().setCursor(cursor);
		
		handle.addMouseDownHandler(new MouseDownHandler() {

			public void onMouseDown(MouseDownEvent event) {
				isDown = true;
				action = actionType;
			}
		});
		handle.addTouchStartHandler(new TouchStartHandler() {
			
			public void onTouchStart(TouchStartEvent event) {
				isDown = true;
				action = actionType;
			}
		});
		
		if (top != 0) handle.getElement().getStyle().setTop(top, Unit.PX);
		if (right != 0) handle.getElement().getStyle().setRight(right, Unit.PX);
		if (bottom != 0) handle.getElement().getStyle().setBottom(bottom, Unit.PX);
		if (left != 0) handle.getElement().getStyle().setLeft(left, Unit.PX);
		
		this.handlesContainer.add(handle);	
	}

	
	
	/**
	 * Append draggable background for the selection area
	 */
	private HTML appendDraggableBackground() {
		
		final HTML backgroundHandle = new HTML();
		backgroundHandle.setWidth(this.nInnerWidth + "px");
		backgroundHandle.setHeight(this.nInnerHeight + "px");
		backgroundHandle.getElement().getStyle().setCursor(Cursor.MOVE);
		backgroundHandle.addStyleName(this.bundleResources.css().selectionDraggableBackground());
		
		backgroundHandle.addMouseDownHandler(new MouseDownHandler() {

			public void onMouseDown(MouseDownEvent event) {
				isDown = true;
				action = Constants.DRAG_BACKGROUND;
			}
		});
		backgroundHandle.addTouchStartHandler(new TouchStartHandler() {
			
			public void onTouchStart(TouchStartEvent event) {
				isDown = true;
				action = Constants.DRAG_BACKGROUND;
			}
		});
		
		this.handlesContainer.add(backgroundHandle, 0, 0);
		
		return backgroundHandle;
	}
	
	/**
	 * provides dragging action
	 * 
	 * @param cursorX - cursor X-position relatively the canvas
	 * @param cursorY - cursor Y-position relatively the canvas
	 */
	private void provideDragging(int cursorX, int cursorY) {
		
		Element elH = null; // handle's container
		Element elS = null; // selection's container
		Element elImg = null;
		
		int futureWidth = 0;
		int futureHeight = 0;
		
		switch (this.action) {
			
		
			case Constants.DRAG_BACKGROUND:
				
				if (offsetX == -1) {
					offsetX = cursorX - _container.getWidgetLeft(this.handlesContainer);
				}
				if (offsetY == -1) {
					offsetY = cursorY - _container.getWidgetTop(this.handlesContainer);
				}
				
				elH = this.handlesContainer.getElement();
				
				this.nInnerX = cursorX - offsetX;
				this.nInnerY = cursorY - offsetY;
				
				// don't drag selection out of the canvas borders
				if (this.nInnerX < -SELECTION_BORDER_SIZE) this.nInnerX = -SELECTION_BORDER_SIZE;
				if (this.nInnerY < -SELECTION_BORDER_SIZE) this.nInnerY = -SELECTION_BORDER_SIZE;
				if (this.nInnerX + this.nInnerWidth > this.nOuterWidth + SELECTION_BORDER_SIZE) this.nInnerX = this.nOuterWidth - this.nInnerWidth + SELECTION_BORDER_SIZE;
				if (this.nInnerY + this.nInnerHeight > this.nOuterHeight + SELECTION_BORDER_SIZE) this.nInnerY = this.nOuterHeight - this.nInnerHeight + SELECTION_BORDER_SIZE;
				
				elH.getStyle().setLeft(this.nInnerX, Unit.PX);
				elH.getStyle().setTop(this.nInnerY, Unit.PX);
				
				elS = this.selectionContainer.getElement();
				elS.getStyle().setLeft(this.nInnerX, Unit.PX);
				elS.getStyle().setTop(this.nInnerY, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setLeft(-this.nInnerX - SELECTION_BORDER_SIZE, Unit.PX);
				elImg.getStyle().setTop(-this.nInnerY - SELECTION_BORDER_SIZE, Unit.PX);
				break;
				
				
			case Constants.DRAG_TOP_LEFT_CORNER:
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(this.handlesContainer);
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(this.handlesContainer);
					initH = nInnerHeight;
				}
				futureWidth = initW + (initX - cursorX);
				futureHeight = initH + (initY - cursorY);
				
				if (futureWidth < this.MIN_WIDTH || futureHeight < this.MIN_HEIGHT) {
					return;
				}
				
				this.nInnerWidth = futureWidth;
				this.nInnerHeight = futureHeight;
				
				this.nInnerX = cursorX;
				this.nInnerY = cursorY;
				
				// compensation for specified aspect ratio
				if (this.aspectRatio != 0) {
					if (abs(this.initX - this.nInnerX) > abs(this.initY - this.nInnerY)) {
						int newHeight = (int) (this.nInnerWidth / this.aspectRatio);
						this.nInnerY -= newHeight - this.nInnerHeight;
						
						// to prevent resizing out of the canvas on the Y axes
						if (this.nInnerY <= 0) {
							this.nInnerY = 0;
							newHeight = this.initY + this.initH;
							this.nInnerWidth = (int) (newHeight * this.aspectRatio);
							this.nInnerX = this.initX - (int) (this.initY * this.aspectRatio); 
						}
						
						this.nInnerHeight = newHeight;
					}
					else {
						int newWidth = (int) (this.nInnerHeight * this.aspectRatio);
						this.nInnerX -= newWidth - this.nInnerWidth;
						
						// to prevent resizing out of the canvas on the X axis
						if (this.nInnerX < 0) {
							this.nInnerX = 0;
							newWidth = this.initX + this.initW;
							this.nInnerHeight = (int) (newWidth / this.aspectRatio);
							this.nInnerY = this.initY - (int) (this.initX / this.aspectRatio);
						}
						
						this.nInnerWidth = newWidth;
					}
				}
				
				elH = this.handlesContainer.getElement();
				
				elH.getStyle().setLeft(this.nInnerX, Unit.PX);
				elH.getStyle().setTop(this.nInnerY, Unit.PX);
				elH.getStyle().setWidth(nInnerWidth, Unit.PX);
				elH.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elS = this.selectionContainer.getElement();
				elS.getStyle().setLeft(this.nInnerX, Unit.PX);
				elS.getStyle().setTop(this.nInnerY, Unit.PX);
				elS.getStyle().setWidth(nInnerWidth, Unit.PX);
				elS.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setLeft(-this.nInnerX - 1, Unit.PX);
				elImg.getStyle().setTop(-this.nInnerY - 1, Unit.PX);
				
				Element el3 = this.draggableBackground.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);
				break;
				
				
			case Constants.DRAG_TOP_RIGHT_CORNER:
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(this.handlesContainer) + nInnerWidth;
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(this.handlesContainer);
					initH = nInnerHeight;
				}
				
				futureWidth = initW + (cursorX - initX);
				futureHeight = initH + (initY - cursorY);
				
				if (futureWidth < this.MIN_WIDTH || futureHeight < this.MIN_HEIGHT) {
					return;
				}
				
				nInnerWidth = futureWidth;
				nInnerHeight = futureHeight;
				
				// compensation for specified aspect ratio
				if (this.aspectRatio != 0) {
					if (abs(initX - cursorX) > abs(initY - cursorY)) {
						// move cursor right, top side has been adjusted automatically
						
						int newHeight = (int) (nInnerWidth / this.aspectRatio);
						cursorY -= newHeight - nInnerHeight;
						
						// to prevent resizing out of the canvas on the Y axes
						if (cursorY <= 0) {
							cursorY = 0;
							newHeight = this.initY + this.initH;
							this.nInnerWidth = (int) (newHeight * this.aspectRatio);
						}
						
						nInnerHeight = newHeight;
					}
					else {
						// move cursor up, right side has been adjusted automatically
						
						nInnerWidth = (int) (nInnerHeight * this.aspectRatio);
						
						// to prevent resizing out of the canvas on the X axis
						if ((this.nInnerWidth + this.nInnerX) >= this.nOuterWidth) {
							this.nInnerWidth = this.nOuterWidth - this.nInnerX;
							this.nInnerHeight = (int) (this.nInnerWidth / this.aspectRatio);
							this.nInnerY = this.initY - (int) ((this.nOuterWidth - this.nInnerX - this.initW) / this.aspectRatio);
							cursorY = this.nInnerY;
						}
					}
				}
				
				this.nInnerY = cursorY;
				
				elH = this.handlesContainer.getElement();
				
				elH.getStyle().setTop(cursorY, Unit.PX);
				elH.getStyle().setWidth(nInnerWidth, Unit.PX);
				elH.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elS = this.selectionContainer.getElement();
				elS.getStyle().setTop(cursorY, Unit.PX);
				elS.getStyle().setWidth(nInnerWidth, Unit.PX);
				elS.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setTop(-cursorY - 1, Unit.PX);
				
				el3 = this.draggableBackground.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);		
				break;
				
				
			case Constants.DRAG_BOTTOM_LEFT_CORNER:
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(this.handlesContainer);
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(this.handlesContainer) + nInnerHeight;
					initH = nInnerHeight;
				}
				
				futureWidth = initW + (initX - cursorX);
				futureHeight = initH + (cursorY - initY);
				
				if (futureWidth < this.MIN_WIDTH || futureHeight < this.MIN_HEIGHT) {
					return;
				}
				
				nInnerWidth = futureWidth;
				nInnerHeight = futureHeight;
				
				// compensation for specified aspect ratio
				if (this.aspectRatio != 0) {
					if (abs(initX - cursorX) > abs(initY - cursorY)) {
						// cursor goes left, bottom side goes down...
						
						nInnerHeight = (int) (nInnerWidth / this.aspectRatio);
						
						// to prevent resizing out of the canvas on the Y axis
						if ((this.nInnerHeight + this.nInnerY) >= this.nOuterHeight) {
							this.nInnerHeight = this.nOuterHeight - this.nInnerY; 
							this.nInnerWidth = (int) (this.nInnerHeight * this.aspectRatio);
							this.nInnerY = this.nOuterHeight - this.nInnerHeight;
							cursorX = this.initX - (int) ((this.nOuterHeight - this.initY) * this.aspectRatio);
						}
						
					}
					else {
						// cursor goes down, left side goes to left
						
						int newWidth = (int) (nInnerHeight * this.aspectRatio);
						cursorX -= newWidth - nInnerWidth;
						
						// to prevent resizing out of the canvas on the X axis
						if (cursorX <= 0) {
							newWidth = this.nInnerWidth + this.initX;
							this.nInnerHeight = (int) (newWidth / this.aspectRatio);
							cursorX = 0;
						}
						
						nInnerWidth = newWidth;
					}
				}
				
				this.nInnerX = cursorX;
				
				elH = this.handlesContainer.getElement();
				
				elH.getStyle().setLeft(cursorX, Unit.PX);
				elH.getStyle().setWidth(nInnerWidth, Unit.PX);
				elH.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elS = this.selectionContainer.getElement();
				elS.getStyle().setLeft(cursorX, Unit.PX);
				elS.getStyle().setWidth(nInnerWidth, Unit.PX);
				elS.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setLeft(-cursorX - 1, Unit.PX);
				
				el3 = this.draggableBackground.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);
				break;
				
				
			case Constants.DRAG_BOTTOM_RIGHT_CORNER:
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(this.handlesContainer) + nInnerWidth;
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(this.handlesContainer) + nInnerHeight;
					initH = nInnerHeight;
				}
				
				futureWidth = initW + (cursorX - initX);
				futureHeight = initH + (cursorY - initY);
				
				if (futureWidth < this.MIN_WIDTH || futureHeight < this.MIN_HEIGHT) {
					return;
				}
				
				nInnerWidth = futureWidth;
				nInnerHeight = futureHeight;
				
				// compensation for specified aspect ratio
				if (this.aspectRatio != 0) {
					if (abs(initX - cursorX) > abs(initY - cursorY)) {
						// cursor goes right, bottom side goes down...
						
						nInnerHeight = (int) (nInnerWidth / this.aspectRatio);
						
						// to prevent resizing out of the canvas on the Y axis
						if ((this.nInnerHeight + this.nInnerY) >= this.nOuterHeight) {
							this.nInnerHeight = this.nOuterHeight - this.nInnerY; 
							this.nInnerWidth = (int) (this.nInnerHeight * this.aspectRatio);
							this.nInnerY = this.nOuterHeight - this.nInnerHeight;
							cursorX = this.nOuterWidth;
						}
						
					}
					else {
						// cursor goes down, right side goes to right
						nInnerWidth = (int) (nInnerHeight * this.aspectRatio);
						
						// to prevent resizing out of the canvas on the X axis
						if (this.nInnerWidth + this.nInnerX >= this.nOuterWidth) {
							this.nInnerWidth = this.nOuterWidth - this.nInnerX;
							this.nInnerHeight = (int) (this.nInnerWidth / this.aspectRatio);
							cursorX = this.nOuterHeight;
						}
					}
				}
				
				elH = this.handlesContainer.getElement();
				elH.getStyle().setWidth(nInnerWidth, Unit.PX);
				elH.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elS = this.selectionContainer.getElement();
				elS.getStyle().setWidth(nInnerWidth, Unit.PX);
				elS.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				el3 = this.draggableBackground.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);
				break;
				
				
			default:
				break;
		}
		
	}
	
	/**
	 * Resets all initial values.
	 * 
	 */
	private void reset() {
		
		this.initX = -1;
		this.initY = -1;
		this.initW = -1;
		this.initH = -1;
		this.offsetX = -1;
		this.offsetY = -1;
		this.action = Constants.DRAG_NONE;
	}
	
	/**
	 * Returns absolute value
	 * 
	 * @param value
	 * @return absolute value
	 */
	private int abs(int value) {
		return value >= 0 ? value : -value;
	}

	// DOM HANDLERS
	
	/**
	 * {@inheritDoc}
	 */
	public void onMouseMove(MouseMoveEvent event) {
		
		if (this.isDown) {
			
			this.provideDragging(event.getRelativeX(this._container.getElement()), 
					event.getRelativeY(this._container.getElement()));
			
	        this.updatePreviewWidget();
		}
	}

	/**
	 * Update preview widget if needed.
	 */
	private void updatePreviewWidget() {
		if (previewWidget != null) {
		    previewWidget.updatePreview(
		    		this.getSelectionWidth(),
		    		this.getSelectionHeight(), 
		    		this.getSelectionXCoordinate(),
		    		this.getSelectionYCoordinate());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onTouchMove(TouchMoveEvent event) {
		if (this.isDown) {
			
			JsArray<Touch> touches = event.getTouches();
			if (touches.length() > 0) {
				
				int x = touches.get(0).getRelativeX(this._container.getElement());
				int y = touches.get(0).getRelativeY(this._container.getElement());
				
				if (x < 0 || y < 0 || x > nOuterWidth || y > nOuterHeight) {					
					
					/*
					 * There is no such method as "onMouseOut" for touching, so we can't rely
					 * on event handler. We should process manually these cases, when finger (i.e. cursor) is 
					 * out of the cropper area.
					 * 
					 * If user moves his finger out of the canvas, then "stick" the selection to the canvas edges
					 */
					if (x < 0) x = 0;
					if (x > nOuterWidth) x = nOuterWidth;
					if (y < 0) y = 0;
					if (y > nOuterHeight) y = nOuterHeight; 
				}

				this.provideDragging(x, y);
			}
			
		}
	}

	/**
	 * Resets the dragging state. After this method, the cropper
	 * presumes that the dragging action is finished.
	 */
	private void resetDraggingState() {
		if (this.isDown) {
			this.isDown = false;
			this.reset();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onMouseUp(MouseUpEvent event) {
		this.resetDraggingState();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onMouseOut(MouseOutEvent event) {
		
		/*
		 * When the cursor is out of the canvas, we want to
		 * snap the selection to appropriate canvas border. So, before
		 * we reset the dragging action, let's drag handle last time.
		 * 
		 * @see Issue 12.
		 */
		int x = event.getRelativeX(this._container.getElement());
		int y = event.getRelativeY(this._container.getElement());
		
		// correct coordinates, that are out of canvas
		if (x < 0) x = 0;
		if (x > this.nOuterWidth) x = this.nOuterWidth;
		if (y < 0) y = 0;
		if (y > this.nOuterHeight) x = this.nOuterHeight;
		
		this.provideDragging(x, y);
		
		/*
		 * if cursor is out of canvas and the mouse button is pressed,
		 * then we want to "unclick" the mouse button programmatically.
		 * Otherwise the selection would become "sticky".
		 */
		this.resetDraggingState();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onTouchEnd(TouchEndEvent event) {
		this.resetDraggingState();
	}
	
	/**
	 * Absolute panel with one overrided method, that prevents a static positioning
	 * when a widget has (-1, -1) coordinates.
	 * 
	 * @author ilja.hamalainen@gmail.com (Ilja Hämäläinen)
	 *
	 */
	private static class AbsolutePanelImpl extends AbsolutePanel {
		
		/**
		 * Original method causes the child widget to be positioned statically,
		 * if the left and top are (-1, -1). In our case the background could be positioned
		 * (-1,-1), when the selected area is placed on the top left corner.
		 * 
		 * @See com.google.gwt.user.client.ui.AbsolutePanel.setWidgetPosition(Widget, int, int)
		 */
		@Override
		protected void setWidgetPositionImpl(Widget w, int left, int top) {
			com.google.gwt.user.client.Element h = w.getElement();
			DOM.setStyleAttribute(h, "position", "absolute");
			DOM.setStyleAttribute(h, "left", left + "px");
			DOM.setStyleAttribute(h, "top", top + "px");
		}
	}
}
