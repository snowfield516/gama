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
 * - Benoit Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.jogl;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.awt.AbstractAWTDisplaySurface;
import msi.gama.gui.displays.layers.LayerManager;
import msi.gama.jogl.scene.ModelScene;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.Camera.Arcball.Vector3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ISymbol;
import collada.Output3D;
import com.vividsolutions.jts.geom.Envelope;

@display("opengl")
public final class JOGLAWTDisplaySurface extends AbstractAWTDisplaySurface implements IDisplaySurface.OpenGL {

	private static final long serialVersionUID = 1L;

	protected Point mousePosition;

	private boolean output3D = false;
	// Environment properties useful to set the camera position.

	// Use to toggle the 3D view.
	public boolean threeD = false; // true; //false;

	// Use to toggle the Picking mode
	// private boolean picking = false;

	// Use to toggle the Arcball view
	public boolean arcball = false;

	// Use to toggle the selectRectangle tool
	public boolean selectRectangle = false;

	// Use to toggle the SplitLayer view
	public boolean splitLayer = false;

	// Us toggle to switch cameras
	public boolean switchCamera = false;

	// Use to toggle the Rotation view
	public boolean rotation = false;

	// Used to follow an agent
	public boolean followAgent = false;
	public IAgent agent;

	// Use to draw .shp file
	final String[] shapeFileName = new String[1];

	// private (return the renderer of the openGLGraphics)
	private JOGLAWTGLRenderer renderer;

	// private: the class of the Output3D manager
	Output3D output3DManager;

	public JOGLAWTDisplaySurface(final Object ... args) {
		displayBlock = new Runnable() {

			// Remove all the already existing entity in openGLGraphics and redraw the existing ones.
			@Override
			public void run() {
				final ModelScene s = renderer.getScene();
				if ( s != null ) {
					s.wipe(renderer);

					// FIXME: Why setting this at each run??
					renderer.setTessellation(getOutput().getTesselation());
					renderer.setStencil(getOutput().getStencil());
					renderer.setShowFPS(getOutput().getShowFPS());
					renderer.setAggregated(getOutput().getAggregated());
					renderer.setDrawEnv(getOutput().getDrawEnv());
					renderer.setAmbientLightValue(getOutput().getAmbientLightColor());
					renderer.setPolygonMode(getOutput().getPolygonMode());
					renderer.setCameraPosition(getOutput().getCameraPos());
					renderer.setCameraLookPosition(getOutput().getCameraLookPos());
					renderer.setCameraUpVector(getOutput().getCameraUpVector());
					if ( autosave ) {
						snapshot();
					}

					drawDisplaysWithoutRepainting();

					if ( output3D ) {
						output3DManager.updateOutput3D(renderer);
					}

				}
				canBeUpdated(true);
			}
		};

	}

