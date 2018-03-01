package fiji.plugin.trackmate.extra.action;

import java.util.Collection;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.action.AbstractTMAction;
import fiji.plugin.trackmate.action.TrackMateAction;
import fiji.plugin.trackmate.action.TrackMateActionFactory;
import fiji.plugin.trackmate.gui.TrackMateGUIController;
import fiji.plugin.trackmate.visualization.TrackMateModelView;
import fiji.plugin.trackmate.visualization.hyperstack.HyperStackDisplayer;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

/**
 * Exports {@link Spot}s to {@link EllipseRoi} in the ImageJ1
 * {@link RoiManager}.
 * 
 * @author Stefan Helfrich (University of Konstanz)
 */
public class EllipseRoiExporter extends AbstractTMAction
{

	public static final ImageIcon ICON = new ImageIcon( EllipseRoiExporter.class.getResource( "EllipseRoiIcon.png" ) );

	public static final String NAME = "Export visible spots to Ellipse ROIs";

	public static final String KEY = "EXPORT_TO_IJ_ELLIPSE_ROIS";

	public static final String INFO_TEXT = "<html>" +
			"Generates a round EllipseRoi per visible spot and adds" +
			"it to the RoiManager." +
			"</html>";

	private final ImagePlus imp;

	public EllipseRoiExporter( final ImagePlus imp )
	{
		this.imp = imp;
	}

	@Override
	public void execute( final TrackMate trackmate )
	{
		if ( imp == null )
		{
			logger.error( "Could not find a suitable target image in this TrackMate session.\n" );
			return;
		}

		double dx = imp.getCalibration().pixelWidth;
		double dy = imp.getCalibration().pixelHeight;
		double dz = imp.getCalibration().pixelDepth;

		// Get RoiManager instance
		RoiManager roiManager = RoiManager.getInstance();
		if ( null == roiManager ) {
			roiManager = new RoiManager();
		}

		// Model
		final Model model = trackmate.getModel();
		final Iterable< Spot > iterable = model.getSpots().iterable( true );
		for ( final Spot spot : iterable )
		{
			// Get radius
			double radius = spot.getFeature( Spot.RADIUS ) / dx;

			// Get position
			double positionX = spot.getFeature( Spot.POSITION_X ) / dx + 0.5;
			double positionY = spot.getFeature( Spot.POSITION_Y ) / dy + 0.5;
			double positionZ = spot.getFeature( Spot.POSITION_Z ) / dz;
			double frame = spot.getFeature( Spot.FRAME );

			double x1 = positionX - radius;
			double x2 = positionX + radius;
			double y1 = positionY;
			double y2 = positionY;
			Roi roi = new EllipseRoi( x1, y1, x2, y2, 1d ) ;
			if ( imp.isHyperStack() )
			{
				// Set ROI's position for hyperstack
				roi.setPosition( 0, ( int ) positionZ + 1, ( int ) frame + 1 );
			}
			else
			{
				// Set ROI's position for regular stack
				roi.setPosition( ( int ) frame + 1 );
			}

			roiManager.addRoi( roi );
		}
		logger.log( " Done.\n" );
	}

	@Plugin( type = TrackMateActionFactory.class )
	public static class Factory implements TrackMateActionFactory
	{

		@Override
		public String getInfoText()
		{
			return INFO_TEXT;
		}

		@Override
		public String getKey()
		{
			return KEY;
		}

		@Override
		public TrackMateAction create( final TrackMateGUIController controller )
		{
			final Collection< TrackMateModelView > views = controller.getGuimodel().getViews();
			ImagePlus imp = null;
			for ( final TrackMateModelView view : views )
			{
				if ( view.getKey().equals( HyperStackDisplayer.KEY ) )
				{
					final HyperStackDisplayer hsd = ( HyperStackDisplayer ) view;
					imp = hsd.getImp();
					break;
				}
			}

			return new EllipseRoiExporter( imp );
		}


		@Override
		public ImageIcon getIcon()
		{
			return ICON;
		}

		@Override
		public String getName()
		{
			return NAME;
		}
	}

}
