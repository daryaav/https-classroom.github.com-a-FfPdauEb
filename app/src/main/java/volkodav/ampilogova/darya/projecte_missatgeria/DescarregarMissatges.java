package volkodav.ampilogova.darya.projecte_missatgeria;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
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

public class DescarregarMissatges extends AsyncTask<String, Integer, String> {

    private BufferedReader in;
    private int responseCode = -1;
    private SimpleAdapter adapter;
    private HelperQuepassaeh database;
    private SQLiteDatabase db;
    private ListView llista;
    private Context context;

    // CONSTRUCTOR DE LA CLASSE, ON LI PASSEM EL LISTVIEW PRINCIPAL I EL CONTEXTE
    public DescarregarMissatges(ListView llista, Context context) {
        this.llista = llista;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // ELIMINEM EL TEXTE
        llista.setAdapter(null);
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

        // QUAN LA TASCA ACABI, AGAFAM EL STRING QUE ÉS UN JSON PARSE
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

            adapter = new SimpleAdapter(context, llista, R.layout.estil_missatge,
                    new String[]{"codi", "msg", "datahora", "codiusuari", "nom"},
                    new int[]{R.id.text_codi, R.id.text_msg, R.id.text_datahora,
                            R.id.text_codiusuari, R.id.text_nom});

            this.llista.setAdapter(adapter);

        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}