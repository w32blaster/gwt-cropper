package com.google.code.gwt.crop.client.widget;

import com.google.gwt.dom.client.Element;
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

public class DraggableHandle extends HTML implements 
		MouseDownHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler {

	private boolean isDown = false;
	private IOnGrag onGrag;
	
	private Element parentEl;
	
	public DraggableHandle() {
		super("");
		
		super.addMouseDownHandler(this);
		super.addMouseUpHandler(this);
		super.addMouseOverHandler(this);
		super.addMouseOutHandler(this);

	}
	
	public void setOnDrag(IOnGrag onGrag) {
		this.onGrag = onGrag;
	}
	
	public void setParentElement(Element el) {
		this.parentEl = el;
	}

	final public void onMouseUp(MouseUpEvent event) {
		isDown = false;
		onGrag.resetInitials();
		System.out.println("Mouse Up. RESET ALL.");
	}

	final public void onMouseDown(MouseDownEvent event) {
		isDown = true;
		System.out.println("Mouse Down");
	}

	public void onMouseOver(MouseOverEvent event) {
		System.out.println("Mouse Over");
	}

	public void onMouseOut(MouseOutEvent event) {
		System.out.println("Mouse out");
		//isDown = false;
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
		 * @param cursorX
		 * @param cursorY
		 */
		void onDrag(int cursorX, int cursorY);
		
		/**
		 * Usually dragging mechanisms saves initial state to orient during 
		 * dragging. This method resets these initial values after dragging event ends
		 */
		void resetInitials();
	}

}
