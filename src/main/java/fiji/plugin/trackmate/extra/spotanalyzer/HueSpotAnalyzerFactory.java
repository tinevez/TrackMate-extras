package fiji.plugin.trackmate.extra.spotanalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imglib2.meta.view.HyperSliceImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;

import fiji.plugin.trackmate.Dimension;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.features.spot.SpotAnalyzer;
import fiji.plugin.trackmate.features.spot.SpotAnalyzerFactory;

/**
 * 
 * @author Thorsten Wagner (wagner@biomedical-imaging.de)
 *
 * Spot analyzer which estimates the mean color of a spot
 * The class is based on 
 * https://github.com/tinevez/TrackMate-extras/blob/master/src/main/java/fiji/plugin/trackmate/extra/spotanalyzer/SpotMultiChannelIntensityAnalyzerFactory.java
 * @param <T>
 */
/*
 * 
 
 */

@Plugin( type = SpotAnalyzerFactory.class, priority = 1d )
public class HueSpotAnalyzerFactory< T extends RealType< T > & NativeType< T >> implements SpotAnalyzerFactory< T >{
	
	private static final String KEY = "MEAN_HUE";
	public static final String MEAN_HUE = "MEAN_HUE";
	public static final List< String > FEATURES = new ArrayList< String >( 1 );
	private static final Map< String, Boolean > IS_INT = new HashMap< String, Boolean >( 1 );
	public static final Map< String, String > FEATURE_NAMES = new HashMap< String, String >( 1 );
	public static final Map< String, String > FEATURE_SHORT_NAMES = new HashMap< String, String >( 1 );
	public static final Map< String, Dimension > FEATURE_DIMENSIONS = new HashMap< String, Dimension >( 1 );
	
	private static final String NAME = "Mean Hue (RGB-Stack-Images)";
	
	static
	{
		FEATURES.add( MEAN_HUE );
		IS_INT.put( MEAN_HUE, true );
		FEATURE_SHORT_NAMES.put( MEAN_HUE, "Color" );
		FEATURE_NAMES.put( MEAN_HUE, "Mean Color" );
		FEATURE_DIMENSIONS.put( MEAN_HUE, Dimension.NONE);
	}
	
	@Override
	public List<String> getFeatures() {
		return FEATURES;
	}

	@Override
	public Map<String, String> getFeatureShortNames() {
		return FEATURE_SHORT_NAMES;
	}

	@Override
	public Map<String, String> getFeatureNames() {
		return FEATURE_NAMES;
	}

	@Override
	public Map<String, Dimension> getFeatureDimensions() {
		return FEATURE_DIMENSIONS;
	}

	@Override
	public Map<String, Boolean> getIsIntFeature() {
		return Collections.unmodifiableMap( IS_INT );
	}

	@Override
	public boolean isManualFeature() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInfoText() {
		return "";
	}

	@Override
	public ImageIcon getIcon() {
		return null;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName() {
		return NAME;
	}


	@Override
	public SpotAnalyzer<T> getAnalyzer(Model model, ImgPlus<T> img, int frame,
			int channel) {
		final ImgPlus< T > imgT = HyperSliceImgPlus.fixTimeAxis( img, frame );
		// determine the number of channel
		long[] dimensions = new long[ img.numDimensions() ];
		img.dimensions(dimensions);

		int ch_dim = img.dimensionIndex((AxisType)Axes.CHANNEL);
		int nCh=1;
		if (ch_dim>=0)
			nCh = (int)dimensions[ch_dim];
		return new HueSpotAnalyzer< T >( model, frame, nCh, img );
	}

}