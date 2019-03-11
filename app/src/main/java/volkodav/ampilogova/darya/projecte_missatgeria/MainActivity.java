package volkodav.ampilogova.darya.projecte_missatgeria;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ComprovarXarxa comprovacio;
    private ValidacioUsuari usuari;
    private EditText nom, password, text_missatge;
    private Preferencies preferencies;
    public ListView lv;
    private Button boto_afegir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.llista_missatges);
        boto_afegir = findViewById(R.id.boto_afegir);
        text_missatge = findViewById(R.id.text_missatge);


        // DESACTIVEM EL MODE ESTRICTE I CREAREM UNA COMUNICACIÓ SÍNCRONA
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        comprovacio = new ComprovarXarxa();
        this.registerReceiver(comprovacio, filter);

        preferencies = new Preferencies(this);
        if (preferencies.getCodiusuari() == -1) {
            Intent i = new Intent(this, Login.class);
            startActivityForResult(i, 2);
        }

        boto_afegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    // CRIDEM AL MÈTODE ACTUALITZAESTATXARXA PER PODER COMPROVAR UNA CONNEXIÓ FUNCIONAL
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {

            if (resultCode == Activity.RESULT_OK) {
                try {
                    JSONObject js = new JSONObject(data.getStringExtra("resultat"));
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                // EXECUTAM L'ASYNTASK PASSANT-LI COM A ARGUMENT LA RUTA DE LA IMATGE
                String ruta = "https://iesmantpc.000webhostapp.com/public/provamissatge/";
                Log.d("RUN", "DESCARREGA" + ruta);
                new TascaDescarrega(lv, this).execute(ruta);
            }
        }
    }
}