package net.diogomarques.wifioppish;

import net.diogomarques.utils.CountDownTimer;
import net.diogomarques.wifioppish.IEnvironment.State;
import net.diogomarques.wifioppish.INetworkingFacade.OnSendListener;


/**
 * Android implementation of state {@link IEnvironment.State#Station}
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 */
public class StateStation extends AState {

	public StateStation(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout) {
		environment.deliverMessage("entered station state");
		final INetworkingFacade networking = environment.getNetworkingFacade();

		// TODO: adjust period
		int period = 1000;
		new CountDownTimer(environment.getPreferences().getTCon(), period) {

			@Override
			public void onTick(long arg0) {
				OnSendListener listener = new OnSendListener() {

					@Override
					public void onSendError(String errorMsg) {
						environment.deliverMessage("send error: " + errorMsg
								+ "[" + environment.getCurrentState().name()
								+ "]");
						cancel();
						environment.gotoState(State.Scanning);
					}

					@Override
					public void onMessageSent(String msg) {
						environment.deliverMessage("message successfully sent");
					}
				};

				networking.send("alive", listener);
			}

			@Override
			public void onFinish() {
				environment
						.deliverMessage("t_con finished, exiting station mode");
				environment.gotoState(State.Scanning);

			}
		}.start();
	}

}
