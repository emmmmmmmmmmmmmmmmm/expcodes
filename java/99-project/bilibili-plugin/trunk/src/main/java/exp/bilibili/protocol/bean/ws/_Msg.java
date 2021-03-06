package exp.bilibili.protocol.bean.ws;

import net.sf.json.JSONObject;
import exp.bilibili.protocol.envm.BiliCmd;

/**
 * <PRE>
 * websocket接收的json消息基类
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2018-01-31
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
abstract class _Msg {

	protected BiliCmd cmd;
	
	protected JSONObject json;
	
	protected _Msg(JSONObject json) {
		this.cmd = BiliCmd.UNKNOW;
		this.json = (json == null ? new JSONObject() : json);
		analyse(this.json);
	}

	protected abstract void analyse(JSONObject json);
	
	public BiliCmd getCmd() {
		return cmd;
	}

	public JSONObject getJson() {
		return json;
	}
	
}
