package fiji.plugin.trackmate.extra.spotanalyzer;

import static fiji.plugin.trackmate.extra.spotanalyzer.SpotMultiChannelIntensityAnalyzerFactory.FEATURES;
//import ij.IJ;

import java.util.Iterator;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.features.spot.SpotAnalyzer;
import fiji.plugin.trackmate.util.SpotNeighborhood;
import net.imagej.ImgPlus;
import net.imglib2.meta.view.HyperSliceImgPlus;
import net.imglib2.type.numeric.RealType;

@SuppressWarnings( "deprecation" )
public class SpotMultiChannelIntensityAnalyzer< T extends RealType< T > > implements SpotAnalyzer< T >
{

	private final ImgPlus< T > img;

	private final Model model;

	private final int frame;

	private String errorMessage;

	private long processingTime;

	private final int NumChannel;

	public SpotMultiChannelIntensityAnalyzer( final ImgPlus< T > img, final Model model, final int frame, final int NumChannel )
	{
		this.img = img;
		this.model = model;
		this.frame = frame;
		this.NumChannel = NumChannel;
	}

	/*
	 * PUBLIC METHODS
	 */

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public boolean process()
	{

		for ( int ch = 0; ch < NumChannel; ch++ )
		{
			final ImgPlus< T > imgC = HyperSliceImgPlus.fixChannelAxis( img, ch );
			final Iterator< Spot > spots = model.getSpots().iterator( frame, false );

			while ( spots.hasNext() )
			{
				final Spot spot = spots.next();
				final SpotNeighborhood< T > neighborhood = new SpotNeighborhood< T >( spot, imgC );
				double mean = 0;

				for ( final T pixel : neighborhood )
					mean += pixel.getRealDouble();

				mean /= neighborhood.size();
				spot.putFeature( FEATURES.get( ch ), mean );
			}
		}

		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

}
