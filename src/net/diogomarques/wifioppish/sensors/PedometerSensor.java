package net.diogomarques.wifioppish.sensors;

import name.bagi.levente.pedometer.StepDetector;
import name.bagi.levente.pedometer.StepListener;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Pedometer implementation adapted from bagilevi's Android Pedometer.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 * @see <a href="https://github.com/bagilevi/android-pedometer‎">bagilevi's Android Pedometer</a>‎
 */
public class PedometerSensor extends AbstractSensor {

	private static final String TAG = PedometerSensor.class.getSimpleName();
	
	private Integer steps;
	private Sensor mSensor;
	private SensorManager mSensorManager;
	private StepDetector stepDetector;
	private StepListener stepListener = new StepListener() {
		
		@Override
		public void onStep() {
			steps++;
			Log.i(TAG, steps + " movements");
		}
	};
	
	/**
	 * Creates a pedometer to count user steps/micro-movements
	 * @param c Android context
	 */
	public PedometerSensor(Context c) {
		super(c);
		steps = 0;
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		stepDetector = new StepDetector();
	}
	
	@Override
	public Object getCurrentValue() {
		return steps;
	}
	
	@Override
	public void startSensor() {
		stepDetector.addStepListener(stepListener);
        mSensorManager.registerListener(stepDetector,
	            mSensor,
	            SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public void stopSensor() {
		mSensorManager.unregisterListener(stepDetector);
	}

}
