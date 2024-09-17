package co.edu.unipiloto.stopwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

public class activity_stopwatch extends Activity {
    private long startTime = 0L;
    private long elapsedTime = 0L;
    private boolean running;
    private ArrayList<Long> lapTimes = new ArrayList<>();
    private long lastLapTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        if (savedInstanceState != null){
            elapsedTime = savedInstanceState.getLong("elapsedTime");
            running = savedInstanceState.getBoolean("running");
            lastLapTime = savedInstanceState.getLong("lastLapTime");
            lapTimes = (ArrayList<Long>) savedInstanceState.getSerializable("lapTimes");
        }
        runTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        saveInstanceState.putLong("elapsedTime", elapsedTime);
        saveInstanceState.putBoolean("running", running);
        saveInstanceState.putLong("lastLapTime", lastLapTime);
        saveInstanceState.putSerializable("lapTimes", lapTimes);
    }

    public void onClickStart(View view){
        if (!running) {
            startTime = System.nanoTime() - elapsedTime;
        }
        running = true;
    }

    public void onClickStop(View view){
        running = false;
    }

    public void onClickReset(View view){
        running = false;
        elapsedTime = 0L;
        lapTimes.clear();
        lastLapTime = 0L;
        displayLapTimes();
    }

    public void onClickLap(View view){
        if (running) {
            long currentLapTime = elapsedTime - lastLapTime;
            lapTimes.add(currentLapTime);
            lastLapTime = elapsedTime;
            displayLapTimes();
        }
    }

    private void displayLapTimes() {
        StringBuilder lapTimeText = new StringBuilder();
        for (int i = 0; i < lapTimes.size(); i++) {
            long lapTime = lapTimes.get(i);
            long totalSeconds = lapTime / 1_000_000_000;
            long milliseconds = (lapTime / 1_000_000) % 100;

            int hours = (int) (totalSeconds / 3600);
            int minutes = (int) ((totalSeconds % 3600) / 60);
            int secs = (int) (totalSeconds % 60);

            lapTimeText.append(String.format(Locale.getDefault(), "Vuelta %d: %02d:%02d:%02d.%02d\n", i + 1, hours, minutes, secs, milliseconds));
        }
        TextView lapTimesView = (TextView) findViewById(R.id.lap_times);
        lapTimesView.setText(lapTimeText.toString());
    }

    private void runTimer(){
        final Handler handler = new Handler();
        final TextView timeView = (TextView) findViewById(R.id.time_view);

        handler.post(new Runnable() {
            @Override
            public void run(){
                long now = System.nanoTime();
                if (running) {
                    elapsedTime = now - startTime;
                }

                long totalSeconds = elapsedTime / 1_000_000_000;
                long milliseconds = (elapsedTime / 1_000_000) % 100;

                int hours = (int) (totalSeconds / 3600);
                int minutes = (int) ((totalSeconds % 3600) / 60);
                int secs = (int) (totalSeconds % 60);
                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d.%02d", hours, minutes, secs, milliseconds);
                timeView.setText(time);

                handler.postDelayed(this, 10);
            }
        });
    }
}

