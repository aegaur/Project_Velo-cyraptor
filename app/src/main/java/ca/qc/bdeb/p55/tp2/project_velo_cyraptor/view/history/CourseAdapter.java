package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Course;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.util.OnListFragmentInteractionListener;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private final List<Course> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;

    public CourseAdapter(List<Course> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setValues(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.course);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private final TextView lblDate;
        private final TextView lblTrajet;
        private final TextView lblTemps;
        private final TextView lblDistance;
        private Course course;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            lblDate = (TextView) view.findViewById(R.id.fragment_course_date);
            lblTrajet = (TextView) view.findViewById(R.id.fragment_course_trajet);
            lblTemps = (TextView) view.findViewById(R.id.fragment_course_temps);
            lblDistance = (TextView) view.findViewById(R.id.fragment_course_distance);
        }

        public void setValues(Course course) {
            this.course = course;
            lblDate.setText(this.course.getDATE().toString());
            lblTrajet.setText(this.course.getTrajet() != null ? this.course.getTrajet().getNom() : context.getResources().getString(R.string.activity_run_path_selection_none));
            lblTemps.setText(TimeUnit.MILLISECONDS.toMinutes(this.course.getDuree()) + " " + context.getResources().getString(R.string.fragment_course_unite_temps));
            lblDistance.setText(String.format(context.getResources().getString(R.string.activity_run_lbl_distance_valeur), this.course.getDistance()));
        }
    }
}
