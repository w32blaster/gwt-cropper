/**
 * Copyright 2011 ArcBees Inc.
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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * GWT Cropper - widget allowing to select any area on the top of a picture 
 * in order to crop it.
 * 
 * @author ilja
 *
 */
public class GWTCropper extends HTMLPanel implements MouseMoveHandler, MouseUpHandler, MouseOutHandler {
	
	private final IBundleResources bundleResources = GWT.create(IBundleResources.class);
	
	// canvas sizes
	private int nOuterWidth;
	private int nOuterHeight;
	
	// selection coordinates
	private int nInnerX;
	private int nInnerY;
	private int nInnerWidth;
	private int nInnerHeight;
	
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
	private final AbsolutePanel _container;
	private AbsolutePanel handlesContainer;
	private AbsolutePanel selectionContainer = new AbsolutePanel();
	private HTML draggableBackground;
	
	// settings
	private float aspectRatio = 0;
	
	// minimum size of height or width. Just to prevent selection area to be shrunk to a dot
	private final int MIN_SIZE = 20; 
	
	/**
	 * Bundle of all the CSS resources
	 * 
	 * @author ilja
	 *
	 */
	public interface IBundleResources extends ClientBundle {

		interface GWTCropperStyle extends CssResource {
		    String base();
		    String imageCanvas();
		    String selection();
		    String handlesContainer();
		    String handle();
		}
	
		@Source("GWTCropper.css")
		GWTCropperStyle css();
	}
	
	/**
	 * Constructor. Requires URL to the full image to be cropped
	 * 
	 * @param strImageURL
	 */
	public GWTCropper(String strImageURL) {
		super("");
		
		bundleResources.css().ensureInjected();
		this._container = new AbsolutePanel();
		this.addCanvas(strImageURL);
		
		addDomHandler(this, MouseMoveEvent.getType());
		addDomHandler(this, MouseUpEvent.getType());
		addDomHandler(this, MouseOutEvent.getType());
	}

	// ---------- Public API ------------------
	
	/**
	 * <p>Sets the aspect ratio (proportion of width to height) for the selection.</p>
	 * 
	 * <p> Examples:
	 * <ul>
	 * <li><b>Default</b> is 0, it means, that the selection can have any shape.</li>
	 * <li>Ratio is 1/1=1, then the selection will be square.</li>
	 * <li>Ratio 2/1=2, then the selection will be rectangular where width is twice longer than height</li>
	 * <li>ratio 1/2=0.5, then the selection will be rectangular where height is twice higher than width</li>
	 * </ul>
	 * </p>
	 * 
	 * <p><i>Usage example:</i> You can declare a side proportion in this way: <br/>
	 * <pre> cropper.setAspectRatio( (float) 1/2); </pre>
	 * </p>
	 * 
	 * @param acpectRatio - float value, proportion width/height
	 */
	public void setAspectRatio(float acpectRatio) {
		this.aspectRatio = acpectRatio;
	}
	
	public float getAspectRatio() {
		return this.aspectRatio;
	}
	
	/**
	 * Get the X coordinate of the selection top left corner (CSS parameter: left)
	 * 
	 * @return X coordinate
	 */
	public int getSelectionXCoordinate() {
		return this.nInnerX;
	}
	
	/**
	 * Get the Y coordinate of the selection top left corner (CSS parameter: top)
	 * 
	 * @return Y coordinate
	 */
	public int getSelectionYCoordinate() {
		return this.nInnerY;
	}
	
	/**
	 * Get the width of the selection area
	 * 
	 * @return width in pixels
	 */
	public int getSelectionWidth() {
		return this.nInnerWidth;
	}
	
	/**
	 * Get the height of the selection area
	 * 
	 * @return height in pixels
	 */
	public int getSelectionHeight() {
		return this.nInnerHeight;
	}
	
	/**
	 * Get the canvas height (original image you wish to crop)
	 * 
	 * @return height in PX
	 */
	public int getCanvasHeight() {
		return this.nOuterHeight;
	}
	
