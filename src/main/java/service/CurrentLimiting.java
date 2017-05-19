package service;

/**
 *  @author dzx
 * 控制在一定时间间隔内CASClient或者客户端访问的次数
 */
public class CurrentLimiting {
	
	//初始访问时间
	private volatile long firstAccessTime;
	
	//当前访问时间
	private volatile long currTime;

	//访问间隔
	private volatile long  timeInterval;
	
	//最大访问次数
	private volatile int maxTimes;
	
	//当前访问次数
	private volatile int times = 1;

	
	public CurrentLimiting(long firstAccessTime, long timeInterval, int maxTimes) {
		this.firstAccessTime = firstAccessTime;
		this.timeInterval = timeInterval;
		this.maxTimes = maxTimes;
	}


	public boolean allow(long currTime){
		if(currTime-firstAccessTime>timeInterval){
			firstAccessTime = currTime;
			times = 1;
			return true;
		}
		else{
			if(times<maxTimes){
				times++;
				return true;
			}
			else{
				return false;
			}
		}
	}
	
	public long getFirstAccessTime() {
		return firstAccessTime;
	}


	public void setFirstAccessTime(long firstAccessTime) {
		this.firstAccessTime = firstAccessTime;
	}
}
