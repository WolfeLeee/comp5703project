package comp5703.sydney.edu.au.kinderfoodfinder;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;

public class StartUpActivity extends AppCompatActivity
{
    /* * * * * * * * * * *
     * Defined Variables *
     * * * * * * * * * * */
    private Fragment fragmentLogin;

    /* * * * * * * * * * *
     * On Created Method *
     * * * * * * * * * * */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // set up fragment
        fragmentLogin = new LoginFragment();

        // directly make the view to login fragment first
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragmentLogin).commit();
    }
}
