package fiji.plugin.trackmate.extra.spotanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import fiji.plugin.trackmate.Dimension;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.features.spot.SpotAnalyzerFactory;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imglib2.meta.view.HyperSliceImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

@SuppressWarnings( "deprecation" )
@Plugin( type = SpotAnalyzerFactory.class, priority = 0d )
public class SpotMultiChannelIntensityAnalyzerFactory< T extends RealType< T > & NativeType< T > > implements SpotAnalyzerFactory< T >
{

	/*
	 * CONSTANTS
	 */

	public static final String KEY = "Spot intensity per channel";

	public static final int nlocal_feat = 10;

	public static final ArrayList< String > FEATURES = new ArrayList< String >( nlocal_feat );

	public static final HashMap< String, String > FEATURE_NAMES = new HashMap< String, String >( nlocal_feat );

	public static final HashMap< String, String > FEATURE_SHORT_NAMES = new HashMap< String, String >( nlocal_feat );

	public static final HashMap< String, Dimension > FEATURE_DIMENSIONS = new HashMap< String, Dimension >( nlocal_feat );

	public static final Map< String, Boolean > IS_INT = new HashMap< String, Boolean >( nlocal_feat );
	static
	{
		for ( int i = 0; i < nlocal_feat; i++ )
		{
			FEATURES.add( "MEAN_INTENSITY" + String.format( "%02d", i + 1 ) );
			FEATURE_NAMES.put( FEATURES.get( i ), "Mean intensity channel " + String.format( "%02d", i + 1 ) );
			FEATURE_SHORT_NAMES.put( FEATURES.get( i ), "Mean Ch" + String.format( "%02d", i + 1 ) );
			FEATURE_DIMENSIONS.put( FEATURES.get( i ), Dimension.INTENSITY );
			IS_INT.put( FEATURES.get( i ), Boolean.FALSE );
		}

	}

	/*
	 * METHODS
	 */

	@Override
	public SpotMultiChannelIntensityAnalyzer< T > getAnalyzer( final Model model, final ImgPlus< T > img, final int frame, final int channel )
	{

		final ImgPlus< T > imgT = HyperSliceImgPlus.fixTimeAxis( img, frame );

		// determine the number of channel
		final long[] dimensions = new long[ img.numDimensions() ];
		img.dimensions( dimensions );
		final int ch_dim = img.dimensionIndex( Axes.CHANNEL );
		int nCh = 1;
		if ( ch_dim >= 0 )
			nCh = ( int ) dimensions[ ch_dim ];

		return new SpotMultiChannelIntensityAnalyzer< T >( imgT, model, frame, nCh );
	}

	@Override
	public String getKey()
	{
		return KEY;
	}

	@Override
	public List< String > getFeatures()
	{
		return FEATURES;
	}

	@Override
	public Map< String, String > getFeatureShortNames()
	{
		return FEATURE_SHORT_NAMES;
	}

	@Override
	public Map< String, String > getFeatureNames()
	{
		return FEATURE_NAMES;
	}

	@Override
	public Map< String, Dimension > getFeatureDimensions()
	{
		return FEATURE_DIMENSIONS;
	}

	@Override
	public String getInfoText()
	{
		return null;
	}

	@Override
	public ImageIcon getIcon()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return KEY;
	}

	@Override
	public Map< String, Boolean > getIsIntFeature()
	{
		return IS_INT;
	}

	@Override
	public boolean isManualFeature()
	{
		return false;
	}

}