	/**
	 * Get the canvas width (original image you wish to crop)
	 * 
	 * @return width in PX
	 */
	public int getCanvasWidth() {
		return this.nOuterWidth;
	}
	
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
				nOuterWidth = image.getWidth();
				nOuterHeight = image.getHeight();
				_container.setWidth(nOuterWidth + "px");
				_container.setHeight(nOuterHeight + "px");
				addSelection(src);
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
		
		// set initial coordinates
		this.nInnerX = (int) (nOuterWidth * 0.2);
		this.nInnerY = (int) (nOuterHeight * 0.2);
		this.nInnerWidth = (int) (nOuterWidth * 0.2);
		this.nInnerHeight = (int) ((this.aspectRatio == 0) ? (nOuterHeight * 0.2) : (nInnerWidth / aspectRatio)); 
		
		selectionContainer.setWidth(this.nInnerWidth + "px");
		selectionContainer.setHeight(this.nInnerHeight + "px");
		
		// add background image for selection
		selectionContainer.add(new Image(src), -this.nInnerX, -this.nInnerY);
		this._container.add(selectionContainer, this.nInnerX, this.nInnerY);
		
		this.buildSelectionArea(selectionContainer);
		
		this._container.add(this.handlesContainer, this.nInnerX, this.nInnerY);
	}

	/**
	 * Add special handles. User can drag these handle and change form of the selected area
	 * 
	 * @return container with handles and needed attached event listeners
	 */
	private AbsolutePanel buildSelectionArea(final AbsolutePanel selectionContainer) {
		
		// add selection handles
		this.handlesContainer = new AbsolutePanel();
		
		this.handlesContainer.setWidth(this.nInnerWidth + "px");
		this.handlesContainer.setHeight(this.nInnerHeight + "px");
		
		this.handlesContainer.setStyleName(this.bundleResources.css().handlesContainer());
		this.handlesContainer.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		
		// append background
		this.draggableBackground = this.appendDraggableBackground();	

		// append top left corner handler
		this.appendHandle(Cursor.NW_RESIZE, Constants.DRAG_TOP_LEFT_CORNER, -5, 0, 0, -5);
		
		// append top right corner handler
		this.appendHandle(Cursor.NE_RESIZE, Constants.DRAG_TOP_RIGHT_CORNER, -5, -5, 0, 0);
		
		// append bottom left corner handler
		this.appendHandle(Cursor.SW_RESIZE, Constants.DRAG_BOTTOM_LEFT_CORNER, 0, 0, -5, -5);
		
		// append bottom right corner handler
		this.appendHandle(Cursor.SE_RESIZE, Constants.DRAG_BOTTOM_RIGHT_CORNER, 0, -5, -5, 0);
		
		return handlesContainer;
	}

	/**
	 * Creates small draggable selection handle and appends it to the corner of selection area. User can drag 
	 * this handle and change shape of the selection area
	 * 
	 * @param cursor - cursor type for the CSS
	 * @param actionType - action type for the event processor
	 * @param top - top value in PX
	 * @param right - right value in PX
	 * @param bottom - bottom value in PX
	 * @param left - left value in PX
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
		
		if (top != 0) handle.getElement().getStyle().setTop(top, Unit.PX);
		if (right != 0) handle.getElement().getStyle().setRight(right, Unit.PX);
		if (bottom != 0) handle.getElement().getStyle().setBottom(bottom, Unit.PX);
		if (left != 0) handle.getElement().getStyle().setLeft(left, Unit.PX);
		
		this.handlesContainer.add(handle);	
	}

	/**
	 * Append draggable selection background
	 * 
	 * @param sc - container of selection
	 * @param hc - container of handles
	 */
	private HTML appendDraggableBackground() {
		
		final HTML backgroundHandle = new HTML();
		backgroundHandle.setWidth(this.nInnerWidth + "px");
		backgroundHandle.setHeight(this.nInnerHeight + "px");
		backgroundHandle.getElement().getStyle().setCursor(Cursor.MOVE);
		
		backgroundHandle.addMouseDownHandler(new MouseDownHandler() {

			public void onMouseDown(MouseDownEvent event) {
				isDown = true;
				action = Constants.DRAG_BACKGROUND;
			}
		});
		
		this.handlesContainer.add(backgroundHandle, 0, 0);
		
		return backgroundHandle;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onMouseUp(MouseUpEvent event) {
		this.isDown = false;
		this.reset();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onMouseMove(MouseMoveEvent event) {
		
		if (this.isDown) {
			
			this.provideDragging(event.getRelativeX(this._container.getElement()), 
					event.getRelativeY(this._container.getElement()));
		}
	}
	
	/**
	 * provides dragging action
	 * 
	 * @param cursorX - cursor X-position relatively the canvas
	 * @param cursorY - cursor Y-position relatively the canvas
	 */
	private void provideDragging(int cursorX, int cursorY) {
		
		Element el = null;
		Element el2 = null;
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
				
				el = this.handlesContainer.getElement();
				
				this.nInnerX = cursorX - offsetX;
				this.nInnerY = cursorY - offsetY;
				
				// don't drag selection out of the canvas borders
				if (this.nInnerX < 0) this.nInnerX = 0;
				if (this.nInnerY < 0) this.nInnerY = 0;
				if (this.nInnerX + this.nInnerWidth > this.nOuterWidth) this.nInnerX = this.nOuterWidth - this.nInnerWidth;
				if (this.nInnerY + this.nInnerHeight > this.nOuterHeight) this.nInnerY = this.nOuterHeight - this.nInnerHeight;
				
				el.getStyle().setLeft(this.nInnerX, Unit.PX);
				el.getStyle().setTop(this.nInnerY, Unit.PX);
				
				el2 = this.selectionContainer.getElement();
				el2.getStyle().setLeft(this.nInnerX, Unit.PX);
				el2.getStyle().setTop(this.nInnerY, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setLeft(-this.nInnerX, Unit.PX);
				elImg.getStyle().setTop(-this.nInnerY, Unit.PX);
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
				
				if (futureWidth < this.MIN_SIZE || futureHeight < this.MIN_SIZE) {
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
				
				el = this.handlesContainer.getElement();
				
				el.getStyle().setLeft(this.nInnerX, Unit.PX);
				el.getStyle().setTop(this.nInnerY, Unit.PX);
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				el2 = this.selectionContainer.getElement();
				el2.getStyle().setLeft(this.nInnerX, Unit.PX);
				el2.getStyle().setTop(this.nInnerY, Unit.PX);
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setLeft(-this.nInnerX, Unit.PX);
				elImg.getStyle().setTop(-this.nInnerY, Unit.PX);
				
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
				
				if (futureWidth < this.MIN_SIZE || futureHeight < this.MIN_SIZE) {
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
				
				el = this.handlesContainer.getElement();
				
				el.getStyle().setTop(cursorY, Unit.PX);
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				el2 = this.selectionContainer.getElement();
				el2.getStyle().setTop(cursorY, Unit.PX);
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setTop(-cursorY, Unit.PX);
				
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
				
				if (futureWidth < this.MIN_SIZE || futureHeight < this.MIN_SIZE) {
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
				
				el = this.handlesContainer.getElement();
				
				el.getStyle().setLeft(cursorX, Unit.PX);
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				el2 = this.selectionContainer.getElement();
				el2.getStyle().setLeft(cursorX, Unit.PX);
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setLeft(-cursorX, Unit.PX);
				
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
				
				if (futureWidth < this.MIN_SIZE || futureHeight < this.MIN_SIZE) {
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
				
				el = this.handlesContainer.getElement();
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				el2 = this.selectionContainer.getElement();
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
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

	/**
	 * {@inheritDoc}
	 */
	public void onMouseOut(MouseOutEvent event) {
		if (this.isDown) {
			
			/*
			 * if cursor is out of canvas and the mouse is clicked,
			 * then we want to "unclick" the mouse button programmatically.
			 * Otherwise the selection would become "sticky".
			 */
			this.isDown = false;
			this.reset();
		}
	}
}
