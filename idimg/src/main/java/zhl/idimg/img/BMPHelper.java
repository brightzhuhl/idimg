package zhl.idimg.img;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class BMPHelper {
	
	private static byte[] readBuffer = new byte[4]; 
	
	public BMP readAsBytes(String path){
		File file = new File(path);
		InputStream in= null;
		try {
			in = new FileInputStream(file);
			char idbyte1 = (char)in.read();
			char idbyte2 = (char)in.read();
			if('B'!=idbyte1 || 'M' != idbyte2){
				throw new IllegalArgumentException(path+"，不是正确的位图文件");
			}
			
			BMP bmp = new BMP();
			
			bmp.setBfSize(readNextInt(in));
			
			//跳过4字节的保留字节
			skipNextBytes(in, 4);
			
			bmp.setBfOffBits(readNextInt(in));
			
			bmp.setBiSize(readNextInt(in));
			
			bmp.setBiWidth(readNextInt(in));
			
			bmp.setBiHeight(readNextInt(in));
			
			//跳过planes字段
			skipNextBytes(in, 2);
			
			bmp.setBiBitCount(readNextShort(in));
			
			bmp.setBiCompression(readNextInt(in));
			
			bmp.setBiSizeImages(readNextInt(in));
			
			bmp.setBiXPelsPerMeter(readNextInt(in));
			
			bmp.setBiYPelsPerMeter(readNextInt(in));
			
			bmp.setBiClrUsed(readNextInt(in));
			
			bmp.setBiClrImportant(readNextInt(in));
			
			in.read(new byte[bmp.getBfOffBits()-54]);
			
			int height = bmp.getBiHeight(),
				width = bmp.getBiWidth();
					
			byte[][] result = new byte[height][];
			
			int i=0,j=height-1;
			byte[] temp = new byte[16];
			byte[] bt = new byte[width];
			loop1:while(in.read(temp)!=-1){
				loop2:for(byte b:temp){
					byte t1 = 1;
					for(int m=7; m>=0; m--){
						bt[i] = (byte)(b&(t1<<m));
						if(bt[i] !=0){
							bt[i] = 1;
						}
						i++;
						if(i==100){
							result[j] = bt;
							j--;
							if(j<0){
								break loop1;
							}
							bt = new byte[width];
							i = 0;
							break loop2;
						}
					}
				}
			}
			bmp.setData(result);
			return bmp;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	public int readNextInt(InputStream in) throws IOException{
		int count = in.read(readBuffer);
		if(count != 4){
			throw new RuntimeException("不能从流中读取int值");
		}
		int result = readBuffer[0] & 0x000000ff;
		result |= readBuffer[1]<<8 & 0x0000ffff;
		result |= readBuffer[2]<<16 & 0x00ffffff;
		result |= readBuffer[3]<<24;
		return result;
	}
	
	public byte[] asBytes(int i){
		byte[] bs = new byte[4];
		bs[0] = (byte) (i & 0x000000ff);
		bs[1] = (byte) ((i & 0x0000ff00)>>8);
		bs[2] = (byte) ((i & 0x00ff0000)>>16);
		bs[3] = (byte) ((i & 0xff000000)>>24);
		return bs;
	}
	
	public byte[] asBytes(short s){
		byte[] bs = new byte[2];
		bs[0] = (byte) (s & 0x00ff);
		bs[1] = (byte) (s & 0xff00);
		return bs;
	}
	public short readNextShort(InputStream in) throws IOException{
		short result = (short) in.read();
		result += (short)in.read()<<8;
		return result;
	}
	
	
	
	public void skipNextBytes(InputStream in,int n) throws IOException{
		for(int i=0; i<n; i++){
			in.read();
		}
	}
	
	public File generateBMP(byte[][] data,String loc){
		byte[] heads = new byte[62];
		heads[0] = 'B';
		heads[1] = 'M';
		
		int rowNum = data.length,colNum = data[0].length;
		
		int bits;
		if(colNum%8 != 0){
			bits = colNum/8 +1;
		}else{
			bits = colNum/8;
		}
		
		int rbits;
		
		if(bits%4 != 0){
			rbits = (bits/4+1)*4;
		}else{
			rbits = bits;
		}
		
		System.arraycopy(asBytes(rowNum*rbits+62), 0, heads, 2, 4);
		
		System.arraycopy(asBytes(62), 0, heads, 10, 4);
		
		System.arraycopy(asBytes(40), 0, heads, 14, 4);
		
		System.arraycopy(asBytes(colNum), 0, heads, 18, 4);
		
		System.arraycopy(asBytes(-rowNum), 0, heads, 22 , 4);
		
		System.arraycopy(asBytes((short)1), 0, heads, 26, 2);
		
		System.arraycopy(asBytes((short)1), 0, heads, 28, 2);
		
		System.arraycopy(asBytes(0), 0, heads, 30, 4);
		
		System.arraycopy(asBytes(rowNum*rbits), 0, heads, 34, 4);
		
		System.arraycopy(asBytes(3780), 0, heads, 38, 4);
		
		System.arraycopy(asBytes(3780), 0, heads, 42, 4);
		
		System.arraycopy(asBytes(0x00ffffff), 0, heads, 58, 4);
		
		byte[] imgData = new byte[rowNum*rbits];
		int index = 0;
		for(int i=0; i<rowNum; i++){
			int m = 7;
			byte t = 0;
			for(int j=0; j<rbits*8; j++){
				byte v ;
				if(j>=colNum){
					v = 0;
				}else{
					v = data[i][j];
					if(v!=0){
						v = 1;
					}
				}
				t |= v<<m;
				m--;
				if(m<0){
					imgData[index] = t;
					index ++;
					m = 7;
					t=0;
				}
			}
		}
		File file = new File(loc);
		if(file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
			OutputStream out = new FileOutputStream(file);
			out.write(heads);
			out.write(imgData);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	public static void main(String[] args) {
		BMPHelper helper = new BMPHelper();
		File basedir = new File("C:\\Users\\朱洪亮\\Desktop\\验证码识别");
		new FileFilter() {
			
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		File[] bmps = basedir.listFiles((file)->{
			if(file.getName().endsWith(".bmp")){
				return true;
			}
			return false;
		});
		for(File f:bmps){
			BMP bmp= helper.readAsBytes(f.getPath());
			Spliter spliter = new Spliter();
			//short[] pixelDist = spliter.getPixelDist(bmp.getData());
			
			List<byte[][]> result = spliter.splitWithCFS(bmp.getData());
			
			String baseN = f.getPath().replace(".bmp", "");
			
			for(int i=0; i<result.size(); i++){
				helper.generateBMP(result.get(i), baseN+i+".bmp");
			}
		}
		
	}
}
