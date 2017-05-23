package fiji.plugin.trackmate.extra.trackanalyzer;

import static fiji.plugin.trackmate.extra.spotanalyzer.SpotMultiChannelIntensityAnalyzerFactory.nlocal_feat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import fiji.plugin.trackmate.Dimension;
import fiji.plugin.trackmate.FeatureModel;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackMatePlugIn_;
import fiji.plugin.trackmate.extra.spotanalyzer.SpotMultiChannelIntensityAnalyzerFactory;
import fiji.plugin.trackmate.features.track.TrackAnalyzer;
import ij.ImageJ;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.multithreading.SimpleMultiThreading;

@SuppressWarnings( "deprecation" )
@Plugin( type = TrackAnalyzer.class )
public class TrackMeanIntensityAnalyzer implements TrackAnalyzer, Benchmark
{

	/*
	 * CONSTANTS
	 */
	public static final String KEY = "TRACK_MEAN_INTENSITY";

	public static final ArrayList< String > FEATURES = new ArrayList< String >( nlocal_feat );

	public static final HashMap< String, String > FEATURE_NAMES = new HashMap< String, String >( nlocal_feat );

	public static final HashMap< String, String > FEATURE_SHORT_NAMES = new HashMap< String, String >( nlocal_feat );

	public static final HashMap< String, Dimension > FEATURE_DIMENSIONS = new HashMap< String, Dimension >( nlocal_feat );

	public static final Map< String, Boolean > IS_INT = new HashMap< String, Boolean >( nlocal_feat );
	static
	{
		for ( int i = 0; i < nlocal_feat; i++ )
		{
			FEATURES.add( "MEAN_TRACK_INTENSITY" + String.format( "%02d", i + 1 ) );
			FEATURE_NAMES.put( FEATURES.get( i ), "Mean track intensity channel " + String.format( "%02d", i + 1 ) );
			FEATURE_SHORT_NAMES.put( FEATURES.get( i ), "Mean track Ch" + String.format( "%02d", i + 1 ) );
			FEATURE_DIMENSIONS.put( FEATURES.get( i ), Dimension.INTENSITY );
			IS_INT.put( FEATURES.get( i ), Boolean.FALSE );
		}
	}

	private int numThreads;

	private long processingTime;

	public TrackMeanIntensityAnalyzer()
	{
		setNumThreads();
	}

	/*
	 * METHODS
	 */

	@Override
	public boolean isLocal()
	{
		return true;
	}

	@Override
	public void process( final Collection< Integer > trackIDs, final Model model )
	{
		if ( trackIDs.isEmpty() ) { return; }
		final FeatureModel fm = model.getFeatureModel();

		final ArrayBlockingQueue< Integer > queue = new ArrayBlockingQueue< Integer >( trackIDs.size(), false, trackIDs );

		final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		for ( int i = 0; i < threads.length; i++ )
		{
			threads[ i ] = new Thread( "TrackMeanIntensityAnalyzer thread " + i )
			{
				@Override
				public void run()
				{
					Integer trackID;
					while ( ( trackID = queue.poll() ) != null )
					{
						final Set< Spot > track = model.getTrackModel().trackSpots( trackID );

						for ( int c = 0; c < nlocal_feat; c++ )
						{
							final String intensityFeature = SpotMultiChannelIntensityAnalyzerFactory.FEATURES.get( c );
							double sum = 0.;
							int n = 0;
							for ( final Spot spot : track )
							{
								final Double feature = spot.getFeature( intensityFeature );
								if ( null == feature )
									continue;

								final double val = feature.doubleValue();
								n++;
								sum += val;
							}
							final double mean = sum / n;
							if ( n != 0 )
								fm.putTrackFeature( trackID, FEATURES.get( c ), Double.valueOf( mean ) );
							else
								fm.putTrackFeature( trackID, FEATURES.get( c ), null );
						}
					}
				}
			};
		}

		final long start = System.currentTimeMillis();
		SimpleMultiThreading.startAndJoin( threads );
		final long end = System.currentTimeMillis();
		processingTime = end - start;
	}

	@Override
	public int getNumThreads()
	{
		return numThreads;
	}

	@Override
	public void setNumThreads()
	{
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void setNumThreads( final int numThreads )
	{
		this.numThreads = numThreads;

	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	};

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

	public static void main( final String[] args )
	{
		ImageJ.main( args );
		final String imageFile = "/Users/tinevez/Desktop/DUP_TIM1 GFP clat dsred TIRF-5.tif";
//		IJ.open( imageFile );

		final TrackMatePlugIn_ pg = new TrackMatePlugIn_();
		pg.run( imageFile );

	}
}
