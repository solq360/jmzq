package jzmq;

import org.zeromq.ZMQ;

/***
 * @author solq
 * */
public class TestCtx {
    static{
	System.loadLibrary("libzmq-v120-mt-4_0_4");
	System.out.println(ZMQ.getVersionString());
    }
}
