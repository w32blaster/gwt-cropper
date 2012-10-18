package com.google.code.gwt.crop.client.widget;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.HTML;

public class DraggableHandle extends HTML implements ClickHandler, 
		MouseDownHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler, MouseMoveHandler {

	private boolean isDown = false;
	private IOnGrag onGrag;
	
	private Element parentEl;
	
	public DraggableHandle() {
		super("");
		
		super.addMouseDownHandler(this);
		super.addMouseMoveHandler(this);
		super.addMouseUpHandler(this);
		super.addMouseOverHandler(this);
		super.addMouseOutHandler(this);
		super.addClickHandler(this);
	}
	
	public void setOnDrag(IOnGrag onGrag) {
		this.onGrag = onGrag;
	}
	
	public void setParentElement(Element el) {
		this.parentEl = el;
	}

	final public void onMouseUp(MouseUpEvent event) {
		isDown = false;
		onGrag.resetCursorOffset();
	}

	final public void onMouseDown(MouseDownEvent event) {
		isDown = true;
	}

	final public void onClick(ClickEvent event) {
		System.out.println("onClick");
	}

	public void onMouseMove(MouseMoveEvent event) {
		
		if (isDown) {
			onGrag.onDrag(event.getRelativeX(this.parentEl),
						event.getRelativeY(this.parentEl));
		}
	}

	public void onMouseOver(MouseOverEvent event) {
		
	}

	public void onMouseOut(MouseOutEvent event) {
		isDown = false;
	}
	
	/**
	 * Interface for callback to be fired on the element dragging
	 * 
	 * @author ilja
	 *
	 */
	public interface IOnGrag {
		
		/**
		 * Implementation of the dragging.
		 * 
		 * @param x - coordinate X relative parent element
		 * @param y - coordinate Y relative parent element
		 */
		void onDrag(int x, int y);
		
		/**
		 * Usually dragging mechanisms use offsets for cursor, because 
		 * user grabs the object somewhere in the middle, not the top left corner.
		 * When user releases this object, all this offsets should be reseted. This method allows
		 * to do that
		 */
		void resetCursorOffset();
	}

}
