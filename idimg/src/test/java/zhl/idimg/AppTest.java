package zhl.idimg;

import java.io.File;
import java.util.List;

import org.junit.Test;

import zhl.idimg.img.BMP;
import zhl.idimg.img.BMPHelper;
import zhl.idimg.img.Spliter;

public class AppTest {
	
	
	public void split(){
		BMPHelper helper = new BMPHelper();
		File basedir = new File("C:\\Users\\朱洪亮\\Desktop\\验证码识别");
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
		File basedir = new File("C:\\Users\\朱洪亮\\Desktop\\验证码识别");
		BMPHelper helper = new BMPHelper();
		
		File[] jpgs = basedir.listFiles((file)->{
			if(file.getName().endsWith(".jpg")){
				return true;
			}
			return false;
		});
		for(File f:jpgs){
			byte[][] bmp = helper.binaryImg(f);
			helper.generateBMP(bmp, f.getPath().replace(".jpg", ".bmp"));
		}
	}
	
	@Test
	public void markCharNum(){
	}
}
