package volkodav.ampilogova.darya.projecte_missatgeria;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ComprovarXarxa comprovacio;
    private ValidacioUsuari usuari;
    Button boto;
    EditText nom, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        comprovacio = new ComprovarXarxa();
        this.registerReceiver(comprovacio, filter);

        boto = findViewById(R.id.boto_login);
        boto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nom = findViewById(R.id.text_nom);
                password = findViewById(R.id.text_password);
                String txt_nom = nom.getText().toString();
                String txt_password = password.getText().toString();
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("nom", txt_nom);
                hashMap.put("password", txt_password);
                String resultat = usuari.cridadaPost("http://iesmantpc.000webhostapp.com/public/login/", hashMap);
            }
        });
    }

    // CRIDEM AL MÈTODE ActualitzaEstatXarxa PER PODER COMPROVAR UNA CONNEXIÓ FUNCIONAL
    public void onStart() {
        super.onStart();
        comprovacio.ActualitzaEstatXarxa(this);
    }

    // DONAM DE BAIXA AL RECEPTOR DE BROADCAST QUAN ES DESTRUEIX L'APLICACIÓ
    public void onDestroy() {
        super.onDestroy();
        if (comprovacio != null) {
            this.unregisterReceiver(comprovacio);
        }
    }
}