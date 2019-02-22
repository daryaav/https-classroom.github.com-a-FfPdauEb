package volkodav.ampilogova.darya.projecte_missatgeria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ComprovarXarxa extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // ACTUALITZAR L'ESTAT DEL LA XARXA
        ActualitzaEstatXarxa(arg0);
    }

    public void ActualitzaEstatXarxa(Context context) {
        // OBTENIM UN GESTOR DE LES CONNEXIONS DE LA XARXA
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // OBTENIM L'ESTAT DEL LA XARXA
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // SI ESTÀ CONNECTAT...
        if (networkInfo != null && networkInfo.isConnected()) {
            // LA XARXA ENVIARÀ EL MISSATGE DE XARXA OK
            Toast.makeText(context, "Xarxa ok", Toast.LENGTH_LONG).show();
        } else {
            // SI NO ESTÀ CONNECTAT, LA XARXA ENVIARÀ UN MISSATGE DE QUE NO ESTÀ DISPONIBLE
            Toast.makeText(context, "Xarxa no disponible", Toast.LENGTH_LONG).show();
        }

        //OBTENIM L'ESTAT DE LA XARXA MÒBIL
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean connectat3G = networkInfo.isConnected();

        // OBTENIM L'ESTAT DE LA XARXA WIFI
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean connectatWifi = networkInfo.isConnected();
    }
}