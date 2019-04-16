package comp5703.sydney.edu.au.kinderfoodfinder;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

public class StartUpActivity extends AppCompatActivity
{
    /* * * * * * * * * * *
     * Defined Variables *
     * * * * * * * * * * */
    private Fragment fragmentLogin;
    private Toolbar toolbar;

    /* * * * * * * * * * *
     * On Created Method *
     * * * * * * * * * * */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // set up tool bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setVisibility(View.GONE);

        // set up fragment
        fragmentLogin = new LoginFragment();

        // directly make the view to login fragment first
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragmentLogin).commit();
    }
}
