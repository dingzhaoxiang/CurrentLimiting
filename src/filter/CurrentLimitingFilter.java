package filter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.CurrentLimiting;
public class CurrentLimitingFilter implements Filter {
	ConcurrentHashMap<String,CurrentLimiting> map = new ConcurrentHashMap<String,CurrentLimiting>();
	Thread t = new Thread(new ScannForUselessIP(map));
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req =(HttpServletRequest) request;
		HttpServletResponse res =(HttpServletResponse) response;
		String ip = req.getRemoteAddr();
	    if(!map.containsKey(ip)){
	    	map.put(ip, new CurrentLimiting(System.currentTimeMillis(), 1000, 1));
	    }
	    else{
	    	if(!map.get(ip).allow(System.currentTimeMillis())){
				res.setHeader("Content-type", "text/html;charset=UTF-8");
				res.setCharacterEncoding("UTF-8");
				res.getWriter().println("访问过于频繁");
				return;
	    	}
	    }
	    chain.doFilter(request, response);	    				
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		t.start();
	}

}
//扫描过期的key(ip)
class ScannForUselessIP implements Runnable{
	ConcurrentHashMap<String,CurrentLimiting> map;
	
	public ScannForUselessIP(ConcurrentHashMap<String, CurrentLimiting> map) {
		this.map = map;
	}

	@Override
	public void run() {
		while(true){
			try {
				//每过5秒扫描一次
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			scan();
		}
	}
	public void scan(){
		System.out.println("扫描开始");
	    //扫描起始时间
		long currTime = System.currentTimeMillis();
		//key(ip)的最大存活时间
		long maxLife = 6000L;
		Iterator<Map.Entry<String, CurrentLimiting>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String,CurrentLimiting> entry = it.next();
			String key = entry.getKey();
			if(currTime-map.get(key).getFirstAccessTime()>maxLife){
				it.remove(); 
				System.out.println("The key " +  key + " was deleted");
			}
		}
	}
}

