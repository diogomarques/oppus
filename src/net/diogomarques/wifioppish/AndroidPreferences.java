package net.diogomarques.wifioppish;

import java.util.Random;

import net.diogomarques.wifioppish.IEnvironment.State;
import android.content.Context;

/**
 * Android-specific domain parameters.
 * 
 * TODO: use Android's shared preferences instead of hard-coded params & create
 * prefs activity. use recommended params in trifunovic et al. as default.
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 * 
 */
public class AndroidPreferences implements IDomainPreferences {

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
		int t_beac = 1000 * 30;
		return getRandomTime(t_beac);		
	}

	@Override
	public int getTPro() {
		int t_pro = 1000 * 30;
		return getRandomTime(t_pro);
	}

	@Override
	public int getTScan() {
		int t_scan = 1000 * 30;
		return getRandomTime(t_scan);
	}

	@Override
	public int getTCon() {
		int t_con = 1000 * 30;
		return getRandomTime(t_con);
	}

	/**
	 * Return a random uniform value between the given minimum and a maximum set
	 * at 3 the times the minimum.
	 * <p>
	 * <blockquote>"The respective maximum times are always 3 times the min
	 * times." (p. 39)<footer>Trifunovic et al, 2011, <a
	 * href="http://202.194.20.8/proc/MOBICOM2011/chants/p37.pdf"
	 * >PDF</a></footer></blockquote>
	 * 
	 * @param minTime
	 *            the minimum time
	 * @return a randomly distributed value between minTime and 3 * minTime
	 */
	protected int getRandomTime(int minTime) {
		int dif = 3 * minTime - minTime;
		return (int) (new Random().nextDouble() * dif + minTime);
	}

	@Override
	public int getScanPeriod() {
		return 1000;
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
}
