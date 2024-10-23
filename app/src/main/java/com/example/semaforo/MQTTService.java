package com.example.semaforo;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class MQTTService {

    private final Context context;
    private final MqttAndroidClient client;

    public MQTTService(Context context, String serverURI, TextView textViewTemperatura, TextView textViewUmidade) {
        this.context = context;
        this.client = new MqttAndroidClient(context, serverURI, MqttClient.generateClientId());

        this.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                // Converte o payload em uma string
                String payload = new String(message.getPayload());

                // Cria um objeto JSON a partir do payload
                JSONObject jsonObject = new JSONObject(payload);

                // Extrai os valores de temperatura e umidade
                double temperature = jsonObject.getDouble("temperature");
                double humidity = jsonObject.getDouble("humidity");

                String formattedTemperature = String.format("%.2f", temperature);
                String formattedHumidity = String.format("%.2f", humidity);

                textViewTemperatura.setText("Temperatura: " + formattedTemperature + "°C");
                textViewUmidade.setText("Umidade: " + formattedHumidity + "%");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void connect() {

        Context self = this.context;

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(Constantes.USER_MQTT);
        options.setPassword(Constantes.PASSWORD_MQTT.toCharArray());

        try {
            this.client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(self, "Conexão OK", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(self, "Falha de Conexão " + exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            Toast.makeText(self, "Erro de Conexão " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void disconnect() {
        try {
            this.client.disconnect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(context, "Conexao encerrada com MQTT", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(context, "Falha ao desconectar MQTT " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            Toast.makeText(context, "Error ao desconectar MQTT " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void publish(String topico) {
        Context self = this.context;

        try {
            MqttMessage message = new MqttMessage();
            message.setQos(Constantes.QOS_MQTT);
            message.setRetained(Constantes.RETAINED_MQTT);
            this.client.publish(topico, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(self, "Publicação realizada com sucesso", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(self, "Falha na publicação", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            Toast.makeText(self, "Erro ao publicar topico " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void subscribe(String topico) {
        Context self = this.context;

        try {
            this.client.subscribe(topico, Constantes.QOS_MQTT, self, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(self, "Tópico temperatura conectado", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(self, "Falha ao conectar tópico temperatura", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            Toast.makeText(self, "Erro ao conectar topico temperatura " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
