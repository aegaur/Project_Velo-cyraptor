package ca.qc.bdeb.p55.tp2.project_velo_cyraptor.view.profile;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.R;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Profil;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.model.Sexe;
import ca.qc.bdeb.p55.tp2.project_velo_cyraptor.persistance.DbHelper;

import java.util.ArrayList;

public class Profile extends ActionBarActivity {

    private static final double RATIO_CONVERSION_POIDS = 2.2;

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

    private void sauvegarderProfil() {
        this.profil.setAge(Integer.parseInt(txtAge.getText().toString()));
        this.profil.setTailleCm(Integer.parseInt(txtTaille.getText().toString()));
        this.profil.setPoidsLbs(kgEnLbs(Integer.parseInt(txtPoids.getText().toString())));
        this.profil.setSexe(Sexe.getSexeByIndex(this.spnSexe.getSelectedItemPosition()));
        dbHelper.updateProfil(profil);
    }

    private void affecterViews() {
        txtAge = (EditText) findViewById(R.id.activity_profile_txt_age);
        txtTaille = (EditText) findViewById(R.id.activity_profile_txt_taille);
        txtPoids = (EditText) findViewById(R.id.activity_profile_txt_poids);
        spnSexe = (Spinner) findViewById(R.id.activity_profile_spn_sexe);
        btnSauvegarder = (Button) findViewById(R.id.activity_profile_btn_save);
    }

    private void affecterValeurs() {
        profil = dbHelper.getProfil();
        txtAge.setText(Integer.toString(profil.getAge()));
        txtPoids.setText(Long.toString(lbsEnKg(profil.getPoidsLbs())));
        txtTaille.setText(Integer.toString(profil.getTailleCm()));
        spnSexe.setSelection(profil.getSexe().getINDEX());
    }

    private void initSpinnerSexe() {
        ArrayList<String> listeString = new ArrayList<>();
        for (Sexe sexe : Sexe.values()) {
            listeString.add(getString(sexe.getID_STRING()));
        }
        spnSexe.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listeString));
    }

    private long lbsEnKg(double lbs) {
        return Math.round(lbs / RATIO_CONVERSION_POIDS);
    }

    private int kgEnLbs(int kilos) {
        return (int) Math.round(kilos * RATIO_CONVERSION_POIDS);
    }
}
