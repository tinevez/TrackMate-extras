package fiji.plugin.trackmate.extra.spotanalyzer;
import net.imagej.ImgPlus;
import net.imglib2.RandomAccess;
import net.imglib2.meta.view.HyperSliceImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import ij.IJ;

import java.util.Iterator;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.SpotCollection;
import fiji.plugin.trackmate.features.spot.SpotAnalyzer;
import fiji.plugin.trackmate.util.SpotNeighborhood;


public class HueSpotAnalyzer< T extends RealType< T >> implements SpotAnalyzer< T > {

	private final Model model;
	private final int frame;
	private final ImgPlus<T> img;
	private int NumChannel;
	private String errorMessage;
	public HueSpotAnalyzer(final Model model, final int frame, int NumChannel, final ImgPlus<T> img) {
		this.model = model;
		this.frame=frame;
		this.img = img;
		this.NumChannel=NumChannel;
		errorMessage="";
		
	}

	@Override
	public boolean checkInput() {
		if(NumChannel!=3){
			errorMessage="This featues only works with RGB-Stack images";
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public boolean process() {
		final SpotCollection sc = model.getSpots();
		Iterator< Spot > spotIt = sc.iterator( frame, false );
        while ( spotIt.hasNext() ) {
        	Spot spot = spotIt.next();
        	double[] rgb = new double[3];
        	for(int ch=0; ch<NumChannel; ch++)
        	{
        		ImgPlus<T> imgC = HyperSliceImgPlus.fixChannelAxis( img, ch );
        		SpotNeighborhood< T > neighborhood = new SpotNeighborhood< T >( spot, imgC );
        		
        		double mean = 0;
        		for ( T pixel : neighborhood ){
        			mean += pixel.getRealDouble();
        		}
        		mean /= neighborhood.size();
        		rgb[ch]=mean;
        	}
        	spot.putFeature(HueSpotAnalyzerFactory.MEAN_HUE, getHUE(rgb));
        }
		return true;
	}
	
	public double getHUE(double[] rgb){
    	rgb[0] *= 1f/255f;
    	rgb[1] *= 1f/255f;
    	rgb[2] *= 1f/255f;
    	double cMax = Math.max(Math.max(rgb[0],rgb[1]),rgb[2]);
    	double cMin = Math.min(Math.min(rgb[0],rgb[1]),rgb[2]);
    	double dc = cMax-cMin;
    	int v=0;
    	if(dc<0.00001){
    		v= 0;
    	}
    	else if(cMax==rgb[0]){
    		v= (int) (60f*((rgb[1]-rgb[2])/dc ));
    	}
    	else if(cMax==rgb[1]){
    		v = (int)(60*(2+(rgb[2]-rgb[0])/dc));
    	}
    	else if(cMax==rgb[2]){
    		v = (int)(60*(4+(rgb[0]-rgb[1])/dc));
    	}
    	if(v<0){
    		v+=360;
    	}
    	return v;
	}
	
	
	@Override
	public long getProcessingTime() {
		// TODO Auto-generated method stub
		return 0;
	}

}