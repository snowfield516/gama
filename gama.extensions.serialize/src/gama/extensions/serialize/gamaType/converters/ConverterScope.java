/*********************************************************************************************
 *
 * 'ConverterScope.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.extensions.serialize.gamaType.converters;

import gama.kernel.simulation.SimulationAgent;
import gama.runtime.scope.IScope;

public class ConverterScope {
	SimulationAgent simAgt;
	IScope scope;
	
	public ConverterScope(IScope s){
		scope = s;
		simAgt=null;
	}

	public IScope getScope() { return scope; }
	public SimulationAgent getSimulationAgent() { return simAgt; }
	public void setSimulationAgent(SimulationAgent sim){ simAgt = sim;}
}