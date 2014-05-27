package net.diogomarques.wifioppish;

import java.util.Random;

import net.diogomarques.wifioppish.IEnvironment.State;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Android-specific domain parameters.
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 */
public class AndroidPreferences implements IDomainPreferences {

	// FIXME switch before deployment
	public static final boolean DEBUG = true;

	/*
	 * Universal timeout parameter for use in debugging.
	 */
	int debugMinTimeMilis = 1000 * 30;

	// Dependencies
	private Context mContext;

	/**
	 * Constructor.
	 * 
	 * @param ctx
	 *            the current context.
	 */
	public AndroidPreferences(Context ctx) {
		mContext = ctx;
	}

	protected Context getContext() {
		return mContext;
	}

	@Override
	public int getPort() {
		return 33333;
	}

	@Override
	public int getTBeac() {
		return getRandomTimeFromKey(R.string.key_t_beac);
	}

	@Override
	public int getTPro() {
		return getRandomTimeFromKey(R.string.key_t_pro);
	}

	@Override
	public int getTScan() {
		return getRandomTimeFromKey(R.string.key_t_scan);
	}

	@Override
	public int getTCon() {
		return getRandomTimeFromKey(R.string.key_t_con);
	}
	@Override
	public int getTInt() {
		return DEBUG ? 5000 : getRandomTimeFromKey(R.string.key_t_int);
	}

	/**
	 * Returns a random uniform value between a minimum (set in the default
	 * {@link SharedPreferences} with key <i>resId</i>) and a maximum (set at 3
	 * the times the minimum).
	 * <p>
	 * <blockquote>"The respective maximum times are always 3 times the min
	 * times." (p. 39)<footer>Trifunovic et al, 2011, <a
	 * href="http://202.194.20.8/proc/MOBICOM2011/chants/p37.pdf"
	 * >PDF</a></footer></blockquote>
	 * 
	 * @param resId
	 *            the resource identifier for they key in
	 *            {@link SharedPreferences}
	 * @return a randomly distributed value between
	 */
	protected int getRandomTimeFromKey(int resId) {
		if (DEBUG)
			return debugMinTimeMilis;
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		String key = mContext.getString(resId);
		int minTime = Integer.parseInt(prefs.getString(key, null));
		int dif = 3 * minTime - minTime;
		return (int) (new Random().nextDouble() * dif + minTime);
	}

	@Override
	public int getScanPeriod() {
		return 5000;
	}

	@Override
	public String getWifiSSID() {
		return "emergencyAP";
	}

	@Override
	public String getWifiPassword() {
		return "emergency";
	}

	@Override
	public State getStartState() {
		return IEnvironment.State.Scanning;
	}
	@Override
	public boolean checkInternetMode() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		boolean internetState = prefs.getBoolean("internet",false);
		return internetState;
	}

	@Override
	public String getApiEndpoint() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		String key = mContext.getString(R.string.key_t_api);
		String address = prefs.getString(key, null);
		
		return address;
	}
}
