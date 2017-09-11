package zhl.idimg.img;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Spliter {
	/**
	 * 
	 * @author zhuhl
	 * @date 2017年9月5日 下午3:26:23
	 * @description 
	 *	获取bmp图片的竖直投影直方图
	 * @param bmp bmp图片像素点数组
	 * @return 直方图一维数组
	 */
	public short[] getPixelDist(byte[][] bmp){
		int colnum = bmp[0].length;
		short[] pd = new short[bmp[0].length];
		for(int i=0; i<colnum; i++){
			short pixelNum = 0;
			for(int j=0; j<bmp.length; j++){
				if(bmp[j][i]==0){
					pixelNum++;
				}
			}
			pd[i] = pixelNum;
		}
		return pd;
	}
	
	/**
	 * 
	 * @author zhuhl
	 * @date 2017年9月5日 下午3:27:47
	 * @description 
	 *	根据直方图分割bmp图片
	 * @param bmp bmp图片像素点数组
	 * @return 分割后的bmp图片数组列表
	 */
	public List<byte[][]> splitByPixelDist(byte[][] bmp){
		short[] pd = getPixelDist(bmp);
		int rowNum = bmp.length;
		List<byte[][]> result = new ArrayList<byte[][]>();
		int left=-1;
		for(int i=0; i<pd.length; i++){
			short pixelNum = pd[i];
			if(pixelNum>1){
				continue;
			}
			if(left==-1){
				left = i;
			}else{
				if(i-left<=5){
					left = i;
					continue;
				}
				byte[][] subbmp = new byte[rowNum][];
				for(int s=0; s<rowNum; s++){
					subbmp[s] = Arrays.copyOfRange(bmp[s], left, i+1);
				}
				result.add(subbmp);
				left = i;
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @author zhuhl
	 * @date 2017年9月5日 下午3:29:15
	 * @description 
	 *	cfs算法分割bmp图片
	 * @param bmp bmp图片像素点数组
	 * @return 分割后的bmp图片数组列表
	 */
	public List<byte[][]> splitWithCFS(byte[][] bmp){
		int rowNum = bmp.length,colNum = bmp[0].length;
		boolean[][] s = new boolean[rowNum][colNum];
		List<List<short[]>> cfses = new ArrayList<>();
		Queue<short[]> sq = new LinkedList<>();
		for(short i=0; i<rowNum; i++){
			for(short j=0; j<colNum; j++){
				try {
					if(!s[i][j] && bmp[i][j]==0){
						List<short[]> cfs = new ArrayList<>();
						sq.add(new short[]{i,j});
						s[i][j] = true;
						while(!sq.isEmpty()){
							short[] p = sq.poll();
							cfs.add(p);
							
							short r = p[0],c = p[1];
							//右
							if(c+1<colNum){
								searchPoint(new short[]{r,(short)(c+1)},sq, bmp, s);
							}
							//右下
							if(c+1<colNum && r+1<rowNum){
								searchPoint(new short[]{(short)(r+1),(short)(c+1)},sq, bmp, s);
							}
							//右上
							if(c+1<colNum && r-1>=0){
								searchPoint(new short[]{(short)(r-1),(short)(c+1)},sq, bmp, s);
							}
							//左
							if(c-1>=0){
								searchPoint(new short[]{r,(short)(c-1)},sq, bmp, s);
							}
							//左上
							if(c-1>=0 && r-1>=0){
								searchPoint(new short[]{(short)(r-1),(short)(c-1)},sq, bmp, s);
							}
							//左下
							if(c-1>=0 && r+1<rowNum){
								searchPoint(new short[]{(short)(r+1),(short)(c-1)},sq, bmp, s);
							}
							//上
							if(r-1>=0){
								searchPoint(new short[]{(short)(r-1),c},sq, bmp, s);
							}
							//下
							if(r+1<rowNum){
								searchPoint(new short[]{(short)(r+1),c},sq, bmp, s);
							}
							
						}
						cfses.add(cfs);
					}				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		List<byte[][]> result = new ArrayList<>();
		
		for(List<short[]> cfs:cfses){
			if(cfs.size()<3){
				continue;
			}
			
			short mic=Short.MAX_VALUE,mir=Short.MAX_VALUE,mxc=0,mxr=0;
			for(short[] p:cfs){
				short r = p[0],c = p[1];
				if(r>mxr){
					mxr = r;
				}if(r<mir){
					mir = r;
				}if(c>mxc){
					mxc = c;
				}if(c<mic){
					mic = c;
				}
			}
			byte[][] clsBmp = createAndInit((short)30, (short)(mxc-mic+1), (byte)1);
			for(short[] p:cfs){
				short r = p[0],c = p[1];
				clsBmp[r][c-mic] = 0;
			}
			
			result.add(clsBmp);
		}
		
		return result;
	}
	
	public List<byte[][]> spliteWithDropAL(final byte[][] bmp,final int charNum){
		List<byte[][]> result = new ArrayList<>();
		int bw = bmp[0].length;
		//如果只有一个字符，不分割直接返回
		if(charNum <=1){
			result.add(bmp);
			return result;
		}
		
		//直方图数组
		short[] pixelDist = getPixelDist(bmp);
		//初始化参数，最小、最大字符宽度，bmp图片宽度
		//final int minWd = 11, maxWd = 18 ;
		
		//确定分割点范围
		final int srleft, sright,avCharW = bw/charNum+1;
		
		srleft = avCharW*3/4;
		sright = bw - avCharW*3/4;
		
		//计算最范围内像素最少的点为初始分割点
		short[] subPds = Arrays.copyOfRange(pixelDist, srleft-1, sright>bw?bw:sright);
		int[] index = new int[subPds.length];
		for(int i=0; i< index.length; i++){
			index[i] = i+srleft-1;
		}
		for(int i=0; i<subPds.length-1; i++){
			for(int j=i+1; j<subPds.length; j++){
				if(subPds[j]<subPds[i]){
					int temp = subPds[i];
					subPds[i] = subPds[j];
					subPds[j] = (short)temp;
					
					temp = index[i];
					index[i] = index[j];
					index[j] = temp;
				}
			}
		}
		//取得像素最少的点进行切割
		for(int i=0; i<1;i++){
			int startX = index[i];
			List<byte[][]> splited = processDropAL(bmp, startX);
			int leftCharNum =  new Double(Math.rint(startX*1.0/avCharW)).intValue();
			if(leftCharNum>1){
				result.addAll(spliteWithDropAL(splited.get(0), leftCharNum));
			}else{
				result.add(splited.get(0));
			}
			if( (charNum-leftCharNum) > 1 ){
				result.addAll(spliteWithDropAL(splited.get(1), charNum-leftCharNum));
			}else{
				result.add(splited.get(1));
			}
		}
		
		return result;
	}
	/**
	 * 
	 * @author zhuhl
	 * @date 2017年9月6日 上午10:06:05
	 * @description 
	 *	滴水算法实现
	 * @param bmp
	 * @param pointX 切割起始点x坐标
	 * @return
	 */
	private List<byte[][]> processDropAL(byte[][] bmp,int pointX){
		//HashSet<String> hasSearched = new HashSet<>();
		ArrayList<int[]> path = new ArrayList<>();
		int x = pointX,y = 0,bh = bmp.length,bw = bmp[0].length;
		//int lastX=0,lastY=0;
		int lastXn = 0;
		while(y < bh-1){
			
			int p1 = 5 * ((y+1<bh)&&(x-1>0) ? bmp[y+1][x-1] : 0);
			int p2 = 4 * ( y+1<bh ? bmp[y+1][x] : 0);
			int p3 = 3 * ( (y+1<bh)&&(x+1<bw) ? bmp[y+1][x+1] : 0);
			int p4 = 2 * ( x+1<bw ? bmp[y][x+1] : 0);
			int p5 = (x-1>0 ? bmp[y][x-1] : 0);
			
			int sumW = p1 + p2 + p3 + p4 + p5;
			int w = 0;
			if( (sumW == 0) || (sumW == 15) ){
				w = 4;
			}else{
				for(int i:new int[]{p1,p2,p3,p4,p5}){
					if(i > w){
						w = i;
					}
				}
			}
			int xn =0 ,yn = 0;
			if(w == 1){
				xn = -1;
			}else if(w == 2){
				xn = 1;
			}else if(w == 3){
				xn = 1;
				yn = 1;
			}else if(w == 4){
				yn = 1;
			}else if(w == 5){
				xn = -1;
				yn = 1;
			}
			
			
			if(x+xn>pointX+4 || x+xn<pointX-4){
				xn = 0;
				yn = 1;
			}
			if(lastXn==-xn && yn==0){
				xn = 0;
				yn = 1;
			}
			path.add(new int[]{x,y});

			if(path.size()>100){
				System.out.println();
			}
			x = x + xn;
			y = y + yn;
			
			lastXn = xn;
			/*if(hasSearched.contains(lastY+","+lastX)){
				for(int[] p:path){
					System.out.println(p[0]+"\t"+p[1]);
				}
				System.out.println(lastY+"\t"+lastX);
				throw new RuntimeException("hasSearched");
			}else{
				hasSearched.add(lastY+","+lastY);
			}*/
			
		}
		path.add(new int[]{path.get(path.size()-1)[0],bh-1});
		int mxWidth = 0,minWid=Integer.MAX_VALUE;
		HashMap<Integer, Integer> noRepeatPath = new HashMap<>();
		for(int[] point:path){
			int tempX = point[0],tempY = point[1];
			
			if(noRepeatPath.get(tempY)!=null){
				noRepeatPath.put(tempY, Integer.min(noRepeatPath.get(tempY), tempX));
			}else{
				noRepeatPath.put(tempY, tempX);
			}
			
			if(tempX > mxWidth){
				mxWidth = tempX;
			}
			if(tempX < minWid){
				minWid = tempX;
			}
		}
		
		byte[][] left = createAndInit((short)bh, (short)(mxWidth+1), (byte)1),right = createAndInit((short)bh, (short)(bw-minWid), (byte)1);
		
		for(int i=0; i<bh; i++){
			try {
			System.arraycopy(bmp[i],0, left[i], 0, noRepeatPath.get(i));
			for(int s=bw-1; s>=noRepeatPath.get(i); s--){
				
					right[i][s-minWid] = bmp[i][s];
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		List<byte[][]> splitedBmp = new ArrayList<>();
		splitedBmp.add(left);
		splitedBmp.add(right);
		return splitedBmp;
	}
	
	/**
	 * 
	 * @author zhuhl
	 * @date 2017年9月5日 下午3:29:54
	 * @description 
	 *	创建指定大小的二维数组并初始化
	 * @param dr length1
	 * @param dc length2
	 * @param init 初始化值
	 * @return
	 */
	public byte[][] createAndInit(short dr,short dc,byte init){
		byte[][] res = new byte[dr][];
		for(short i=0; i<dr; i++){
			res[i] = new byte[dc];
			Arrays.fill(res[i], init);
		}
		return  res;
	}
	
	private void searchPoint(short[] p,Queue<short[]> q,byte[][] bmp,boolean[][] flag){
		short r = p[0],c = p[1];
		if(!flag[r][c] && bmp[r][c]==0){
			q.add(new short[]{r,(short)(c)});
			flag[r][c] = true;
		}
	}
}
