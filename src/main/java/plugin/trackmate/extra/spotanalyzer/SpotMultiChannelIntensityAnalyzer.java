package plugin.trackmate.extra.spotanalyzer;


import static plugin.trackmate.extra.spotanalyzer.SpotMultiChannelIntensityAnalyzerFactory.FEATURES;
//import ij.IJ;

import java.util.Iterator;

import net.imagej.ImgPlus;
import net.imglib2.type.numeric.RealType;
import net.imglib2.meta.view.HyperSliceImgPlus;
//import net.imglib2.util.Util;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.util.SpotNeighborhood;
import fiji.plugin.trackmate.features.spot.SpotAnalyzer;
//import fiji.plugin.trackmate.features.spot.SpotAnalyzerFactory;


public class SpotMultiChannelIntensityAnalyzer< T extends RealType< T >> implements SpotAnalyzer< T >
{

	private ImgPlus< T > img;
	private Model model;
	private int frame;
	private String errorMessage;
	private long processingTime;
	private int NumChannel;
	
	public SpotMultiChannelIntensityAnalyzer( ImgPlus< T > img, final Model model, int frame, int NumChannel )
	{
		this.img  = img;
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
		
		for(int ch=0; ch<NumChannel; ch++)
		{
			ImgPlus<T> imgC = HyperSliceImgPlus.fixChannelAxis( img, ch );
			Iterator<Spot> spots = model.getSpots().iterator(frame, false);
			
			while(spots.hasNext() )
			{
				Spot spot = spots.next();
				SpotNeighborhood< T > neighborhood = new SpotNeighborhood< T >( spot, imgC );
				double mean = 0;
				
				for ( T pixel : neighborhood )
					mean += pixel.getRealDouble();
				
				mean /= neighborhood.size();
				spot.putFeature( FEATURES.get(ch), mean );
			}
		}

		
		//IJ.log("all ok t=" + frame);
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
