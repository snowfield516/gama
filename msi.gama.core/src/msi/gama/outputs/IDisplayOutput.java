/*********************************************************************************************
 * 
 * 
 * 'IDisplayOutput.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs;

/**
 * @author drogoul
 */
public interface IDisplayOutput extends IOutput {

	public String getViewName();

	public boolean isUnique();

	public String getViewId();

	public boolean isSynchronized();

	public void setSynchronized(final boolean sync);

}
