public class MainActivity extends Activity implements SensorEventListener {
        private SensorManager mSensorManager;
        private Sensor mProximity;

        @Override
        public final void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // Toast.makeText(getApplicationContext(), "hello1", Toast.LENGTH_SHORT).show();

            // Get an instance of the sensor service, and use that to get an instance of
            // a particular sensor.
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        @Override
        public final void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do something here if sensor accuracy changes.
        }

        @Override
        public final void onSensorChanged(SensorEvent event) {
            float distance = event.values[0];
            String p = String.valueOf(distance);
            if (distance<5) {
                Toast.makeText(getApplicationContext(), "Check", Toast.LENGTH_SHORT).show();
            }

            // Do something with this sensor data.
        }

        @Override
        protected void onResume() {
            // Register a listener for the sensor.
            super.onResume();
            mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        protected void onPause() {
            // Be sure to unregister the sensor when the activity pauses.
            super.onPause();
            mSensorManager.unregisterListener(this);
        }
    }