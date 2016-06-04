package exp.libs.warp.queue.sc.test;

import exp.libs.warp.other.queue.sc.SCQBean;

public class MyBean extends SCQBean<MyElement> {

	public MyBean(MyElement e) {
		super(e);
	}

	@Override
	protected void handle(MyElement e) {
		String s = e.getS();
		System.out.println("data: " + s);
		// TODO something
		e.setResult("test: this is your result");
	}
	
}