package com.mayaobot.ocr;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mayabot.utils.Base64;

public class BaiduOCR {
	private static String httpUrl = "http://apis.baidu.com/idl_baidu/baiduocrpay/idlocrpaid";
	private static String httpArg = "fromdevice=pc&version=v2&clientip=10.10.10.0&detecttype=LocateRecognize&languagetype=CHN_ENG&imagetype=1&image=";

	/**
	 * @param urlAll
	 *            :请求接口
	 * @param httpArg
	 *            :参数
	 * @return 返回结果
	 */
	public static String request(String imageBase64, String httpArg) {
	    BufferedReader reader = null;
	    String result = null;
	    StringBuffer sbf = new StringBuffer();
	    httpArg += URLEncoder.encode(imageBase64);

	    try {
	        URL url = new URL(httpUrl);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type",
	                        "application/x-www-form-urlencoded");
	        // 填入apikey到HTTP header
	        connection.setRequestProperty("apikey", "b6f013423c81ea1bd21433d45875566b");
	        connection.setDoOutput(true);
	        connection.getOutputStream().write(httpArg.getBytes("UTF-8"));
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        String strRead = null;
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	            sbf.append("/r/n");
	        }
	        reader.close();
	        result = sbf.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
//		List<byte[]> imageByteArrayList = 
//				PDFProcessor.extractImage(new File("H:/Temp/pdf/关于2014年度浙江政务服务网建设绩效考评情况的通报(19804).pdf"));
//		
//		for (byte[] byteArray : imageByteArrayList) {
//			System.out.println(request(Base64.encodeBytes(byteArray)));
////			System.out.println(Base64.encodeBytes(byteArray));
//			break;
//		}
		Gson gson = new Gson();
		
		File pdfDir = new File("H:\\Temp\\pdf");
		for (File dir : pdfDir.listFiles()) {
			if (dir.isDirectory()) {
				List<String> imgList = Lists.newArrayList(dir.list());
				for (File img : dir.listFiles()) {
					if (img.getName().endsWith(".txt")) {
						continue;
					} else {
						if (imgList.contains(img.getName() + ".txt")) {
							continue;
						}
					}
					BufferedImage bi = ImageIO.read(img);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ImageIO.write(bi, "jpg", out);
					String imgBase64 = Base64.encodeBytes(out.toByteArray());
					String httpArgs = "";
					if (imgBase64.length() > 300*1024) {
						httpArgs = "fromdevice=pc&version=v2&clientip=10.10.10.0&detecttype=LocateRecognize&languagetype=CHN_ENG&imagetype=1&sizetype=big&image=";
					} else {
						httpArgs = "fromdevice=pc&version=v2&clientip=10.10.10.0&detecttype=LocateRecognize&languagetype=CHN_ENG&imagetype=1&image=";
					}
					String result = request(imgBase64, httpArgs);
					result = result.replace("/r", "").replace("/n", "");
					Map json = null;
					try{
						json = gson.fromJson(result, Map.class);
					} catch(Exception e) {
						System.out.println(result);
						continue;
					}
					if (json.get("errMsg").equals("success")) {
						StringBuffer sb = new StringBuffer();
						for (Map segment : (List<Map>)json.get("retData")) {
							sb.append(segment.get("word"));
							sb.append("\n");
						}
						
						FileWriter fw = new FileWriter(img.getPath() + ".txt");
						fw.write(sb.toString());
						fw.close();
					} else {
						System.out.println(result);
					}
					
					Thread.sleep(1000);
				}
				
				System.out.println(dir.getName() + "  over.");
			}
		}
		
//		File img = new File(BaiduOCR.class.getResource("/src.jpg").getPath());
//		BufferedImage bi = ImageIO.read(img);
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		ImageIO.write(bi, "jpg", out);
//		System.out.println(request(Base64.encodeBytes(out.toByteArray())));
	}
}
