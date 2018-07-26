package msi.gaml.compilation;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;

/**
 * Instances of this interface are supposed to validate (or not) individual operators. They must be declared along the
 * 'operator' annotation, in a 'validator' annotation in order to be called by the semantic validation process.
 * 
 * @author A. Drogoul
 * @since July 2018
 *
 */
public interface IOperatorValidator extends IValidator {
	/**
	 * Called during the semantic validation process, this method should ensure that informations, warnings or errors
	 * specific to the context of this operator are produced. It is not allowed (except in some rare circumstances) to
	 * manipulate the arguments. For example, substituting an argument for another. The description and the underlying
	 * emf object are passed mainly for the purpose of emitting errors/warnings, e.g.
	 * 
	 * context.error("... text...", IGamlIssue.UNKNOWN_VARIABLE, emfContext); or,
	 * 
	 * context.warning(" ... text ...", IGamlIssue.WRONG_TYPE, emfContext, Types.INT);
	 * 
	 * @param context
	 *            The statement in which the operator is situated. Can be null for individual expressions used in
	 *            monitors, for instance. This validator, although it has access to the whole description, is not
	 *            supposed to modify it, especially because the validation of the other expressions and children of this
	 *            description can be incomplete
	 * @param emfContext
	 *            The underlying emf object corresponding to this operator. Note that it is not always known (in which
	 *            case it is null), notably when the statement is synthetic. It is strictly forbidden to modify it.
	 * @param arguments
	 *            the array of expressions corresponding to the arguments of the operator. None are null, but the size
	 *            of the array will depend on the operator
	 * @return true if the operator is considered as valid (even when informations and warnings are produced during the
	 *         validation), false otherwise. In that case, it is better to produce meaningful errors before.
	 */
	@Override
	boolean validate(IDescription context, EObject emfContext, IExpression... arguments);

}