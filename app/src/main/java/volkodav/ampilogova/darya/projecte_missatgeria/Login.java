package volkodav.ampilogova.darya.projecte_missatgeria;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class Login extends AppCompatActivity {
    private ValidacioUsuari usuari;
    private Button boto;
    private EditText nom, password;
    private Preferencies preferencies;
    private JSONObject js;
    private String codUsuari, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // CREEM UN OBJECTE DE TIPUS PREFERÈNCIES, I LI INTRODÜIM UN CONTEXTE COM A PARÀMETRE
        preferencies = new Preferencies(this);

        // QUAN CLIQUEM EL BOTÓ DE LOGIN, FAREM LA COMPROVACIÓ DEL USUARI
        boto = findViewById(R.id.boto_login);
        boto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                nom = findViewById(R.id.text_nom);
                password = findViewById(R.id.text_password);
                String txt_nom = nom.getText().toString();
                String txt_password = password.getText().toString();
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("nom", txt_nom);
                hashMap.put("password", txt_password);

                // LI AFEGIM LA URL QUE HEM DE MESTER I EL HASMAP AMB LES DADES DEL NOM DEL USUARI
                // I LA SEVA CONTRASENYA. TAMBÉ LI PASSEM UN TOKEN BUIT.
                String resultat = usuari.cridadaPost("http://iesmantpc.000webhostapp.com/public/login/", hashMap, "");
                boolean r = false;

                try {
                    // CREAM UN OBJECTE JSON , PASSANT-LI LA RUTA DEL SERVIDOR
                    js = new JSONObject(resultat);

                    // CREAM UN ALTRE OBJECTE JSON, PER PODER AGAFAR LES DADES DEL CODIUSUARI I EL TOKEN
                    JSONObject dades = js.getJSONObject("dades");
                    codUsuari = dades.getString("codiusuari");
                    token = dades.getString("token");

                    // AGAFAM LA RESPOSTA DEL SERVIDOR, A LA HORA DE COMPROVAR EL LOGIN. ENS HAURÀ DE
                    // DONAR TRUE O FALSE
                    r = js.getBoolean("correcta");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // SI EL USUARI ÉS CORRECTE, AGAFEM EL MISSATGE QUE ENS INDICA QUE EL LOGIN
                // ÉS CORRECTE
                if (r) {
                    preferencies.setUser(txt_nom);
                    preferencies.setPassword(txt_password);
                    preferencies.setCodiusuari(Integer.valueOf(codUsuari));
                    preferencies.setToken(token);

                    i.putExtra("resultat", resultat);
                    setResult(Activity.RESULT_OK, i);
                    finish();

                // SI EL USUARI NO ÉS CORRECTE, ENVIEM UN MISSATGE DE ERROR
                } else {
                    Toast.makeText(getBaseContext(), "Login incorrecte", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}