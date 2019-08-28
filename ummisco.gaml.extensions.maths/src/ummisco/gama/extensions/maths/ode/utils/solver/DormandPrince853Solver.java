/*********************************************************************************************
 *
 * 'DormandPrince853Solver.java, in plugin ummisco.gama.extensions.maths, is part of the source code of the GAMA
 * modeling and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.extensions.maths.ode.utils.solver;

import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

import msi.gama.util.IList;
import msi.gama.util.IMap;

public class DormandPrince853Solver extends Solver {

	public DormandPrince853Solver(final double minStep, final double maxStep, final double scalAbsoluteTolerance,
			final double scalRelativeTolerance, final IMap<String, IList<Double>> integrated_val) {
		super((minStep + maxStep) / 2,
				new DormandPrince853Integrator(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance),
				integrated_val);
	}

}