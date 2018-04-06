package com.example.daniel.myapplicationguia5;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.graphics.Color;
        import android.support.v4.app.FragmentActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.SeekBar;
        import android.widget.Spinner;
        import android.widget.TextView;

        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.CameraPosition;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;

        import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    ArrayList<Place> places;

    Spinner spinnerMapType;
    SeekBar seekBarZoom;
    LatLng defaultLatLng = new LatLng(13.724776, -89.142497);

    private static final LatLng SALVADOR = new LatLng(13.718324, -89.140631);


    FollowPosition followPosition;

    private Marker mSalvador;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when
        //the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        seekBarZoom = (SeekBar) findViewById(R.id.seekBarZoom);

        //HAGA USO DEL ASISTENTE PARA CREAR setOnSeekBarChangeListener
        //El único método que modificará es onProgressChanged
        seekBarZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //CODIGO INTERIOR CREADO POR USTED
                chooseMoveCamera(mMap, defaultLatLng, progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        spinnerMapType = (Spinner) findViewById(R.id.spinnerMapType);

        //HAGA USO DEL ASISTENTE PARA CREAR setOnItemSelectedListener
        spinnerMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //CODIGO INTERIOR CREADO POR USTED
                String mapType = spinnerMapType.getSelectedItem().toString();
                if (mMap == null) return;

                if (mapType.equals("MAP_TYPE_NORMAL")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else if (mapType.equals("MAP_TYPE_SATELLITE")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (mapType.equals("MAP_TYPE_HYBRID")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                //FIN DE CODIGO INTERIOR CREADO POR USTED

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }


    //Broadcast Receiver.
    //Permanecerá escuchando por actualizaciones de FetchPlacesService
    // (Servicio que intentará descargar los datos)
    //HAGA USO DEL ASISTENTE PARA CREAR BroadcastReceiver
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("UDBTEST","Hay receive");

            //CÓDIGO INTERIOR CREADO POR USTED
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Log.d("UDBTEST","Si ha ingresado");
                places = (ArrayList<Place>) bundle.getSerializable(FetchPlacesService.RESULT);
                if (places != null && places.size() > 0) {
                    if (mMap != null) {
                        for (Place tmp : places) {
                            LatLng tmpLatLng =
                                    new LatLng(tmp.getLat(), tmp.getLon());
                            mMap.addMarker(new MarkerOptions().
                                    position(tmpLatLng).
                                    title(tmp.getPlaceName())
                            );
                        }
                    }
                }
            }



            //FIN DEL CÓDIGO INTERIOR CREADO POR USTED

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(FetchPlacesService.NOTIFICATION));
                /**/
        Intent intent = new Intent(this, FetchPlacesService.class);

        startService(intent);

        if (followPosition != null) {
            followPosition.register(MapsActivity.this);

        }

    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        if (followPosition != null)
            followPosition.unRegister(MapsActivity.this);
        super.onPause();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {



        mMap = googleMap;

        if(mMap != null){
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_ventana, null);


                    TextView tv_locality = (TextView) v.findViewById(R.id.tv_locality);
                    TextView tv_ship = (TextView) v.findViewById(R.id.tv_ship);

                    LatLng ll = marker.getPosition();
                    tv_locality.setText(marker.getTitle());
                    tv_ship.setText(marker.getSnippet());
                    return v;
                }
            });
        }


        mSalvador = mMap.addMarker(new MarkerOptions()
                .position(SALVADOR)
                .snippet("Fundación Salvador del Mundo")
                .title("Fusalmo")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.uno)));

        mSalvador.setTag(0);


        followPosition = new FollowPosition(this.mMap, MapsActivity.this);

        followPosition.register(MapsActivity.this);

        //Moveremos la cámara a la Universidad Don Bosco
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLatLng));
        chooseMoveCamera(mMap, defaultLatLng, 10);

        drawShapes();

    }


    //El siguiente método permitirá movernos de manera animada
    // a una posición del mapa
    private void chooseMoveCamera(GoogleMap googleMap, LatLng tmpLatLng, int zoom){
        CameraPosition cameraPosition =
                new CameraPosition.Builder().zoom(zoom).target(tmpLatLng).build();

        googleMap.animateCamera
                (CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    //El siguiente método custom permite agregar diferentes figuras
    private void drawShapes(){

        ShapesMap shapesMap = new ShapesMap(this.mMap);

        //PolyLines
        ArrayList<LatLng> lines = new ArrayList<>();

        lines.add(new LatLng (13.736888, -89.135783) );
        lines.add(new LatLng (13.735216, -89.131929) );
        lines.add(new LatLng (13.733720, -89.132465) );
        lines.add(new LatLng (13.734996, -89.136656) );
        lines.add(new LatLng (13.736764, -89.135719) );

        //Llamado al método custom drawLine de shapesMap
        shapesMap.drawLine(lines,5, Color.RED);

        ArrayList<LatLng> linesD = new ArrayList<>();


        ArrayList<LatLng> poligon = new ArrayList<>();
        poligon.add(new LatLng(13.744415, -89.168059));
        poligon.add(new LatLng(13.742148, -89.163989));
        poligon.add(new LatLng(13.741075, -89.167915));
        poligon.add(new LatLng(13.745635, -89.168235));


        //Transparencia
        //Valor Hexadecimal, transparencia + color
        //0x: Valor hexadecimal
        //2F: Trasparencia
        //00FF00: Color Hexadecimal
        shapesMap.drawPoligon(poligon,5,Color.GREEN,0x2F00FF00);



        //Agregando Circulo

        LatLng circlePoint = new LatLng(13.718512, -89.167180);
        shapesMap.drawCircle(circlePoint,100,Color.BLUE,2,Color.TRANSPARENT);



  }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}


