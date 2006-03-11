/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Created on 23.06.2004
 */
/*$Id: XmlBindingTools.java,v 1.1.4.2 2006-03-11 16:42:36 dpolivaev Exp $*/

package freemind.common;

import java.awt.Dimension;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.JDialog;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.controller.actions.generated.instance.XmlAction;

/**
 * @author foltin
 * Singleton
 */
public class XmlBindingTools {

	private static XmlBindingTools instance;
    private static IBindingFactory mBindingFactory;


    private XmlBindingTools() {
	}

	public static XmlBindingTools getInstance() {
		if(instance == null) {
			instance = new XmlBindingTools();
            try {
                mBindingFactory = BindingDirectory.getFactory(XmlAction.class);
            } catch (JiBXException e) {
                e.printStackTrace();
            }

		}
		return instance;
	}

	
	public IMarshallingContext createMarshaller() {
        try {
            return mBindingFactory.createMarshallingContext();
        } catch (JiBXException e) {
            e.printStackTrace();
            return null;
        }
	}
	
	public IUnmarshallingContext createUnmarshaller() {
        try {
            return  mBindingFactory.createUnmarshallingContext();
        } catch (JiBXException e) {
            e.printStackTrace();
            return null;
        }
	}
	
	public void storeDialogPositions(Controller controller, JDialog dialog, WindowConfigurationStorage storage, String window_preference_storage_property) {
		storage.setX((dialog.getX()));
		storage.setY((dialog.getY()));
		storage.setWidth((dialog.getWidth()));
		storage.setHeight((dialog.getHeight()));
		String marshalled = marshall(storage);
		String result = marshalled;
		controller.setProperty(window_preference_storage_property, result);
	}

	public WindowConfigurationStorage decorateDialog(Controller controller, JDialog dialog, String window_preference_storage_property) {
		String marshalled = controller.getProperty(window_preference_storage_property);
		WindowConfigurationStorage result = decorateDialog(marshalled, dialog);
		return result;
	}
	
    public WindowConfigurationStorage decorateDialog(String marshalled, JDialog dialog) {
//		String unmarshalled = controller.getProperty(
//		        propertyName);
		if (marshalled != null) {
			WindowConfigurationStorage storage = (WindowConfigurationStorage) unMarshall(marshalled);
			if (storage != null) {
				dialog.setLocation(storage.getX(), storage.getY());
				dialog.getRootPane().setPreferredSize(new Dimension(storage.getWidth(), storage.getHeight()));
			}
			return storage;
		}
		return null;
    }


	public String marshall(XmlAction action) {
        // marshall:
        //marshal to StringBuffer:
        StringWriter writer = new StringWriter();
        IMarshallingContext m = XmlBindingTools.getInstance().createMarshaller();
        try {
            m.marshalDocument(action, "UTF-8", null,
                    writer);
        } catch (JiBXException e) {
            e.printStackTrace();
            return null;
        }
        String result = writer.toString();
        return result;

	}

	public XmlAction unMarshall(String inputString) {
		try {
			// unmarshall:
            IUnmarshallingContext u = XmlBindingTools.getInstance().createUnmarshaller();
			StringBuffer xmlStr = new StringBuffer( inputString);
			XmlAction doAction = (XmlAction) u.unmarshalDocument
            ( new StringReader( xmlStr.toString() )  , null);
			return doAction;
		} catch (JiBXException e) {
            e.printStackTrace();
            return null;
        }

	}


}
