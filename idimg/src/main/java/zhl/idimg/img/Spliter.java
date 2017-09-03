package zhl.idimg.img;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Spliter {
	
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
	
	public List<byte[][]> splitByPixelDist(byte[][] bmp,short[] pd){
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
	
	public List<byte[][]> splitWithCFS(byte[][] bmp){
		int rowNum = bmp.length,colNum = bmp[0].length;
		boolean[][] s = new boolean[rowNum][colNum];
		List<List<short[]>> cfses = new ArrayList<>();
		Queue<short[]> sq = new LinkedList<>();
		for(short i=0; i<rowNum; i++){
			for(short j=0; j<colNum; j++){
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
			}
		}
		
		List<byte[][]> result = new ArrayList<>();
		
		for(List<short[]> cfs:cfses){
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
