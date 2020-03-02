package rtvtr.inspiringlab.com.np.rtvtr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;


import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import tech.gusavila92.websocketclient.WebSocketClient;

import static android.provider.CalendarContract.CalendarCache.URI;

public class MainActivity extends AppCompatActivity {

    private WebSocketClient webSocketClient;
    private Context context;
    private URI uri;
    String socketUri = "ws://192.168.0.118:5809/detections";
    String imageUrl = "http://192.168.0.118:5809/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createWebsocketClient();
    }

    private void createWebsocketClient() {
        try {
            // Connect to local host
            uri = new URI(socketUri);
//            imageURI = new URI("http://192.168.0.118:5809");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
//                webSocketClient.send("Hello World!");
                sendMessage();
            }
            @Override
            public void onTextReceived(String s) {
                Log.i("WebSocket", "Message received");
                final String message = s;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject reader = new JSONObject(message);

//                            JSONObject predicted  = reader.getJSONObject("predicted");

                            String predicted_plate = reader.getString("predicted");
                            String vehicle_type = reader.getString("vehicle_type");
                            String vehicle_image = reader.getString("vehicle_image");
                            String plate_image = reader.getString("plate_image");
                            TextView vehicleType = findViewById(R.id.tv_vehicle_type);
                            vehicleType.setText(vehicle_type);
                            TextView licensePlate= findViewById(R.id.tv_license_plate);
                            licensePlate.setText(predicted_plate);
                            ImageView vehicleImage = findViewById(R.id.iv_vehicle_type);
                            ImageView plateImage =findViewById(R.id.iv_license_plate);
                            Picasso.get().load(imageUrl+vehicle_image).into(vehicleImage);
                            Picasso.get().load(imageUrl+plate_image).into(plateImage);




                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }
            @Override
            public void onPingReceived(byte[] data) {
            }
            @Override
            public void onPongReceived(byte[] data) {
            }
            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }
            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                System.out.println("onCloseReceived");
            }
        };
        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }
    public void sendMessage() {
        Log.i("WebSocket", "Button was clicked");
        String message = "{\"client_id\":\"baal ho\", \"post_id\":1}";
        webSocketClient.send(message);

    }
}
