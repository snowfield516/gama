/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.extensions.fipa;

/**
 * Implementation of the FIPA Brokering interaction protocol. Reference :
 * http://www.fipa.org/specs/fipa00033/SC00033H.html
 */
public class FIPABrokering extends FIPAProtocol {

	/** Definition of protocol model. */
	private static Object[] __after_cancel = {
			FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END),  Integer.valueOf(1), null,
			FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END),  Integer.valueOf(1), null };

	/** The __after_inform. */
	private static Object[] __after_inform = {
			FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END),  Integer.valueOf(1), null,
			FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), Integer.valueOf(1), null };

	/** The __after_agree. */
	private static Object[] __after_agree = {
			FIPAConstants.Performatives.CANCEL,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			__after_cancel, FIPAConstants.Performatives.FAILURE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END),  Integer.valueOf(1), null,
			FIPAConstants.Performatives.INFORM,
			Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ),  Integer.valueOf(1),
			__after_inform };

	/** The __after_req. */
	private static Object[] __after_req = { FIPAConstants.Performatives.CANCEL,
		Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR,
			__after_cancel, FIPAConstants.Performatives.REFUSE,
			Integer.valueOf(FIPAConstants.CONVERSATION_END), Integer.valueOf(1), null,
			FIPAConstants.Performatives.AGREE,
			Integer.valueOf(FIPAConstants.NO_AGENT_ACTION_REQ), Integer.valueOf(1),
			__after_agree };

	/** The roots. */
	public static Object[] roots = { FIPAConstants.Performatives.PROXY,
			 Integer.valueOf(FIPAConstants.AGENT_ACTION_REQ), INITIATOR, __after_req };

	static {
		__after_req[3] = roots;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.misc.current_development.FIPAProtocol#getName()
	 */
	@Override
	public int getIndex() {
		return FIPAConstants.Protocols.FIPA_BROKERING;
	}
}
