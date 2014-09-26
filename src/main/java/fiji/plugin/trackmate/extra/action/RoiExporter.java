package fiji.plugin.trackmate.extra.action;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.PointRoi;
import ij.plugin.frame.RoiManager;

import java.util.Collection;
import java.util.NavigableSet;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import fiji.plugin.trackmate.LoadTrackMatePlugIn_;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.action.AbstractTMAction;
import fiji.plugin.trackmate.action.TrackMateAction;
import fiji.plugin.trackmate.action.TrackMateActionFactory;
import fiji.plugin.trackmate.gui.TrackMateGUIController;
import fiji.plugin.trackmate.visualization.TrackMateModelView;
import fiji.plugin.trackmate.visualization.hyperstack.HyperStackDisplayer;

public class RoiExporter extends AbstractTMAction
{

	public static final ImageIcon ICON = new ImageIcon( RoiExporter.class.getResource( "MultiPointRoiIcon.png" ) );

	public static final String NAME = "Export current spots to IJ rois";

	public static final String KEY = "EXPORT_TO_IJ_ROIS";

	public static final String INFO_TEXT = "<html>" +
			"Generates an IJ multi-point ROI from the visible spots "
			+ "and add them all to the ROI manager. There is one multi-point "
			+ "ROI created by frame. " +
			"</html>";

	private final ImagePlus imp;

	public RoiExporter( final ImagePlus imp )
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

		final double dx = imp.getCalibration().pixelWidth;
		final double dy = imp.getCalibration().pixelHeight;

		RoiManager roiManager = RoiManager.getInstance();
		if ( null == roiManager )
		{
			roiManager = new RoiManager();
		}
		roiManager.reset();

		final NavigableSet< Integer > frames = trackmate.getModel().getSpots().keySet();
		for ( final int frame : frames )
		{
			final int points = trackmate.getModel().getSpots().getNSpots( frame, true );
			final float[] ox = new float[ points ];
			final float[] oy = new float[ points ];
			final Iterable< Spot > iterable = trackmate.getModel().getSpots().iterable( frame, true );
			int index = 0;
			for ( final Spot spot : iterable )
			{
				final double x = spot.getDoublePosition( 0 ) / dx;
				final double y = spot.getDoublePosition( 1 ) / dy;

				ox[ index ] = ( float ) x;
				oy[ index ] = ( float ) y;

				index++;
			}

			final PointRoi roi = new PointRoi( ox, oy, points );
			roiManager.addRoi( roi );
		}
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
		public ImageIcon getIcon()
		{
			return ICON;
		}

		@Override
		public String getKey()
		{
			return KEY;
		}

		@Override
		public String getName()
		{
			return NAME;
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

			return new RoiExporter( imp );
		}

	}

	public static void main( final String[] args )
	{
		ImageJ.main( args );
		final LoadTrackMatePlugIn_ plugIn = new LoadTrackMatePlugIn_();
		plugIn.run( "samples/FakeTracks.xml" );
	}

}
