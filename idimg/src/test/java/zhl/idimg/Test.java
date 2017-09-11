package zhl.idimg;

public class Test {
	
	public static void main(String[] args) {
		/*int[] source = new int[]{1,2,3,4,5,6,8};
		int[] dest = new int[3];
		System.arraycopy(source, 5, dest, 1, 2);
		for(int i:dest){
			System.out.print(i+"\t");
		}*/
		byte b = (byte) 0xef;
		int test = b;
		System.out.println(b+","+test+":"+Integer.toBinaryString(test));
	}
}
