package volkodav.ampilogova.darya.projecte_missatgeria;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ComprovarXarxa comprovacio;
    private ValidacioUsuari usuari;
    private Button boto;
    private EditText nom, password, text_missatge;
    private Preferencies preferencies;
    private ListView lv;
    private Button boto_afegir;
    private BufferedReader in;
    private int responseCode = -1;
    private SimpleAdapter adapter;
    private HelperQuepassaeh database;

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

                // EXECUTAM L'ASYNTASK PASSANT-LI COM A ARGUMENT LA RUTA DE LA IMATGE
                String ruta = "https://iesmantpc.000webhostapp.com/public/provamissatge/";
                Log.d("RUN", "DESCARREGA" + ruta);
                if (v == boto_afegir) {
                    new TascaDescarrega().execute(ruta);
                }
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

                    // EXECUTAM L'ASYNTASK PASSANT-LI COM A ARGUMENT LA RUTA DE LA IMATGE
                    String ruta = "https://iesmantpc.000webhostapp.com/public/provamissatge/";
                    Log.d("RUN", "DESCARREGA" + ruta);
                    new TascaDescarrega().execute(ruta);

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class TascaDescarrega extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // ELIMINEM EL TEXTE
            lv.setAdapter(null);
        }

        protected String doInBackground(String... params) {
            StringBuilder text = new StringBuilder();
            URL url = null;
            try {
                // AGAFAM LA URL QUE S'HA PASSAT COM ARGUMENT
                url = new URL(params[0]);

                // FEIM LA CONNEXIÓ A LA URL
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                // CODI DE RESPOSTA
                responseCode = httpURLConnection.getResponseCode();
                Log.d("RUN", "Descarrega " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    // RECOLLIM EL TEXTE
                    in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String texte;
                    while ((texte = in.readLine()) != null) {
                        text.append(texte);
                    }
                    in.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return text.toString();
        }

        @Override
        protected void onPostExecute(String resultat) {
            String res = "";

            // QUAN LA TASCA ACABI, AGAFAM EL STRING QUE ÉS UN JSON
            // PARSE
            try {
                JSONObject json = new JSONObject(resultat);
                JSONArray jArray = json.getJSONArray("dades");

                // LLISTA DE DESCÀRREGUES
                ArrayList<HashMap<String, String>> llista = new ArrayList<HashMap<String, String>>();

                // ES GUARDA A LA LLISTA
                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject jObject = jArray.getJSONObject(i);
                    map.put("codi",jObject.getString("codi"));
                    map.put("msg",jObject.getString("msg"));
                    map.put("datahora",jObject.getString("datahora"));
                    map.put("codiusuari",jObject.getString("codiusuari"));
                    map.put("nom",jObject.getString("nom"));
                    llista.add(map);
                }

                adapter = new SimpleAdapter(getBaseContext(), llista, R.layout.estil_missatge,
                        new String[]{"codi", "msg", "datahora", "codiusuari", "nom"},
                        new int[]{R.id.text_codi, R.id.text_msg, R.id.text_datahora,
                        R.id.text_codiusuari, R.id.text_nom});

                lv.setAdapter(adapter);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}