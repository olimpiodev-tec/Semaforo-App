package com.example.semaforo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MQTTService client;

    private Button buttonLedVermelho;
    private Button buttonLedAmarelo;
    private Button buttonLedVerde;

    private SwitchCompat switchStatusSemaforo;

    private TextView textViewTemperatura;
    private TextView textViewUmidade;

    int subscribeTopicTemperature = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonLedVermelho = findViewById(R.id.buttonLedVermelho);
        buttonLedAmarelo = findViewById(R.id.buttonLedAmarelo);
        buttonLedVerde = findViewById(R.id.buttonLedVerde);

        switchStatusSemaforo = findViewById(R.id.switchStatusSemaforo);
        ImageButton imageButtonTemperature = findViewById(R.id.imageButtonTemperature);
        imageButtonTemperature.setOnClickListener(this);

        textViewTemperatura = findViewById(R.id.textViewTemperatura);
        textViewUmidade = findViewById(R.id.textViewUmidade);

        conectarMQTT();
        eventsSwitch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.disconnect();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButtonTemperature) {
            textViewTemperatura.setText("Temperatura: ");
            textViewUmidade.setText("Umidade: ");

            MainActivity.this.client.publish(Constantes.MQTT_TOPIC_LER_TEMPERATURA);

            if (subscribeTopicTemperature == 0) {
                this.client.subscribe(Constantes.MQTT_TOPIC_ENVIAR_TEMPERATURA);
                subscribeTopicTemperature = 1;
            }
        }
    }

    private void conectarMQTT() {
        String hostname = "broker.hivemq.com";
        String port = "1883";
        String serverURI = "tcp://".concat(hostname).concat(":").concat(port);
        this.client = new MQTTService(this, serverURI, textViewTemperatura, textViewUmidade);
        client.connect();
    }

    private void eventsSwitch() {

        switchStatusSemaforo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                   MainActivity.this.client.publish(Constantes.MQTT_TOPIC_SEMAFORO_LIGAR);
                   ligarBotoes();
                } else {
                    MainActivity.this.client.publish(Constantes.MQTT_TOPIC_SEMAFORO_DESLIGAR);
                    desligarBotoes();
                }
            }
        });
    }

    private void ligarBotoes() {
        buttonLedVermelho.setBackgroundTintList(getColorStateList(R.color.vermelho));
        buttonLedAmarelo.setBackgroundTintList(getColorStateList(R.color.amarelo));
        buttonLedVerde.setBackgroundTintList(getColorStateList(R.color.verde));
    }

    private void desligarBotoes() {
        buttonLedVermelho.setBackgroundTintList(getColorStateList(R.color.cinza));
        buttonLedAmarelo.setBackgroundTintList(getColorStateList(R.color.cinza));
        buttonLedVerde.setBackgroundTintList(getColorStateList(R.color.cinza));
    }
}