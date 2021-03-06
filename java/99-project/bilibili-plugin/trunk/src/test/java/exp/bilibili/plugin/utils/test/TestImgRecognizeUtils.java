package exp.bilibili.plugin.utils.test;

import java.io.File;

import exp.bilibili.plugin.utils.ImgRecognizeUtils;

public class TestImgRecognizeUtils {

	public static void main(String[] args) {
		File dir = new File("./src/test/resources/exp/bilibili/plugin/utils/test/math/cur");
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				continue;
			}

//			if (!file.getName().equals("img-1.jpeg")) {
//				continue;
//			}

			String imgPath = file.getAbsolutePath();
			String exp = ImgRecognizeUtils.analyseExpression(imgPath);
			System.out.println(file.getName() + ":" + exp);
		}
	}

}
