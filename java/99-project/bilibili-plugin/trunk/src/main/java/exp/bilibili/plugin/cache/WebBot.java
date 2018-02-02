package exp.bilibili.plugin.cache;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.bean.ldm.BiliCookie;
import exp.bilibili.plugin.envm.LotteryType;
import exp.bilibili.plugin.utils.UIUtils;
import exp.bilibili.protocol.XHRSender;
import exp.bilibili.protocol.bean.other.LotteryRoom;
import exp.bilibili.protocol.xhr.DailyTasks;
import exp.libs.utils.num.NumUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * Web行为模拟器（仿真机器人）
 * 
 * 	主要功能:
 *   1.全平台礼物抽奖管理器（小电视/高能礼物/节奏风暴）
 *   2.日常任务(签到/友爱社/小学数学)
 *   3.自动投喂
 *   4.打印版权信息
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class WebBot extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(WebBot.class);
	
	private final static long DAY_UNIT = 86400000L;
	
	private final static long HOUR_UNIT = 3600000L;
	
	private final static int HOUR_OFFSET = 8;
	
	/** 轮询间隔 */
	private final static long LOOP_TIME = 1000L;
	
	/** 定时触发事件的间隔 */
	private final static long EVENT_TIME = 3600000L;
	
	/** 定时触发事件的周期 */
	private final static int EVENT_LIMIT = (int) (EVENT_TIME / LOOP_TIME);
	
	/** 轮询次数 */
	private int loopCnt;
	
	/** 已完成当天任务的cookies */
	private Set<BiliCookie> finCookies;
	
	/** 最近一次添加过cookie的时间点 */
	private long lastAddCookieTime;
	
	/** 执行下次日常任务的时间点 */
	private long nextTaskTime;
	
	/** 上次重置每日任务的时间点 */
	private long resetTaskTime;
	
	/** 单例 */
	private static volatile WebBot instance;
	
	/**
	 * 构造函数
	 */
	private WebBot() {
		super("Web行为模拟器");
		this.loopCnt = 0;
		this.finCookies = new HashSet<BiliCookie>();
		this.lastAddCookieTime = System.currentTimeMillis();
		this.nextTaskTime = System.currentTimeMillis();
		initResetTaskTime();
	}
	
	/**
	 * 把上次任务重置时间初始化为当天0点
	 */
	private void initResetTaskTime() {
		resetTaskTime = System.currentTimeMillis() / DAY_UNIT * DAY_UNIT;
		resetTaskTime -= HOUR_UNIT * HOUR_OFFSET;
		resetTaskTime += 300000;	// 避免临界点时差, 后延5分钟
	}
	
	/**
	 * 获取单例
	 * @return
	 */
	public static WebBot getInstn() {
		if(instance == null) {
			synchronized (WebBot.class) {
				if(instance == null) {
					instance = new WebBot();
				}
			}
		}
		return instance;
	}

	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
	}

	@Override
	protected void _loopRun() {
		try {
			toDo();
		} catch(Exception e) {
			log.error("模拟Web行为异常", e);
		}
		_sleep(LOOP_TIME);
	}

	@Override
	protected void _after() {
		finCookies.clear();
		log.info("{} 已停止", getName());
	}
	
	private void toDo() {
		
		// 优先参与直播间抽奖
		LotteryRoom room = RoomMgr.getInstn().getGiftRoom();
		if(room != null) {
			toLottery(room);
			
		// 无抽奖操作则做其他事情
		} else {
			doDailyTasks();	// 执行每日任务
			doEvent();		// 定时触发事件
		}
	}
	
	/**
	 * 通过后端注入服务器参与抽奖
	 * @param room
	 */
	private void toLottery(LotteryRoom room) {
		final int roomId = room.getRoomId();
		final String raffleId = room.getRaffleId();
		
		// 小电视抽奖
		if(room.TYPE() == LotteryType.TV) {
			XHRSender.toTvLottery(roomId, raffleId);
			
		// 节奏风暴抽奖
		} else if(room.TYPE() == LotteryType.STORM) {
			XHRSender.toStormLottery(roomId, raffleId);
			
		// 高能抽奖
		} else {
			XHRSender.toEgLottery(roomId);
		}
	}
	
	/**
	 * 执行每日任务
	 */
	private void doDailyTasks() {
		resetDailyTasks();	// 满足某个条件则重置每日任务
		
		if(nextTaskTime > 0 && nextTaskTime <= System.currentTimeMillis()) {
			Set<BiliCookie> cookies = CookiesMgr.INSTN().ALL();
			for(BiliCookie cookie : cookies) {
				if(finCookies.contains(cookie)) {
					continue;
				}
				
				long max = -1;
				max = NumUtils.max(DailyTasks.toSign(cookie), max);
				max = NumUtils.max(DailyTasks.toAssn(cookie), max);
				max = NumUtils.max(DailyTasks.doMathTask(cookie), max);
				nextTaskTime = NumUtils.max(nextTaskTime, max);
				
				if(max <= 0) {
					finCookies.add(cookie);
				}
			}
		}
	}
	
	/**
	 * 当cookies发生变化时, 重置每日任务
	 */
	private void resetDailyTasks() {
		
		// 当跨天时, 重置任务时间, 且清空完成任务的cookie标记
		long now = System.currentTimeMillis();
		if(now - resetTaskTime > DAY_UNIT) {
			resetTaskTime = now;
			nextTaskTime = now;
			finCookies.clear();
			
		// 当cookie发生变化时, 仅重置任务时间
		} else if(lastAddCookieTime != CookiesMgr.INSTN().getLastAddCookieTime()) {
			lastAddCookieTime = CookiesMgr.INSTN().getLastAddCookieTime();
			nextTaskTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * 触发事件
	 */
	private void doEvent() {
		if(loopCnt++ >= EVENT_LIMIT) {
			loopCnt = 0;
			
			// 自动投喂
			toAutoFeed();
			
			// 打印心跳
			log.info("{} 活动中...", getName());
			UIUtils.printVersionInfo();
		}
	}
	
	/**
	 * 自动投喂
	 */
	private void toAutoFeed() {
		if(UIUtils.isAutoFeed() == false) {
			return;
		}
		
		int roomId = UIUtils.getFeedRoomId();
		Set<BiliCookie> cookies = CookiesMgr.INSTN().MINIs();
		for(BiliCookie cookie : cookies) {
			if(cookie.isAutoFeed()) {
				XHRSender.toFeed(cookie, roomId);
			}
		}
	}
	
}
