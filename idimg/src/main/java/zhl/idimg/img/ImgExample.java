package zhl.idimg.img;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImgExample {
	
	private static final int formatWd = 27;
	
	private char label;
	
	private List<byte[]> imgVectors;
	
	public static class IllegalBMPWidthException extends RuntimeException{
		
		private static final long serialVersionUID = 1L;
		
		public IllegalBMPWidthException(String msg) {
			super(msg);
		}
	}
	
	public static byte[] formatTrainImg(byte[][] bmp){
		byte[] vct  = new byte[(bmp.length/3)*(formatWd/3)];
		if(bmp[0].length>formatWd){
			throw new IllegalBMPWidthException("bmp宽度不能大于"+formatWd);
		}
		byte interval = (byte)(formatWd/2 - bmp[0].length/2);
		byte[][] formatBmp = new byte[bmp.length][];
		for(int i=0; i<bmp.length; i++){
			formatBmp[i] = new byte[formatWd];
			Arrays.fill(formatBmp[i], (byte)1);
			System.arraycopy(bmp[i], 0, formatBmp[i], interval, bmp[i].length);
		}
		int index = 0;
		for(int i=0; i<=formatBmp.length-3; i=i+3){
			
			for(int j=0; j<=formatBmp[i].length-3; j=j+3){
				int count = 0;
				for(int r =i; r<i+3; r++){
					for(int c=j; c<j+3; c++){
						count += (formatBmp[r][c]==0 ? 1 : 0);
					}
				}
				vct[index] = (byte)count;
				index++;
			}
		}
		return vct;
	}
	
	public ImgExample(char label,List<byte[][]> bmps){
		this.label = label;
		imgVectors = new ArrayList<>();
		init(bmps);
	}
	
	private void init(List<byte[][]> bmps){
		for(byte[][] bmp:bmps){
			imgVectors.add(formatTrainImg(bmp));
		}
	}

	public char getLabel() {
		return label;
	}

	public void setLabel(char label) {
		this.label = label;
	}

	public List<byte[]> getImgVectors() {
		return imgVectors;
	}

	public void setImgVectors(List<byte[]> imgVectors) {
		this.imgVectors = imgVectors;
	}
	
	
}
