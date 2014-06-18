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

	private static final String TAG = "Pedometer Sensor";
	
	private Integer steps;
	private Sensor mSensor;
	private SensorManager mSensorManager;
	private StepDetector stepDetector;
	private StepListener stepListener = new StepListener() {
		
		@Override
		public void passValue() { }
		
		@Override
		public void onStep() {
			steps++;
			Log.i(TAG, steps + " steps");
		}
	};
	
	
	public PedometerSensor(Context c) {
		super(c);
		steps = 0;
	}
	
	@Override
	public Object getCurrentValue() {
		return steps;
	}
	
	@Override
	public void startSensor() {
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(
	            Sensor.TYPE_ACCELEROMETER);
		stepDetector = new StepDetector();
		stepDetector.addStepListener(stepListener);
        mSensorManager.registerListener(stepDetector,
	            mSensor,
	            SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public void stopSensor() {
		// TODO Auto-generated method stub
		
	}

}
