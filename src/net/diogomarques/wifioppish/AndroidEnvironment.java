package net.diogomarques.wifioppish;

import android.os.Handler;
import android.os.Message;

public class AndroidEnvironment implements IEnvironment {

	private final Handler mHandler;

	public AndroidEnvironment(Handler consoleHandler) {
		this.mHandler = consoleHandler;
	}

	public Handler getmHandler() {
		return mHandler;
	}

	@Override
	public void notifyEnv(String msg) {
		mHandler.sendMessage(Message.obtain(mHandler, 0, msg));
	}

}