	@Override
	public void initialize(final double env_width, final double env_height, final LayeredDisplayOutput out) {
		super.initialize(env_width, env_height, out);

		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		// Call sun.awt.noerasebackground to reduce the flickering when creating a popup menu,
		// due to AWT erasing the GLCanvas every time before jogl repaint.
		System.setProperty("sun.awt.noerasebackground", "true");
		renderer = new JOGLAWTGLRenderer(this);
		renderer.setAntiAliasing(getQualityRendering());
		// renderer.setPolygonTriangulated(false);
		renderer.setTessellation(getOutput().getTesselation());
		renderer.setStencil(getOutput().getStencil());
		renderer.setZFighting(getOutput().getZFighting());
		renderer.setShowFPS(getOutput().getShowFPS());
		renderer.setAggregated(getOutput().getAggregated());
		renderer.setDrawEnv(getOutput().getDrawEnv());
		renderer.setAmbientLightValue(getOutput().getAmbientLightColor());
		renderer.setDiffuseLightValue(getOutput().getDiffuseLightColor());
		renderer.setPolygonMode(getOutput().getPolygonMode());
		renderer.setCameraPosition(getOutput().getCameraPos());
		renderer.setCameraLookPosition(getOutput().getCameraLookPos());
		renderer.setCameraUpVector(getOutput().getCameraUpVector());
		// GuiUtils.debug("JOGLAWTDisplaySurface.initialize : jogl canvas added; self size = " + getSize());
		add(renderer.canvas, BorderLayout.CENTER);
		// openGLGraphicsGLRender.animator.start();
		zoomFit();
		// new way
		// createIGraphics();
		this.setVisible(true);

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				// resizeImage(getWidth(), getHeight());
				if ( renderer != null && renderer.canvas != null ) {
					renderer.canvas.setSize(getWidth(), getHeight());
				}
				initOutput3D(out.getOutput3D(), out.getOutput3DNbCycles());
				updateDisplay();
				previousPanelSize = getSize();
			}
		});
		renderer.animator.start();
	}

	@Override
	protected void createIGraphics() {
		if ( iGraphics == null ) {
			iGraphics = new JOGLAWTDisplayGraphics(this, renderer);
		}
	}

	@Override
	public void setPaused(final boolean flag) {
		if ( flag == true ) {
			if ( renderer.animator.isAnimating() ) {
				renderer.animator.stop();
			}
		} else {
			if ( !renderer.animator.isAnimating() ) {
				renderer.animator.start();
			}
		}
		super.setPaused(flag);
	}

	@Override
	public void outputChanged(final double env_width, final double env_height, final LayeredDisplayOutput output) {
		setBackgroundColor(output.getBackgroundColor());
		this.setBackground(getBackgroundColor());
		setEnvWidth(env_width);
		setEnvHeight(env_height);
		widthHeightConstraint = env_height / env_width;

		if ( manager == null ) {
			manager = new LayerManager(this);
			final List<? extends ISymbol> layers = output.getChildren();
			for ( final ISymbol layer : layers ) {
				// IDisplay d =
				manager.addLayer(LayerManager.createLayer((ILayerStatement) layer, env_width, env_height, iGraphics));
			}

		} else {
			manager.outputChanged();
		}
		// paintingNeeded.release();
	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		// we take the smallest dimension as a guide
		final int[] dim = new int[2];
		dim[0] = vwidth > vheight ? (int) (vheight / widthHeightConstraint) : vwidth;
		dim[1] = vwidth <= vheight ? (int) (vwidth * widthHeightConstraint) : vheight;
		return dim;
	}

	public void selectAgents(final IAgent agent, final int layerId) {
		menuManager.buildMenu(renderer.camera.getMousePosition().x, renderer.camera.getMousePosition().y, agent);

	}

	public void selectSeveralAgents(final Iterator<IAgent> agents, final int layerId) {
		menuManager.buildMenu(false, renderer.camera.getMousePosition().x, renderer.camera.getMousePosition().y,
			new GamaList(agents));
	}

	@Override
	public void forceUpdateDisplay() {
		updateDisplay();
	}

	public void drawDisplaysWithoutRepainting() {
		if ( iGraphics == null ) { return; }
		// ex[0] = null;
		manager.drawLayersOn(iGraphics);
	}

	@Override
	public void dispose() {
		renderer.dispose();
		if ( manager != null ) {
			manager.dispose();
		}
	}

	@Override
	public void zoomIn() {
		renderer.camera.zoom(true);
	}

	@Override
	public void zoomOut() {
		renderer.camera.zoom(false);
	}

	@Override
	public void zoomFit() {
		resizeImage(getWidth(), getHeight());
		if ( renderer != null ) {
			renderer.frame = 0;
			renderer.camera.zeroVelocity();
			renderer.camera.resetCamera(getEnvWidth(), getEnvHeight(), threeD);
		}
		super.zoomFit();
	}

	@Override
	public void toggleView() {
		threeD = !threeD;
		zoomFit();
		updateDisplay();
	}

	@Override
	public void togglePicking() {
		renderer.setPicking(!renderer.isPicking());
		renderer.camera.zeroVelocity();
		if ( !renderer.isPicking() ) {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		} else {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public void toggleArcball() {
		arcball = !arcball;
	}

	@Override
	public void toggleInertia() {
		renderer.setInertia(!renderer.getInertia());
	}

	@Override
	public void toggleSelectRectangle() {
		selectRectangle = !selectRectangle;
		if ( selectRectangle && !renderer.camera.isViewIn2DPlan() ) {
			zoomFit();
		}

	}

	@Override
	public void toggleTriangulation() {
		renderer.triangulation = !renderer.triangulation;
	}

	@Override
	public void toggleSplitLayer() {

		splitLayer = !splitLayer;
		final int nbLayers = this.getManager().getItems().size();
		int i = 0;
		final Iterator<ILayer> it = this.getManager().getItems().iterator();
		while (it.hasNext()) {
			final ILayer curLayer = it.next();
			if ( splitLayer ) {// Split layer
				curLayer.setElevation((double) i / nbLayers);
			} else {// put all the layer at zero
				curLayer.setElevation(0.0);
			}
			i++;
		}
		this.updateDisplay();
	}

	@Override
	public void toggleRotation() {
		rotation = !rotation;
	}

	@Override
	public void toggleCamera() {
		// TODO Auto-generated method stub
		switchCamera = !switchCamera;
		renderer.switchCamera();
		zoomFit();
		updateDisplay();
	}

	/**
	 * Add a simple feature collection from a .Shp file.
	 */
	@Override
	public void addShapeFile() {

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// Display.getDefault().asyncExec(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// final Shell shell = new Shell(Display.getDefault());
		// final FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		//
		// dialog.setText("Browse for a .shp file");
		//
		// dialog.setFilterPath(System.getProperty(GAMA.getModel().getProjectPath()));
		//
		// dialog.setFilterExtensions(new String[] { "*.shp" });
		//
		// if ( dialog.open() != null ) {
		//
		// final String path = dialog.getFilterPath();
		//
		// final String[] names = dialog.getFileNames();
		//
		// for ( int i = 0; i < names.length; i++ ) {
		// shapeFileName[i] = path + "/" + names[i];
		// System.out.println(shapeFileName[i]);
		// }
		//
		// }
		//
		// renderer.myShapeFileReader = new ShapeFileReader(shapeFileName[0]);
		// final SimpleFeatureCollection myCollection =
		// renderer.myShapeFileReader
		// .getFeatureCollectionFromShapeFile(renderer.myShapeFileReader.store);
		// final Color color =
		// new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
		// (int) (Math.random() * 255));
		// renderer.getScene().addCollections(myCollection, color);
		// // FIXME: Need to reinitialise th displaylist
		//
		// }
		// });
		// }
		// }).start();

	}

	//
	// @Override
	// public IGraphics.OpenGL getIGraphics() {
	// return (IGraphics.OpenGL) super.getIGraphics();
	// }

	@Override
	public void focusOn(final IShape geometry, final ILayer display) {
		// FIXME: Need to compute the depth of the shape to adjust ZPos value.
		// FIXME: Problem when the geometry is a point how to determine the maxExtent of the shape?
		// FIXME: Problem when an agent is placed on a layer with a z_value how to get this z_layer value to offset it?
		ILocation p = geometry.getLocation();
		renderer.camera.zoomFocus(p.getX(), p.getY(), p.getZ(), geometry.getEnvelope().maxExtent());
	}

	@Override
	public void followAgent(final IAgent a) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				GuiUtils.asyncRun(new Runnable() {

					@Override
					public void run() {
						ILocation l = agent.getGeometry().getLocation();
						Envelope env = agent.getGeometry().getEnvelope();

						double xPos = l.getX() - getEnvWidth() / 2;
						double yPos = -(l.getY() - getEnvHeight() / 2);

						double zPos = env.maxExtent() * 2 + l.getZ();
						double zLPos = -(env.maxExtent() * 2);

						renderer.camera.updatePosition(xPos, yPos, zPos);
						renderer.camera.lookPosition(xPos, yPos, zLPos);
					}
				});
			}
		}).start();

	}

	@Override
	public void initOutput3D(final boolean yes, final ILocation output3DNbCycles) {
		output3D = yes;
		if ( output3D ) {
			output3DManager = new Output3D(output3DNbCycles, renderer);
			// (new Output3D()).to3DGLGEModel(((JOGLAWTDisplayGraphics) openGLGraphics).myJTSGeometries,
			// openGLGraphicsGLRender);
		}
	}

	@Override
	public synchronized void addMouseListener(final MouseListener e) {
		// renderer.canvas.addMouseListener(e);
	}

	@Override
	public synchronized void addMouseMotionListener(final MouseMotionListener e) {
		renderer.canvas.addMouseMotionListener(e);
	}

	public Color getBackgroundColor() {
		return bgColor;
	}

	@Override
	public final boolean resizeImage(final int x, final int y) {
		super.resizeImage(x, y);
		// int[] point = computeBoundsFrom(x, y);
		// int imageWidth = Math.max(1, point[0]);
		// int imageHeight = Math.max(1, point[1]);
		// this.createNewImage(imageWidth, imageHeight);
		// createIGraphics();
		setSize(x, y);
		return true;
	}

	/**
	 * Method getModelCoordinates()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinates()
	 */
	@Override
	public GamaPoint getModelCoordinates() {
		Point mp = renderer.camera.getMousePosition();
		if ( mp == null ) { return null; }
		Point2D.Double p = renderer.getRealWorldPointFromWindowPoint(renderer.camera.getMousePosition());
		if ( p == null ) { return null; }
		return new GamaPoint(p.x, -p.y);
	}

	/**
	 * Method getCameraPosition()
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#getCameraPosition()
	 */
	@Override
	public double[] getCameraPosition() {
		if ( renderer == null && renderer.camera == null ) { return new double[] { 0, 0, 0 }; }
		Vector3D v = renderer.camera.getPosition();
		return new double[] { v.x, v.y, v.z };
	}

	/**
	 * Method computeInitialZoomLevel()
	 * @see msi.gama.gui.displays.awt.AbstractAWTDisplaySurface#computeInitialZoomLevel()
	 */
	@Override
	protected Double computeInitialZoomLevel() {
		if ( renderer == null && renderer.camera == null ) { return 1.0; }
		return renderer.camera.zoomLevel();
	}

	@Override
	public int getDisplayWidth() {
		return (int) (super.getDisplayWidth() * getZoomLevel());
	}

	@Override
	public void setBackgroundColor(final Color c) {
		super.setBackgroundColor(c);
		if ( iGraphics != null ) {
			iGraphics.fillBackground(bgColor, 1);
		}
	}
}
