package zhl.idimg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zhl.idimg.img.BMP;
import zhl.idimg.img.BMPHelper;
import zhl.idimg.img.Spliter;
import zhl.idimg.uti.ImgSelector;

public class AppTest {
	
	
	public void split(){
		BMPHelper helper = new BMPHelper();
		File basedir = new File("C:\\Users\\Administrator\\Desktop\\验证码处理");
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
			if(bmp.getData() !=null){
				List<byte[][]> result = spliter.splitWithCFS(bmp.getData());
				String baseN = basedir.getPath()+"\\"+f.getName().replace(".bmp", "");
				for(int i=0; i<result.size(); i++){
					helper.generateBMP(result.get(i), baseN+i+".bmp");
				}
			}else{
				System.out.println(bmp);
			}
			
		}
	}
	
	public void binary(){
		File basedir = new File("C:\\Users\\Administrator\\Desktop\\验证码处理");
		BMPHelper helper = new BMPHelper();
		
		File[] jpgs = basedir.listFiles((file)->{
			if(file.getName().endsWith("_.jpg")){
				return true;
			}
			return false;
		});
		for(File f:jpgs){
			byte[][] bmp = helper.binaryImg(f);
			helper.generateBMP(bmp, f.getPath().replace(".jpg", ".bmp"));
		}
	}
	
	
	public void markCharNum(){
		int i = 0;
		ImgSelector selector = new ImgSelector();
		selector.setVisible(true);
		File[] files = selector.getSelectFiles();
		for(File f: files){
			File rename = new File(f.getPath().replace(".bmp", "_"+i+"cn.bmp"));
			f.renameTo(rename);
		}
	}
	
	@Test
	public void dropSplit(){
		BMPHelper helper = new BMPHelper();
		File basedir = new File("C:\\Users\\Administrator\\Desktop\\验证码处理");
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
			if(bmp.getData() !=null){
				List<byte[][]> subBmps = spliter.splitWithCFS(bmp.getData());
				List<byte[][]> result = new ArrayList<>();
				if(subBmps.size()<4){
					for(byte[][] subBmp:subBmps){
						int bw = subBmp[0].length;
						if(bw>23 && bw<37){
							result.addAll(spliter.spliteWithDropAL(subBmp, 2));
						}else if(bw>37){
							result.addAll(spliter.spliteWithDropAL(subBmp, 3));
						}else{
							result.add(subBmp);
						}
					}
				}else{
					//result = subBmps;
				}
				String baseN = basedir.getPath()+"\\"+f.getName().replace(".bmp", "");
				for(int i=0; i<result.size(); i++){
					helper.generateBMP(result.get(i), baseN+i+".bmp");
				}
			}else{
				System.out.println(bmp);
			}
		}
	}
}
