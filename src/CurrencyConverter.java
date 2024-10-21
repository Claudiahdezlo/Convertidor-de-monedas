import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class CurrencyConverter {
    private static final String API_KEY = "e255af0d1b79702d8f4b6f4d";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/e255af0d1b79702d8f4b6f4d/latest/USD";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("Bienvenido al convertidor de moneda (:");
            displayMenu();
            System.out.print("Elija su opción: ");
            option = scanner.nextInt();

            if (option >= 1 && option <= 6) {
                System.out.print("Escriba el valor a convertir: ");
                double value = scanner.nextDouble();
                try {
                    performConversion(option, value);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if (option != 7) {
                System.out.println("Opción inválida. Por favor inténtelo de nuevo.");
            }

            System.out.println();
        } while (option != 7);

        System.out.println("Gracias por usar el convertidor de moneda. Adiós!");
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("1) Euro (EUR) => Dólar (USD)");
        System.out.println("2) Dólar (USD) => Euro (EUR)");
        System.out.println("3) Euro (EUR) => Libra (GBP)");
        System.out.println("4) Libra (GBP) => Euro (EUR)");
        System.out.println("5) Euro (EUR) => Yen (JPY)");
        System.out.println("6) Yen (JPY) => Euro (EUR)");
        System.out.println("7) Salir");
        System.out.println("*************************************");
    }

    private static void performConversion(int option, double value) throws Exception {
        String fromCurrency, toCurrency;

        switch (option) {
            case 1:
                fromCurrency = "EUR";
                toCurrency = "USD";
                break;
            case 2:
                fromCurrency = "USD";
                toCurrency = "EUR";
                break;
            case 3:
                fromCurrency = "EUR";
                toCurrency = "GBP";
                break;
            case 4:
                fromCurrency = "GBP";
                toCurrency = "EUR";
                break;
            case 5:
                fromCurrency = "EUR";
                toCurrency = "JPY";
                break;
            case 6:
                fromCurrency = "JPY";
                toCurrency = "EUR";
                break;
            default:
                throw new IllegalArgumentException("Opción Inválida");
        }

        double exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        double convertedValue = value * exchangeRate;

        System.out.printf("%.2f %s = %.2f %s%n", value, fromCurrency, convertedValue, toCurrency);
    }

    private static double getExchangeRate(String fromCurrency, String toCurrency) throws Exception {
        String url = API_URL + "?access_key=" + API_KEY + "&symbols=" + toCurrency;

        try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.has("result") && jsonResponse.getString("result").equals("success")) {
                    JSONObject rates = jsonResponse.getJSONObject("conversion_rates");
                    double exchangeRate = rates.getDouble(toCurrency);
                    return fromCurrency.equals("EUR") ? exchangeRate : 1 / exchangeRate;
                } else {
                    throw new Exception("Error fetching exchange rate: " + jsonResponse.getString("error-type"));
                }
            } else {
                throw new Exception("Error al conectarse a la API. Código de respuesta: " + responseCode);
            }
        } catch (MalformedURLException e) {
            throw new Exception("URL de API inválida: " + e.getMessage());
        } catch (IOException e) {
            throw new Exception("Error al conectarse a la API: " + e.getMessage());
        } catch (JSONException e) {
            throw new Exception("Error de respuesta de la API: " + e.getMessage());
        }
    }
}