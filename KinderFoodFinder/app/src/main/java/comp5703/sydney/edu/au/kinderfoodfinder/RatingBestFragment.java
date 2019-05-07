package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RatingBestFragment extends Fragment {

    TextView tv_title_hen, tv_title_chicken, tv_title_pigs;
    TextView hens_best, chickens_best, pigs_best;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating_best, container, false);

        tv_title_chicken = view.findViewById(R.id.title_chickens);
        tv_title_hen = view.findViewById(R.id.title_hens);
        tv_title_pigs = view.findViewById(R.id.title_pigs);

        hens_best = view.findViewById(R.id.hens_best);
        chickens_best = view.findViewById(R.id.chickens_best);
        pigs_best = view.findViewById(R.id.pigs_best);

        return view;

    }
}
