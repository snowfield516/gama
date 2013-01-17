/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.ui.highlight;

import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.*;
import static org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration.*;
import java.util.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.*;

/**
 * 
 * @author Pierrick
 *         cf. http://www.eclipse.org/Xtext/documentation/latest/xtext.html#highlighting
 * 
 */
public class GamlSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

	private IHighlightedPositionAcceptor acceptor;
	Set<INode> done = new HashSet();

	// @Override
	// public void provideHighlightingForOld(final XtextResource resource,
	// final IHighlightedPositionAcceptor a) {
	// acceptor = a;
	// // if ( resource == null || resource.getParseResult() == null ) { return; }
	// TreeIterator<EObject> root = resource.getAllContents();
	//
	// while (root.hasNext()) {
	// EObject obj = root.next();
	// if ( obj != null ) {
	// if ( obj instanceof Statement ) {
	// if ( obj instanceof Definition ) {
	// setStyle(obj, VARDEF_ID, 1 /* ((Definition) obj).getName() */);
	// } else {
	// GamlFacetRef ref = ((Statement) obj).getRef();
	// if ( ref != null ) {
	// setStyle(ref, FACET_ID, 0 /* ref.getRef() */);
	// }
	// }
	// setStyle(obj, KEYWORD_ID, EGaml.getKeyOf(obj));
	// } else if ( obj instanceof GamlBinaryExpr || obj instanceof Function ) {
	// setStyle(obj, BINARY_ID, ((Expression) obj).getOp());
	// // } else if ( obj instanceof FacetExpr ) {
	// } else if ( obj instanceof Facet ) {
	// setStyle(obj, FACET_ID, 0 /* EGaml.getKeyOf(obj) */);
	// } else if ( obj instanceof TerminalExpression ) {
	// if ( !(obj instanceof StringLiteral) ) {
	// setStyle(obj, NUMBER_ID, 0);
	// }
	// } else if ( obj instanceof VariableRef ) {
	// setStyle(obj, VARIABLE_ID, NodeModelUtils.getNode(obj));
	// }
	//
	// // Ajouter la notion de scope pour pouvoir highlighter les variables globales
	//
	// }
	// }
	// done.clear();
	// }

	@Override
	// Mettre un ID sur les descriptions pour les r�p�rer plus facilement.
	public void provideHighlightingFor(final XtextResource resource,
		final IHighlightedPositionAcceptor a) {
		acceptor = a;
		TreeIterator<EObject> root = resource.getAllContents();

		while (root.hasNext()) {
			EObject obj = root.next();
			if ( obj != null ) {
				if ( obj instanceof Statement ) {
					// if ( obj instanceof Definition ) {
					if ( ((Statement) obj).getName() != null ) {
						setStyle(obj, VARDEF_ID, 1 /* ((Definition) obj).getName() */);
						// }
						// } else {
						// GamlFacetRef ref = ((Statement) obj).getRef();
						// if ( ref != null ) {
						// setStyle(ref, FACET_ID, 0 /* ref.getRef() */);
						// }
					}
					setStyle(obj, KEYWORD_ID, EGaml.getKeyOf(obj));
				} else if ( obj instanceof GamlBinaryExpr || obj instanceof Function ) {
					setStyle(obj, BINARY_ID, ((Expression) obj).getOp());
				} else if ( obj instanceof Facet ) {
					setStyle(obj, FACET_ID, 0);
					if ( ((Facet) obj).getName() != null ) {
						setStyle(obj, VARDEF_ID, 1);
					}
				} else if ( obj instanceof TerminalExpression ) {
					if ( !(obj instanceof StringLiteral) ) {
						setStyle(obj, NUMBER_ID, 0);
					}
				} else if ( obj instanceof VariableRef ) {
					setStyle(obj, VARIABLE_ID, NodeModelUtils.getNode(obj));
				}
			}

		}
		done.clear();
	}

	private void setStyle(final EObject obj, final String s, final int position) {
		// position = -1 for all the node; 0 for the first leaf node, 1 for the second one, etc.
		if ( obj != null && s != null ) {
			INode n = NodeModelUtils.getNode(obj);
			if ( n == null ) { return; }
			if ( position > -1 ) {
				int i = 0;
				for ( ILeafNode node : n.getLeafNodes() ) {
					if ( !node.isHidden() ) {
						if ( position == i ) {
							n = node;
							break;
						}
						i++;
					}
				}
			}
			if ( !done.contains(n) ) {
				done.add(n);
				acceptor.addPosition(n.getOffset(), n.getLength(), s);
			}
		}
	}

	private void setStyle(final EObject obj, final String s, final INode n) {
		acceptor.addPosition(n.getOffset(), n.getLength(), s);
		if ( !done.contains(n) ) {
			done.add(n);
			acceptor.addPosition(n.getOffset(), n.getLength(), s);
		}
	}

	private void setStyle(final EObject obj, final String s, final String text) {
		if ( text == null ) { return; }
		if ( obj != null && s != null ) {
			INode n = NodeModelUtils.getNode(obj);
			if ( n == null ) { return; }
			for ( ILeafNode node : n.getLeafNodes() ) {
				if ( !node.isHidden() ) {
					if ( NodeModelUtils.getTokenText(node).startsWith(text) ) {
						n = node;
						break;
					}
				}
			}

			if ( !done.contains(n) ) {
				done.add(n);
				acceptor.addPosition(n.getOffset(), n.getLength(), s);
			}
		}
	}
}
