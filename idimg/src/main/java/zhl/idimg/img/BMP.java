package zhl.idimg.img;

public class BMP {
	
	
	/**
	 * 位图文件头字节数
	 */
	private final static int fileHeadBits = 14;
	
	//文件头开始
	/**
	 * 位图信息头字节数
	 */
	
	private final static int bmpInfoHeadBits = 40;
	/**
	 * 位图文件格式标识  2byte 只能为"BM"
	 */
	private final static String bfType = "BM";
	
	/**
	 * 位图文件字节数 4byte  
	 */
	private int bfSize;
	
	/**
	 * 位图数据位偏移数 4byte
	 */
	private int bfOffBits;
	
	//信息头开始
	
	/**
	 * 位图信息头字节数
	 */
	private int biSize;
	
	/**
	 * 4字节，以像素为单位说明图像的宽度；
	 */
	private int biWidth;
	
	/**
	 * 4字节，以像素为单位说明图像的高度，同时如果为正，说明位图倒立（即数据表示从图像的左下角到右上角），如果为负说明正向；
	 */
	private int biHeight;
	
	/**
	 * 2字节，为目标设备说明颜色平面数，总被设置为1；
	 */
	private static final short biPlanes = 1;
	
	/**
	 * 2字节，说明比特数/像素数，值有1、2、4、8、16、24、32；
	 */
	private short biBitCount;
	
	/**
	 * 说明图像的压缩类型，最常用的就是0（BI_RGB），表示不压缩；
	 */
	private int biCompression;
	
	/**
	 * 4字节，说明位图数据的大小，当用BI_RGB格式时，可以设置为0；
	 */
	private int biSizeImages;
	
	/**
	 * 表示水平分辨率，单位是像素/米，有符号整数；
	 */
	private int biXPelsPerMeter;
	
	/**
	 * 表示垂直分辨率，单位是像素/米，有符号整数；
	 */
	private int biYPelsPerMeter;
	
	/**
	 * 说明位图使用的调色板中的颜色索引数，为0说明使用所有；
	 */
	private int biClrUsed;
	
	/**
	 * 说明对图像显示有重要影响的颜色索引数，为0说明都重要；
	 */
	private int biClrImportant;
	
	/**
	 * 位图数据,每位表示一个像素，二维数组的行数和列数与位图的行数列数相同
	 */
	private byte[][] data;

	
	
	
	public int getBfSize() {
		return bfSize;
	}

	public void setBfSize(int bfSize) {
		this.bfSize = bfSize;
	}

	public int getBfOffBits() {
		return bfOffBits;
	}

	public void setBfOffBits(int bfOffBits) {
		this.bfOffBits = bfOffBits;
	}

	public int getBiSize() {
		return biSize;
	}

	public void setBiSize(int biSize) {
		this.biSize = biSize;
	}

	public int getBiWidth() {
		return biWidth;
	}

	public void setBiWidth(int biWidth) {
		this.biWidth = biWidth;
	}

	public int getBiHeight() {
		return biHeight;
	}

	public void setBiHeight(int biHeight) {
		this.biHeight = biHeight;
	}

	public short getBiBitCount() {
		return biBitCount;
	}

	public void setBiBitCount(short biBitCount) {
		this.biBitCount = biBitCount;
	}

	public int getBiCompression() {
		return biCompression;
	}

	public void setBiCompression(int biCompression) {
		this.biCompression = biCompression;
	}

	public int getBiSizeImages() {
		return biSizeImages;
	}

	public void setBiSizeImages(int biSizeImages) {
		this.biSizeImages = biSizeImages;
	}

	public int getBiXPelsPerMeter() {
		return biXPelsPerMeter;
	}

	public void setBiXPelsPerMeter(int biXPelsPerMeter) {
		this.biXPelsPerMeter = biXPelsPerMeter;
	}

	public int getBiYPelsPerMeter() {
		return biYPelsPerMeter;
	}

	public void setBiYPelsPerMeter(int biYPelsPerMeter) {
		this.biYPelsPerMeter = biYPelsPerMeter;
	}

	public int getBiClrUsed() {
		return biClrUsed;
	}

	public void setBiClrUsed(int biClrUsed) {
		this.biClrUsed = biClrUsed;
	}

	public int getBiClrImportant() {
		return biClrImportant;
	}

	public void setBiClrImportant(int biClrImportant) {
		this.biClrImportant = biClrImportant;
	}

	public byte[][] getData() {
		return data;
	}

	public void setData(byte[][] data) {
		this.data = data;
	}

	public static int getFileheadbits() {
		return fileHeadBits;
	}

	public static int getBmpinfoheadbits() {
		return bmpInfoHeadBits;
	}

	public static String getBftype() {
		return bfType;
	}

	public static short getBiplanes() {
		return biPlanes;
	}
	
	
	
}
