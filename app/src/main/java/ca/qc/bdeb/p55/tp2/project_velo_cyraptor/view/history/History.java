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
import android.widget.ArrayAdapter;
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
    private CourseFragment[] tabFragments;
    private int indiceSortChoisi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        dbHelper = DbHelper.getInstance(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabFragments = new CourseFragment[TypeCourse.values().length];
        tabFragments[0] = CourseFragment.newInstance(TypeCourse.A_PIED);
        tabFragments[1] = CourseFragment.newInstance(TypeCourse.A_VELO);
        adapter.addFragment(tabFragments[0], getString(R.string.activity_run_section_title));
        adapter.addFragment(tabFragments[1], getString(R.string.activity_bike_section_title));
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
        } else if (id == R.id.menu_history_sort) {
            dialogSortHistory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogSortHistory() {
        final AlertDialog.Builder constructeurDialog = new AlertDialog.Builder(this);

        constructeurDialog.setTitle(R.string.activity_run_path_selection_title);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);

        for (HistorySorts historySorts : HistorySorts.values()) {
            arrayAdapter.add(getString(historySorts.getDISPLAY_ID()));
        }
        constructeurDialog.setSingleChoiceItems(arrayAdapter, indiceSortChoisi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                indiceSortChoisi = i;
                reloadFragments();
            }
        });
        constructeurDialog.show();
    }

    private void reloadFragments() {
        for (CourseFragment courseFragment : tabFragments) {
            courseFragment.rearangerListe(HistorySorts.values()[indiceSortChoisi]);
        }
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
        CourseFragment courseFragment = tabFragments[tabLayout.getSelectedTabPosition()];
        dbHelper.viderHistorique(courseFragment.getTypeCourse());
        courseFragment.refraichir();
    }
}
