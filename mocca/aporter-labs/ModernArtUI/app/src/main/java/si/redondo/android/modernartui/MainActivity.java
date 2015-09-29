package si.redondo.android.modernartui;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;


public class MainActivity extends ActionBarActivity {

    private final int SEEK_MAX = 100;
    private SeekBar seekBar;
    private Button button_lu_1, button_lu_2, button_lu_3;
    private Button button_ll_1, button_rm_1;
    private Button button_ru_1, button_ru_2;
    private Button button_rl_1, button_rl_2;
    private LinearLayout layout_dyn_lu, layout_dyn_ru, layout_dyn_rl;
    private int mSeekBarProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(SEEK_MAX);

        layout_dyn_lu = (LinearLayout) findViewById(R.id.layout_dyn_lu);
        layout_dyn_ru = (LinearLayout) findViewById(R.id.layout_dyn_ru);
        layout_dyn_rl = (LinearLayout) findViewById(R.id.layout_dyn_rl);

        button_ll_1 = (Button) findViewById(R.id.button_ll_1);
        button_rm_1 = (Button) findViewById(R.id.button_rm_1);
        button_lu_1 = (Button) findViewById(R.id.button_lu_1);
        button_lu_2 = (Button) findViewById(R.id.button_lu_2);
        button_lu_3 = (Button) findViewById(R.id.button_lu_3);
        button_ru_1 = (Button) findViewById(R.id.button_ru_1);
        button_ru_2 = (Button) findViewById(R.id.button_ru_2);
        button_rl_1 = (Button) findViewById(R.id.button_rl_1);
        button_rl_2 = (Button) findViewById(R.id.button_rl_2);

        createSeekBarListener();
    }

    private void updateColors() {

        button_lu_1.setBackgroundColor(new Color().argb(
                (int)(Math.random() * 255),
                (int)(Math.random() * 255), 0,(int)(Math.random() * 255)));
        button_lu_2.setBackgroundColor(new Color().argb(
                (int) (Math.random() * 255),
                (int)(Math.random() * 255), 0,(int)(Math.random() * 255)));
        button_lu_3.setBackgroundColor(new Color().argb(
                (int) (Math.random() * 255),
                (int)(Math.random() * 255), 0,(int)(Math.random() * 255)));
        button_ll_1.setBackgroundColor(new Color().argb(
                (int) (Math.random() * 255),
                (int)(Math.random() * 255), (int)(Math.random() * 255), 255));
        button_ru_1.setBackgroundColor(new Color().argb(
                (int) (Math.random() * 255),
                (int)(Math.random() * 255), (int)(Math.random() * 255),255));
        button_ru_2.setBackgroundColor(new Color().argb(
                (int) (Math.random() * 255),
                (int)(Math.random() * 255), 100, 100));
        button_rl_1.setBackgroundColor(new Color().argb(
                (int) (Math.random() * 255),
                (int)(Math.random() * 255), 255, 0));
        button_rl_2.setBackgroundColor(new Color().argb(
                (int) (Math.random() * 255),
                (int)(Math.random() * 255), 255, 0));
    }

    private void updateLayout() {
        float seekLevel = ((float) mSeekBarProgress) / SEEK_MAX;
        float random1 = (float) Math.random();
        float random2 = (float) (Math.random());

        /* first dynamic layer */
        button_lu_1.setLayoutParams(new LinearLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, 0, random1));

        button_lu_2.setLayoutParams(new LinearLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, 0, seekLevel));

        button_lu_3.setLayoutParams(new LinearLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, 0, random2));

        /* second dynamic layer */
        float random3 = (float) (Math.random());
        button_ru_1.setLayoutParams(new LinearLayout.LayoutParams(
                0, TableLayout.LayoutParams.MATCH_PARENT, 1 - random3));
        button_ru_2.setLayoutParams(new LinearLayout.LayoutParams(
                0, TableLayout.LayoutParams.MATCH_PARENT, random3));

        /* third dynamic layer */
        float random4 = (float) (Math.random()/100 * 13 - 0.08);
        button_rl_1.setLayoutParams(new LinearLayout.LayoutParams(
                0, TableLayout.LayoutParams.MATCH_PARENT, seekLevel + random4));
        button_rl_2.setLayoutParams(new LinearLayout.LayoutParams(
                0, TableLayout.LayoutParams.MATCH_PARENT, 1 - seekLevel - random4));

    }

    private void createSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekBarProgress = progress;
                updateColors();
                updateLayout();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                button_rm_1.setText("MoSS");
                button_rm_1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                button_rm_1.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            (new LinkDialogBox()).show(getFragmentManager(), "Link");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
