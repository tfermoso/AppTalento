package com.talentomobile.apptalento;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.talentomobile.apptalento.model.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private Button btnBuscar;
    private EditText edtCiudad;
    private TextView txtCiudad;
    private TextView txtRegion;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnBuscar = findViewById(R.id.btnBuscar);
        edtCiudad = findViewById(R.id.edtCiudad);
        txtCiudad = findViewById(R.id.txtCiudad);
        txtRegion = findViewById(R.id.txtRegion);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Datos", "pulsado buscar");
                if (edtCiudad.getText() != null) {
                    if (!edtCiudad.getText().toString().equals("")) {
                        Log.i("Datos", "Buscando..." + edtCiudad.getText().toString());
                        new ObtenerWebService().execute(edtCiudad.getText().toString());
                    }
                    else
                        Toast.makeText(getApplication(),"Introduce una Ciudad",Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(getApplication(),"Introduce una Ciudad",Toast.LENGTH_SHORT).show();
            }
        });

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


    }


    public class ObtenerWebService extends AsyncTask<String, Void, City> {

        @Override
        protected City doInBackground(String... params) {
            City city = new City();
            try {
                Log.i("Datos", "Haciendo petición a geonames");
                URL url = new URL("http://api.geonames.org/searchJSON?q=" + params[0] + "&maxRows=20&startRow=0&lang=en&isNameRequired=true&style=FULL&username=ilgeonamessample");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();
                if (respuesta == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader
                    // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                    // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                    // StringBuilder.
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);        // Paso toda la entrada al StringBuilder
                    }
                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    //Log.i("Datos","Json "+respuestaJSON.toString());
                    //Accedemos al vector de resultados
                    JSONArray resultJSON = respuestaJSON.getJSONArray("geonames");   // estado es el nombre del campo en el JSON
                    if (resultJSON != null) {

                        JSONObject respuestaJSON2 = new JSONObject(resultJSON.get(0).toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                        //Log.i("Datos","re "+respuestaJSON2.getString("asciiName"));
                        //Log.i("Datos","re "+respuestaJSON2.getString("countryCode"));
                        //Log.i("Datos","re "+respuestaJSON2.getString("lat"));
                        //Log.i("Datos","re "+respuestaJSON2.getString("lng"));
                        //Accedemos al vector de resultados

                        double dlat = Double.parseDouble(respuestaJSON2.getString("lat"));
                        double dlon = Double.parseDouble(respuestaJSON2.getString("lng"));
                        city.setName(respuestaJSON2.getString("asciiName"));
                        city.setCountry(respuestaJSON2.getString("countryCode"));
                        city.setLatitude(dlat);
                        city.setLongitude(dlon);
                        city.setBbox(respuestaJSON2.getString("bbox"));
                        Log.i("Datos", "name " + city.getName());
                        //Log.i("Datos",city.toString());
                    }
                    return city;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return city;
        }

        //connection.setHeader("content-type", "application/json");


        @Override
        protected void onCancelled(City s) {
            super.onCancelled(s);
        }

        @Override
        protected void onPostExecute(City c) {
            Log.i("Datos: ", c.getLatitude() + "," + c.getLongitude());
            // Add a markerand move the camera
            if (c.getName()!=null) {
                txtCiudad.setText(c.getName());
                txtRegion.setText(c.getCountry());
                LatLng ciudad = new LatLng(c.getLatitude(), c.getLongitude());
                Log.i("Datos", "ciudad " + ciudad.toString());
                mMap.addMarker(new MarkerOptions().position(ciudad).title(c.getName()));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(ciudad));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ciudad, 8));

            } else {
                Toast.makeText(getApplication(), "No se ha encontrado la ciudad", Toast.LENGTH_SHORT).show();
            }

            //super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("Datos", "Pidiendo datos");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
