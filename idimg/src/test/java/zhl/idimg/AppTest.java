package zhl.idimg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import zhl.idimg.img.BMP;
import zhl.idimg.img.BMPHelper;
import zhl.idimg.img.ImgExample;
import zhl.idimg.img.ImgExample.IllegalBMPWidthException;
import zhl.idimg.img.SVMRecognizer;
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
						}else if(bw>37 && bw<=50){
							result.addAll(spliter.spliteWithDropAL(subBmp, 3));
						}else if(bw>50){
							result.addAll(spliter.spliteWithDropAL(subBmp, 4));
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
	
	@Test
	public void recognize(){
		BMPHelper helper = new BMPHelper();
		File basedir = new File("C:\\Users\\Administrator\\Desktop\\验证码处理\\examples\\trainingset");
		File[] bmps = basedir.listFiles((file)->{
			if(file.getName().endsWith(".bmp")){
				return true;
			}
			return false;
		});
		HashMap<Character,List<byte[][]>> trainMap = new HashMap<>();
		for(File f:bmps){
			BMP bmp= helper.readAsBytes(f.getPath());
			byte[][] bmpArray = bmp.getData();
			for(int i=0; i<bmpArray.length; i++){
				for(int j=0; j<bmpArray[0].length; j++){
					System.out.print(bmpArray[i][j]+"\t");
				}
				System.out.println();
			}
			System.out.println("next");
			Character tag = f.getName().substring(0, 1).toCharArray()[0];
			List<byte[][]> bmpArrays = trainMap.get(tag);
			if(bmpArrays == null){
				bmpArrays = new ArrayList<>();
				trainMap.put(tag, bmpArrays);
			}
			bmpArrays.add(bmp.getData());
		}
		
		List<ImgExample> examples = new ArrayList<>();
		for(char key:trainMap.keySet()){
			ImgExample example = new ImgExample(key, trainMap.get(key));
			examples.add(example);
		}
		
		SVMRecognizer recognizer = new SVMRecognizer(examples);
		File testDir = new File("C:\\Users\\Administrator\\Desktop\\验证码处理\\examples\\testset");
		File[] testBmps = testDir.listFiles((file)->{
			if(file.getName().endsWith(".bmp")){
				return true;
			}
			return false;
		});
		for(File f:testBmps){
			String name = f.getName();
			String tag = name.substring(0,name.lastIndexOf(".")-1);
			BMP testBmp = helper.readAsBytes(f.getPath());
			byte[] svmData = null;
			try {
				svmData = ImgExample.formatTrainImg(testBmp.getData());
			} catch (IllegalBMPWidthException e) {
				e.printStackTrace();
				continue;
			}
			System.out.println(tag+":"+recognizer.predict(svmData));
		}
	}
}
