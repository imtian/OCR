package com.mayaobot.ocr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

// http://blog.jdk5.com/zh/java-resize-and-compress-images/ 等比缩放图片

public class PDFProcessor {
	
	public static List<byte[]> extractImage(File pdf) throws IOException {
		PDDocument doc = PDDocument.load(pdf);
		PDPageTree pages = doc.getDocumentCatalog().getPages();
		List<byte[]> imageByteArrayList = new ArrayList<byte[]>();
		ImageUtils util = new ImageUtils();
	    File dir = new File(pdf.getAbsolutePath().substring(0, pdf.getAbsolutePath().lastIndexOf(".")));
	    dir.mkdir();
		for (PDPage page : pages) {
			PDResources res = page.getResources();
			Iterator<COSName> itr = res.getXObjectNames().iterator();
			while(itr.hasNext()) {
				COSName name = itr.next();
				PDImageXObject img = (PDImageXObject) res.getXObject(name);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ImageIO.write(img.getImage(), "jpg", new File(dir.getAbsolutePath() + "/" + name.getName() + ".jpg"));
//				ImageIO.write(img.getImage(), "jpg", out);
//				imageByteArrayList.add(out.toByteArray());
			}
		}
		
		return imageByteArrayList;
	}

	public static void main(String[] args) throws IOException {
		File dir = new File("H:/Temp/pdf/");
		for (File file : dir.listFiles()) {
			try{
				List<byte[]> imageByteArrayList = extractImage(file);
			} catch (IllegalArgumentException e) {
				
			}
		}
		
		List<byte[]> imageByteArrayList = extractImage(new File("H:/Temp/pdf/关于举办浙江政务服务网建设工作培训的通知(35018).pdf"));
		
//		for (byte[] byteArray : imageByteArrayList) {
////			System.out.println(Base64.encodeBytes(byteArray));
//		}
	}

}
