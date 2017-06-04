package com.freakybyte.openxcdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.openxc.VehicleManager;
import com.openxc.measurements.EngineSpeed;
import com.openxc.measurements.FuelLevel;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.VehicleDoorStatus;
import com.openxc.measurements.VehicleSpeed;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;
    @BindView(R.id.tv_rpm)
    public AppCompatTextView mTvRmp;
    @BindView(R.id.tv_fuel_level)
    public AppCompatTextView mTvFuelLevel;
    @BindView(R.id.tv_vehicle_speed)
    public AppCompatTextView mTvVehicleSpeed;
    @BindView(R.id.tv_car_door)
    public AppCompatTextView mTvCarDoor;
    @BindView(R.id.iv_vehicle)
    public SimpleDraweeView mIvVehicle;

    private VehicleManager mVehicleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        mIvVehicle.setImageURI("http://instillsolutions.com/watertownford/leasespecials/transitconnect.jpg");

    }

    @Override
    public void onPause() {
        super.onPause();
        // When the activity goes into the background or exits, we want to make
        // sure to unbind from the service to avoid leaking memory
        if (mVehicleManager != null) {
            Log.i(TAG, "Unbinding from Vehicle Manager");
            // Remember to remove your listeners, in typical Android
            // fashion.
            mVehicleManager.removeListener(EngineSpeed.class,
                    mSpeedListener);
            mVehicleManager.removeListener(EngineSpeed.class,
                    mFuelListener);
            mVehicleManager.removeListener(EngineSpeed.class,
                    mVehicleSpeed);
            mVehicleManager.removeListener(EngineSpeed.class,
                    mDoorStatus);
            unbindService(mConnection);
            mVehicleManager = null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // When the activity starts up or returns from the background,
        // re-connect to the VehicleManager so we can receive updates.
        if (mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    EngineSpeed.Listener mSpeedListener = new EngineSpeed.Listener() {
        @Override
        public void receive(Measurement measurement) {
            final EngineSpeed speed = (EngineSpeed) measurement;
            HomeActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mTvRmp.setText(String.valueOf(speed.getValue().doubleValue()) +" RPM");
                }
            });
        }
    };

    FuelLevel.Listener mFuelListener = new FuelLevel.Listener() {
        @Override
        public void receive(Measurement measurement) {
            final FuelLevel fuel = (FuelLevel) measurement;
            HomeActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mTvFuelLevel.setText(String.valueOf(fuel.getValue().doubleValue()) + " %");
                }
            });
        }
    };

    VehicleSpeed.Listener mVehicleSpeed = new VehicleSpeed.Listener() {
        @Override
        public void receive(Measurement measurement) {
            final VehicleSpeed speed = (VehicleSpeed) measurement;
            HomeActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mTvVehicleSpeed.setText(String.valueOf(speed.getValue().doubleValue()) + " km/h");
                }
            });
        }
    };


    VehicleDoorStatus.Listener mDoorStatus = new VehicleDoorStatus.Listener() {
        @Override
        public void receive(Measurement measurement) {
            final VehicleDoorStatus status = (VehicleDoorStatus) measurement;
            HomeActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mTvCarDoor.setText(String.valueOf(status.getValue().getSerializedValue()) + "");
                }
            });
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the VehicleManager service is
        // established, i.e. bound.
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            // When the VehicleManager starts up, we store a reference to it
            // here in "mVehicleManager" so we can call functions on it
            // elsewhere in our code.
            mVehicleManager = ((VehicleManager.VehicleBinder) service)
                    .getService();

            // We want to receive updates whenever the EngineSpeed changes. We
            // have an EngineSpeed.Listener (see above, mSpeedListener) and here
            // we request that the VehicleManager call its receive() method
            // whenever the EngineSpeed changes
            mVehicleManager.addListener(EngineSpeed.class, mSpeedListener);
            mVehicleManager.addListener(FuelLevel.class, mFuelListener);
            mVehicleManager.addListener(VehicleSpeed.class, mVehicleSpeed);
            mVehicleManager.addListener(VehicleDoorStatus.class, mDoorStatus);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleManager Service  disconnected unexpectedly");
            mVehicleManager = null;
        }
    };

}
