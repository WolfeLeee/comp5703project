package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RatingGoodFragment extends Fragment {
    TextView tv_title_hen, tv_title_chicken, tv_title_pigs;
    TextView hens_good, chickens_good, pigs_good;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating_good, container, false);

        tv_title_chicken = view.findViewById(R.id.title_chickens_good);
        tv_title_hen = view.findViewById(R.id.title_hens_good);
        tv_title_pigs = view.findViewById(R.id.title_pigs_good);

        hens_good = view.findViewById(R.id.hens_good);
        chickens_good = view.findViewById(R.id.chickens_good);
        pigs_good = view.findViewById(R.id.pigs_good);
        return view;

    }
}
