package com.example.daniel.myapplicationguia5;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by Daniel on 17/02/2018.
 */

//Implementa IntentService.
//Tome en cuenta que debe implementar el método onHandleIntent
public class FetchPlacesService extends IntentService {

    //Hará las veces de identificador
    public static String NOTIFICATION = "udb.edu.sv.dasguia03";

    public static String RESULT = "dataResult";

    private ArrayList<Place> result = new ArrayList();



    //Un identificador de clase.
    //se pasa como parámetro en súper.

    public FetchPlacesService() {
        super("fetchplaces");
    }



    //Se ejecuta al iniciar el servicio y deja los datos preparados
    // para ser tomados con BroadCastReceiver
    @Override
    protected void onHandleIntent(Intent intent) {

        //Llenaremos el ArrayList con nuestros datos.
        result.add(new Place("Finca",13.738905, -89.134183));
        result.add(new Place("Mi casa", 13.724766, -89.142497));


        publishData();

    }

    //Método custom que se encargará de publicar los datos
    //para que sean capturados por MapsActivity
    public void publishData(){

        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}


