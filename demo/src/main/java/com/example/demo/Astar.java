package com.example.demo;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.example.demo.HelloApplication;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.json.simple.JSONArray;

public class Astar {

    private Graph<String, String> estaciones = new DefaultUndirectedGraph<>(null, null, true);
    private Map<String, Double> heuristica = new HashMap<>(); /* Valor estimado desde cada nodo hasta el nodo destino */
    private String origen;
    private String destino;
    private JSONArray coords;
    private JSONArray aristas;

    public Astar(String origen, String destino, String peso) throws IOException, ParseException {
        this.origen = origen;
        this.destino = destino;
        JSONParser parser = new JSONParser();
        InputStream is =getClass().getResourceAsStream("/json/coordenadas.json");
        Reader rd = new InputStreamReader(is, "UTF-8");
        coords = (JSONArray) parser.parse(rd);
        is = getClass().getResourceAsStream("/json/aristas.json");
        rd = new InputStreamReader(is, "UTF-8");
        aristas = (JSONArray) parser.parse(rd);
        /* inicializar estructuras de datos */
        Set<String> repes = new HashSet<String>();
        for (int i = 0; i < aristas.size(); i++) {
            JSONObject jobj = (JSONObject) aristas.get(i);
            if (!repes.contains(jobj.get("Origen")) && repes.contains(jobj.get("Destino"))) {

                estaciones.addVertex((String) jobj.get("Origen"));
                repes.add((String) jobj.get("Origen"));

            }
            if (!repes.contains(jobj.get("Destino"))) {

                estaciones.addVertex((String) jobj.get("Destino"));
                repes.add((String) jobj.get("Destino"));

            }

            estaciones.addEdge((String) jobj.get("Origen"), (String) jobj.get("Destino"));
            estaciones.setEdgeWeight((String) jobj.get("Origen"), (String) jobj.get("Destino"),
                    (Double) jobj.get(peso));
        }

    }

    public void heuristica(String origenNombre) throws IllegalArgumentException {
        boolean encontrado = false;
        JSONObject origen = null;
        for (int i = 0; i < coords.size() && !encontrado; i++) {
            origen = (JSONObject) coords.get(i);
            encontrado = ((String) origen.get("Name")).equals(origenNombre);
        }
        if (origen == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < coords.size(); i++) {
            JSONObject destino = (JSONObject) coords.get(i);

            double dLat = Math.abs((double) destino.get("Latitude") - (double) origen.get("Latitud"));
            double dLon = Math.abs((double) destino.get("Longitude") - (double) origen.get("Longitude"));
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos((double) origen.get("Latitude"))
                            * Math.cos((double) destino.get("Latitude"));
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            heuristica.put((String) destino.get("Name"), 6371 * c);
        }
    }

    public String algoritmo() {
        Map<String, Double> visitados = new HashMap<>();
        Map<String, Double> listaAbierta = new HashMap<>();
        String camino = origen;
        Double pesoCamino = Double.MAX_VALUE;
        String nodo = origen;
        double value = heuristica.get(nodo);
        visitados.put(nodo, value);
        listaAbierta.put(camino, value);
        do {
            camino = menorCamino(listaAbierta.entrySet());
            pesoCamino = calcularPeso(camino);
            String[] caminoArray = camino.split(",");
            nodo = caminoArray[caminoArray.length - 1];
            actualizarEstructuras(visitados, listaAbierta, camino, pesoCamino);
        } while (!nodo.equals(destino));
        return camino;
    }

    public double calcularPeso(String res) {
        double peso = 0;
        String[] camino = res.split(",");
        for (int i = 0; i < camino.length - 1; i++) {
            String edge = camino[i] + camino[i + 1];
            peso += estaciones.getEdgeWeight(edge);
        }
        return peso;
    }

    private String menorCamino(Set<Entry<String, Double>> entrySet) {
        String menorCamino = "";
        Double menorValor = Double.MAX_VALUE;
        Iterator<Entry<String, Double>> it = entrySet.iterator();
        while (it.hasNext()) {
            Entry<String, Double> entry = it.next();
            if (menorValor > entry.getValue()) {
                menorValor = entry.getValue();
                menorCamino = entry.getKey();
            }
        }
        return menorCamino;
    }

    private void actualizarEstructuras(Map<String, Double> visitados, Map<String, Double> listaAbierta, String camino, Double peso) {
        String[] caminoArray = camino.split(",");
        String nodo = caminoArray[caminoArray.length - 1];
        Set<String> edges = estaciones.edgesOf(nodo);
        for (String edge : edges) {
            String nodoFuente = estaciones.getEdgeSource(edge);
            String nodoObjetivo = estaciones.getEdgeTarget(edge);
            nodoObjetivo = nodoObjetivo.equals(nodo) ? nodoFuente : nodoObjetivo;
            camino += "," + nodoObjetivo;
            peso += heuristica.get(nodoObjetivo);
            boolean contieneCamino = listaAbierta.containsKey(camino);
            if (contieneCamino && listaAbierta.get(camino) < peso) {
                listaAbierta.put(camino, peso);
            } else if (!contieneCamino) {
                listaAbierta.put(camino, peso);
            }
            visitados.put(nodoObjetivo,
                    peso + estaciones.getEdgeWeight(edge) + heuristica.get(nodoObjetivo.toString()));
        }
    }

    /* private boolean contieneCamino(Map<String, Double> listaAbierta, String camino) {
        Set<String> keySet = listaAbierta.keySet();
        Iterator<String> it = keySet.iterator();
        String[] caminoArray = camino.split(",");
        boolean encontrado = true;
        while (it.hasNext() && !encontrado) {
            String[] cArrayList = it.next().split(",");
            encontrado = true;
            for (int i = 0; i < cArrayList.length && encontrado; i++) {
                String nodo = caminoArray[i];
                encontrado = false;
                for (int j = 0; j < caminoArray.length && !encontrado; j++) {
                    encontrado = nodo == caminoArray[j];
                }
            }
        }
        return encontrado;
    } */

}
