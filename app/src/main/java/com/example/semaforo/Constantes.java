package com.example.semaforo;

public class Constantes {
    public static final String USER_MQTT = "";
    public static final String PASSWORD_MQTT = "";
    public static final int QOS_MQTT = 1;
    public static final boolean RETAINED_MQTT = false;

    public static final String MQTT_TOPIC_SEMAFORO_LIGAR = "semaforo/ligar";
    public static final String MQTT_TOPIC_SEMAFORO_DESLIGAR = "semaforo/desligar";
    public static final String MQTT_TOPIC_LER_TEMPERATURA = "temperatura/ler";
    public static final String MQTT_TOPIC_ENVIAR_TEMPERATURA = "temperatura/enviar";
}
