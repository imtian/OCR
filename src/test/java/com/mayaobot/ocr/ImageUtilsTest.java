package com.mayaobot.ocr;

import java.io.File;

import org.junit.Test;

public class ImageUtilsTest {

	@Test
	public void test() {
		String str = ImageUtilsTest.class.getResource("/src.jpg").getPath();
		File f = new File(str);     //原图片
		ImageUtils.fromFile(f)
		    .width(800)
		    .height(800)
		    .keepRatio(true)
		    .toFile(new File("H:/Temp/pdfimg/src.jpg"));
	}

}
