package zhl.idimg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/** 
* @author zhuhl 
* @version 创建时间：2017年9月4日 上午10:10:16 
* @description 
*
*/

public class TaobaoIdImg {
	
	public static void main(String[] args) {
		
		try {
			for(int i=0; i<100; i++){
				URL url = new URL("https://pin.aliyun.com/get_img?sessionid=ALIaaa226c19ea05f16653aaef09d1374ac&identity=zhaoshang_sellermanager&r=1504490833727");
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.connect();
				InputStream in = conn.getInputStream();
				int temp = 0;
				File file = new File("C:\\Users\\Administrator\\Desktop\\验证码处理\\idimg"+i+"_.jpg");
				if(file.exists()){
					file.delete();
				}
				file.createNewFile();
				
				OutputStream out = new FileOutputStream(file);
				
				while((temp = in.read())!=-1){
					out.write(temp);
				}
				in.close();
				out.flush();
				out.close();
				Thread.sleep(1000);
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
