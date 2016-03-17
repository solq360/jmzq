package jzmq;

import org.zeromq.ZMQ;

public class Test_Router_Dealer extends TestCtx {
    public static void main(String args[]) {
	final ZMQ.Context context = ZMQ.context(1);
	ZMQ.Socket router = context.socket(ZMQ.ROUTER);
	ZMQ.Socket dealer = context.socket(ZMQ.DEALER);

	router.bind("tcp://*:5555");
	dealer.bind("tcp://*:5566");

	for (int i = 0; i < 20; i++) {
	    new Thread(new Runnable() {

		public void run() {
		    ZMQ.Socket response = context.socket(ZMQ.PUSH);
		    response.connect("tcp://localhost:5555");
		    while (!Thread.currentThread().isInterrupted()) {
			//response.recv();
			response.send("hello");
			try {
			    Thread.currentThread().sleep(1);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
		    }
		    response.close();
		}

	    }).start();
	}
	ZMQ.proxy(router, dealer, null);
	router.close();
	dealer.close();
	context.term();
    }
}