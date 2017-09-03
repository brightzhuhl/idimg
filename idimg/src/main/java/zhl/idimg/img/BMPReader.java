package zhl.idimg.img;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BMPReader {
	
	public byte[][] readAsBytes(String path){
		File file = new File(path);
		InputStream in= null;
		try {
			in = new FileInputStream(file);
			char idbyte1 = (char)in.read();
			char idbyte2 = (char)in.read();
			int size = in.read();
			size += in.read()<<8;
			size += in.read()<<16;
			size += in.read()<<24;
			in.read(new byte[4]);
			int offset = in.read();
			offset += in.read()<<8;
			offset += in.read()<<16;
			offset += in.read()<<24;
			
			int imgInfoSize = in.read();
			imgInfoSize += in.read()<<8;
			imgInfoSize += in.read()<<16;
			imgInfoSize += in.read()<<24;
			
			int width = in.read();
			width += in.read()<<8;
			width += in.read()<<16;
			width += in.read()<<24;
			
			int height = in.read();
			height += in.read()<<8;
			height += in.read()<<16;
			height += in.read()<<24;
			
			in.read(new byte[2]);
			short depth = (short) in.read();
			depth += (short)in.read()<<8;
			
			in.read(new byte[offset-30]);
			
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
			
			System.out.println("id:"+idbyte1+idbyte2);
			System.out.println("size:"+size);
			System.out.println("offset:"+offset);
			System.out.println("imgInfoSize:"+imgInfoSize);
			System.out.println("width:"+width);
			System.out.println("height:"+height);
			System.out.println("depth:"+depth);
			in.close();
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		BMPReader reader = new BMPReader();
		byte[][] result = reader.readAsBytes("C:\\Users\\朱洪亮\\Desktop\\验证码识别\\mgru.bmp");
		for(int i=0; i<result.length; i++){
			for(int j=0; j<result[i].length; j++){
				System.out.print(result[i][j]+"\t");
			}
			System.out.println();
		}
	}
}
