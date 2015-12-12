package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.history;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Course;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.TypeCourse;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.persistance.DbHelper;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.OnListFragmentInteractionListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CourseFragment extends Fragment {

    private static final String ARG_KEY = "type";
    private TypeCourse typeCourse;

    private OnListFragmentInteractionListener mListener;
    private DbHelper dbHelper;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseFragment() {
    }

    public static CourseFragment newInstance(TypeCourse typeCourse) {
        CourseFragment fragment = new CourseFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_KEY, typeCourse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DbHelper.getInstance(getContext());

        if (getArguments() != null) {
            typeCourse = (TypeCourse) getArguments().getSerializable(ARG_KEY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            chargerCourses();
        }
        return view;
    }

    private void chargerCourses() {
        List<Course> listeCourses = dbHelper.getTousCourses(typeCourse.name());
        recyclerView.setAdapter(new CourseAdapter(listeCourses, mListener, getContext()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public TypeCourse getTypeCourse() {
        return typeCourse;
    }

    public void refraichir() {
        chargerCourses();
    }
}
