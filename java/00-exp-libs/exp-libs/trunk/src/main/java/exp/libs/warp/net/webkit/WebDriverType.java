package exp.libs.warp.net.webkit;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * Web驱动类型
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebDriverType {

	/** WEB驱动包目录 */
	private final static String DRIVER_DIR = "./lib/driver/";
	
	/** HTML默认浏览器 */
	private final static String HtmlUnit = "HtmlUnit";
	public final static WebDriverType HTMLUTIL = new WebDriverType(HtmlUnit);
	
	/** PhantomJS无头浏览器 */
	private final static String PhantomJS = "phantomjs-driver.exe";
	public final static WebDriverType PHANTOMJS = new WebDriverType(PhantomJS);
	
	/** Chrome浏览器 */
	private final static String Chrome = "chrome-driver.exe";
	public final static WebDriverType CHROME = new WebDriverType(Chrome);
	
	/** 驱动名 */
	private String driverName;
	
	/** 驱动路径 */
	private String driverPath;
	
	/**
	 * 构造函数
	 * @param driverName 驱动名
	 */
	private WebDriverType(String driverName) {
		this.driverName = driverName;
		this.driverPath = StrUtils.concat(DRIVER_DIR(), driverName);
	}
	
	/**
	 * WEB驱动包目录 (可重载)
	 * @return
	 */
	protected String DRIVER_DIR() {
		return DRIVER_DIR;
	}
	
	/**
	 * 驱动名
	 * @return
	 */
	public String DRIVER_NAME() {
		return driverName;
	}
	
	/**
	 * 驱动路径
	 * @return
	 */
	public String DRIVER_PATH() {
		return driverPath;
	}
	
}
