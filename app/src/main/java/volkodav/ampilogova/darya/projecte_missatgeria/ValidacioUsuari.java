package volkodav.ampilogova.darya.projecte_missatgeria;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

// CLASSE ON VALIDAREM L'USUARI
public class ValidacioUsuari {

    // DINS EL MÈTODE LI HAUREM DE PASSAR LA ADREÇA URL QUE HAGUEM DE MESTER I UN HASHMAP
    // QUE TENDRÀ UN USUARI I UNA CONTRASENYA
    public static String cridadaPost(String adrecaURL, HashMap<String, String> parametres) {
        String resultat = "";
        try {
            URL url = new URL(adrecaURL);
            Log.i("ResConnectUtils", "Connectant" + adrecaURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setReadTimeout(15000);
            httpConn.setConnectTimeout(25000);
            httpConn.setRequestMethod("POST");
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            // CRIDEM AL MÈTODE DE MONTAPARAMETRES PER CREAR LA CLAU-VALOR AMB ELS PARÀMETRES PASSATS
            writer.write(montaParametres(parametres));
            writer.flush();
            writer.close();
            os.close();
            int resposta = httpConn.getResponseCode();
            if (resposta == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new
                        InputStreamReader(httpConn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    resultat += line;
                }
                Log.i("ResConnectUtils", resultat);
            } else {
                resultat = "";
                Log.i("ResConnectUtils", "Errors:" + resposta);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultat;
    }

    public static String montaParametres(HashMap<String, String> params) throws UnsupportedEncodingException {
        // A PARTIR DE UN HASHMAP CLAU-VALOR, CREAM CLAU=VALOR1&CLAU2=VALOR2
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}