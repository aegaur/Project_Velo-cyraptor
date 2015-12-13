package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.statistics;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Trajet;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.TypeCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.OnFragmentInteractionListener;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.ViewPagerAdapter;

import static ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.statistics.TrajetDetail.CLEE_TRAJET;

public class Statistics extends AppCompatActivity implements OnFragmentInteractionListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Initialize le view pager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(StatisticsFragment.newInstance(TypeCourse.A_PIED), getString(R.string.activity_run_section_title));
        adapter.addFragment(StatisticsFragment.newInstance(TypeCourse.A_VELO), getString(R.string.activity_bike_section_title));
        adapter.addFragment(TrajetFragment.newInstance(), getString(R.string.activity_stats_path_section_title));
        viewPager.setAdapter(adapter);
    }

    /**
     * Évènement recue lors du click sur un élément de la liste de trajets
     */
    @Override
    public void onFragmentInteraction(Trajet trajet) {
        Intent intent = new Intent(this, TrajetDetail.class);
        intent.putExtra(CLEE_TRAJET, trajet);
        startActivity(intent);
    }
}
