/*******************************************************************************************************
 *
 * gaml.expressions.CameraPositionUnitExpression.java, in plugin gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.expressions;

import gama.common.interfaces.outputs.IGraphics;
import gama.metamodel.shape.GamaPoint;
import gama.runtime.scope.IScope;
import gaml.types.Types;

public class CameraPositionUnitExpression extends UnitConstantExpression<GamaPoint> {

	public CameraPositionUnitExpression(final String doc) {
		super(new GamaPoint(), Types.POINT, "camera_location", doc, null);
	}

	@Override
	public GamaPoint _value(final IScope scope) {
		if (scope == null)
			return (GamaPoint) getConstValue();
		final IGraphics g = scope.getGraphics();
		if (g == null || g.is2D())
			return (GamaPoint) getConstValue();
		return ((IGraphics.ThreeD) g).getCameraPos();
	}

	@Override
	public boolean isConst() {
		return false;
	}

}