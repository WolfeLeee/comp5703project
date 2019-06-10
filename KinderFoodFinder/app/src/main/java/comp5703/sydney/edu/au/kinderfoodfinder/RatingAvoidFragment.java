package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RatingAvoidFragment extends Fragment {
    TextView tv_title_hen, tv_title_chicken, tv_title_pigs, pigs, line, note;
    TextView hens_avoid, chickens_avoid, pigs_avoid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating_avoid, container, false);
        tv_title_chicken = view.findViewById(R.id.title_chickens_avoid);
        tv_title_hen = view.findViewById(R.id.title_hens_avoid);
        tv_title_pigs = view.findViewById(R.id.title_pigs_avoid);

        hens_avoid = view.findViewById(R.id.hens_avoid);
        chickens_avoid = view.findViewById(R.id.chickens_avoid);
        pigs_avoid = view.findViewById(R.id.pigs_avoid);
        pigs = view.findViewById(R.id.pigs);
        line = view.findViewById(R.id.pigs_line);
        note = view.findViewById(R.id.pigs_note);
        return view;

    }
}
