package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.history;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Course;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.TypeCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.persistance.DbHelper;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.OnListFragmentInteractionListener;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.ViewPagerAdapter;

public class History extends AppCompatActivity implements OnListFragmentInteractionListener {

    private static final String CLEE_COURSE = "course";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_history);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        dbHelper = DbHelper.getInstance(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(CourseFragment.newInstance(TypeCourse.A_PIED), getString(R.string.activity_run_section_title));
        adapter.addFragment(CourseFragment.newInstance(TypeCourse.A_VELO), getString(R.string.activity_bike_section_title));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_history_clear) {
            confirmClearHistory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Course course) {
        Intent intent = new Intent(this, CourseDetails.class);
        intent.putExtra(CLEE_COURSE, course);
        startActivity(intent);
    }

    private void confirmClearHistory() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.activity_history_confirm_title)
                .setMessage(R.string.activity_history_confirm_message)
                .setPositiveButton(R.string.activity_history_confirm_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearHistory();
                    }
                })
                .setNegativeButton(R.string.activity_history_confirm_no, null).show();
    }

    private void clearHistory() {
        dbHelper.viderHistorique(tabLayout.getSelectedTabPosition() == 0 ? TypeCourse.A_PIED : TypeCourse.A_VELO);
    }
}
