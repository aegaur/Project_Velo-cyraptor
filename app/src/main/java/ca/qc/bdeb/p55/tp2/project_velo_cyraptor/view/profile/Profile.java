package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.profile;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Profil;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Sexe;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.persistance.DbHelper;

import java.util.ArrayList;

public class Profile extends ActionBarActivity {

    private static final double RATIO_CONVERSION_POIDS = 2.2;
    private static final int RADIX = 10;

    private DbHelper dbHelper;
    private Profil profil;

    private EditText txtAge;
    private EditText txtTaille;
    private EditText txtPoids;
    private Spinner spnSexe;
    private Button btnSauvegarder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = DbHelper.getInstance(this);
        affecterViews();
        initSpinnerSexe();
        affecterValeurs();
        affecterListeners();
    }

    private void affecterListeners() {
        btnSauvegarder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sauvegarderProfil();
            }
        });
    }

    /**
     * Sauvegarde les informations du profil dans la base de données
     */
    private void sauvegarderProfil() {
        boolean success = true;
        String strAge = txtAge.getText().toString();
        String strTaille = txtTaille.getText().toString();
        String strPoids = txtPoids.getText().toString();
        if (isInteger(strAge)) {
            this.profil.setAge(Integer.parseInt(strAge));
        } else {
            success = false;
        }
        if (isInteger(strTaille)) {
            this.profil.setTailleCm(Integer.parseInt(strTaille));
        } else {
            success = false;
        }
        if (isInteger(strPoids)) {
            this.profil.setPoidsLbs(kgEnLbs(Integer.parseInt(strPoids)));
        } else {
            success = false;
        }
        this.profil.setSexe(Sexe.getSexeByIndex(this.spnSexe.getSelectedItemPosition()));
        if (success) {
            success = dbHelper.updateProfil(profil);
        }
        Toast.makeText(this, success ? R.string.activity_profile_tst_save_success
                : R.string.activity_profile_tst_save_failure, Toast.LENGTH_LONG).show();
    }

    /**
     * Détermine si une string est un entier
     * @param s la string
     * @return vrai si c'est un entier faux sinon
     * @source http://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
     */
    public static boolean isInteger(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), RADIX) < 0) return false;
        }
        return true;
    }

    /**
     * Initialize les variables de classe avec la vue associés
     */
    private void affecterViews() {
        txtAge = (EditText) findViewById(R.id.activity_profile_txt_age);
        txtTaille = (EditText) findViewById(R.id.activity_profile_txt_taille);
        txtPoids = (EditText) findViewById(R.id.activity_profile_txt_poids);
        spnSexe = (Spinner) findViewById(R.id.activity_profile_spn_sexe);
        btnSauvegarder = (Button) findViewById(R.id.activity_profile_btn_save);
    }

    /**
     * Affêcte les valeurs du profile aux champs
     */
    private void affecterValeurs() {
        profil = dbHelper.getProfil();
        txtAge.setText(Integer.toString(profil.getAge()));
        txtPoids.setText(Long.toString(lbsEnKg(profil.getPoidsLbs())));
        txtTaille.setText(Integer.toString(profil.getTailleCm()));
        spnSexe.setSelection(profil.getSexe().getINDEX());
    }

    /**
     * Initialize le spniier de sexe
     */
    private void initSpinnerSexe() {
        ArrayList<String> listeString = new ArrayList<>();
        for (Sexe sexe : Sexe.values()) {
            listeString.add(getString(sexe.getID_STRING()));
        }
        spnSexe.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listeString));
    }

    /**
     * Convertis des livres en kilo
     * @param lbs la valeure en livres
     * @return la valeure en kilo
     */
    private long lbsEnKg(double lbs) {
        return Math.round(lbs / RATIO_CONVERSION_POIDS);
    }

    /**
     * Convertis des kilo en livres
     * @param kilos la valeure en kilo
     * @return la valeure en livres
     */
    private int kgEnLbs(int kilos) {
        return (int) Math.round(kilos * RATIO_CONVERSION_POIDS);
    }
}
