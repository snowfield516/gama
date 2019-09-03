/*********************************************************************************************
 *
 * 'GamaNavigatorImportMenu.java, in plugin gama.ui.base.navigator, is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ui.navigator.support;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import gama.ui.base.resources.GamaIcons;

/**
 * Class GamaNavigatorMenus.
 *
 * @author drogoul
 * @since 8 mars 2015
 *
 */
public class GamaNavigatorImportMenu extends GamaNavigatorMenu { // NO_UCD (unused code)

	public GamaNavigatorImportMenu(final IStructuredSelection selection) {
		this.selection = selection;
	}

	IStructuredSelection selection;

	private final SelectionListener fromDisk = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			openWizard("org.eclipse.ui.wizards.import.FileSystem", selection);
		}

	};

	private final SelectionListener fromArchive = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			openWizard("org.eclipse.ui.wizards.import.ZipFile", selection);
		}

	};
	private final SelectionListener project = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			openWizard("gama.ui.base.import.ExternalProject", selection);
		}

	};

	@Override
	protected void fillMenu() {
		action("Import project...", project, GamaIcons.create("navigator/navigator.import.project2").image());
		sep();
		action("Import resources into projects from disk...", fromDisk,
				GamaIcons.create("navigator/navigator.import.disk2").image());
		action("Import resources into projects from archive...", fromArchive,
				GamaIcons.create("navigator/navigator.import.archive2").image());
	}

}