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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ComprovarXarxa comprovacio;
    private ValidacioUsuari usuari;
    private EditText text_missatge;
    private TextView codusuari, n;
    private Preferencies preferencies;
    public ListView lv;
    private Button boto;
    private ValidacioUsuari connecta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INICIEM ELS ID'S DEL LAYOUT
        lv = findViewById(R.id.llista_missatges);
        boto = findViewById(R.id.boto_afegir);
        text_missatge = findViewById(R.id.text_missatge);

        // DESACTIVEM EL MODE ESTRICTE I CREAM UNA COMUNICACIÓ SÍNCRONA
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // COMPROVEM LA XARXA
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        comprovacio = new ComprovarXarxa();
        this.registerReceiver(comprovacio, filter);

        // GUARDEM EL LOGIN DINS LES PREFERÈNCIES PER NO HAVER DE TORNAR A INTRODUÏR LES DADES
        // EN TORNAR A ENTRAR
        preferencies = new Preferencies(this);
        if (preferencies.getCodiusuari() == -1) {
            Intent i = new Intent(this, Login.class);
            startActivityForResult(i, 2);
        }

        // ENVIEM EL MISSATGE EN CLICAR EL BOTÓ
        boto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String missatge = text_missatge.getText().toString();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String data = dateFormat.format(date);

                codusuari = findViewById(R.id.text_codiusuari);
                int codU = preferencies.getCodiusuari();

                n = findViewById(R.id.text_nom);
                String nom = preferencies.getUser();

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("msg", missatge );
                hashMap.put("data", data);
                hashMap.put("codiusuari", String.valueOf(codU));
                hashMap.put("nom", nom);

                String ruta = "https://iesmantpc.000webhostapp.com/public/provamissatge/";
                connecta.cridadaPost(ruta, hashMap);

                Toast.makeText(getBaseContext(), "Missatge enviat", Toast.LENGTH_LONG).show();

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

            // SI EL LOGIN ÉS CORRECTE...
            if (resultCode == Activity.RESULT_OK) {

                // CREAM UN OBJECTE JSON PER PODER DESCARREGAR LA LLISTA DE MISSATGES
                try {
                    JSONObject js = new JSONObject(data.getStringExtra("resultat"));
                    JSONObject js2 = new JSONObject(data.getStringExtra("codiusuari"));
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                String ruta = "https://iesmantpc.000webhostapp.com/public/provamissatge/";
                Log.d("RUN", "DESCARREGA" + ruta);
                new DescarregarMissatges(lv, this).execute(ruta);
            }
        }
    }
}