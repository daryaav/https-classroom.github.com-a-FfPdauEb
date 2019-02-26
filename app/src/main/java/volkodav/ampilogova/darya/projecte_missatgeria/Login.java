package volkodav.ampilogova.darya.projecte_missatgeria;

import android.app.Activity;
import android.content.Context;
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
    private ComprovarXarxa comprovacio;
    private ValidacioUsuari usuari;
    private Button boto;
    private EditText nom, password;
    private Preferencies preferencies;
    private JSONObject js;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferencies = new Preferencies(this);


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
                // I LA SEVA CONTRASENYA
                String resultat = usuari.cridadaPost("http://iesmantpc.000webhostapp.com/public/login/", hashMap);
                boolean r = false;
                try {
                    js = new JSONObject(resultat);
                    r = js.getBoolean("correcta");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (r) {
                    preferencies.setUser(txt_nom);
                    preferencies.setPassword(txt_password);
                    i.putExtra("resultat", resultat);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Login incorrecte", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}