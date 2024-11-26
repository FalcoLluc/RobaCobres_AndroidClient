package com.example.robacobres_androidclient.services;

import android.content.Context;
import android.util.Log;

import com.example.robacobres_androidclient.callbacks.AuthCallback;
import com.example.robacobres_androidclient.callbacks.ItemCallback;
import com.example.robacobres_androidclient.callbacks.UserCallback;
import com.example.robacobres_androidclient.interceptors.AddCookiesInterceptor;
import com.example.robacobres_androidclient.interceptors.ReceivedCookiesInterceptor;
import com.example.robacobres_androidclient.models.Item;
import com.example.robacobres_androidclient.models.User;

import java.util.List;
import java.util.HashSet;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service {
    private static Service instance;
    private Retrofit retrofit;
    private Servidor serv;

    // Private constructor to prevent instantiation from other classes
    private Service(Context context) {

        // Interceptor para loggear las peticiones HTTP (útil para depuración)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Cliente OkHttp con interceptores de cookies
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AddCookiesInterceptor(context))       // Interceptor para agregar cookies en las solicitudes
                .addInterceptor(new ReceivedCookiesInterceptor(context))  // Interceptor para recibir y almacenar cookies
                .addInterceptor(loggingInterceptor)                      // Interceptor para log
                .build();

        // Configurar Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/RobaCobres/") // Base URL
                .addConverterFactory(GsonConverterFactory.create())       // Conversor JSON
                .client(client)                                           // Cliente OkHttp
                .build();

        serv = retrofit.create(Servidor.class);
    }

    // Método público para obtener la única instancia del Singleton Service
    public static Service getInstance(Context context) {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new Service(context); // Crear la instancia si es nula
                }
            }
        }
        return instance;
    }

    public void registerUser(String _username, String _password, final UserCallback callback) {
        User body = new User(_username, _password);
        Call<User> call = serv.registerUser(body);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u = response.body();
                if (response.code() == 201) {
                    callback.onLoginOK(u);
                    callback.onMessage("CONGRATULATIONS, " + u.getName() + " YOU ARE REGISTERED");
                } else if (response.code() == 501) {
                    callback.onMessage("USERNAME ALREADY USED");
                    Log.d("API_RESPONSE", "USERNAMEUSED");
                } else {
                    Log.d("API_RESPONSE", "ERROR");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
                callback.onMessage("ERROR DUE TO CONNECTION");
            }
        });
    }

    public void loginUser(String _username, String _password, final UserCallback callback) {
        User body = new User(_username, _password);
        Call<Void> call = serv.loginUser(body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201) {
                    callback.onLoginOK(body);
                    Log.d("API_RESPONSE", "POST SUCCESSFUL");
                } else if (response.code() == 501 || response.code() == 502) {
                    callback.onMessage("USER OR PASSWORD WRONG");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                } else {
                    callback.onMessage("ERROR");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
                callback.onMessage("ERROR DUE TO CONNECTION");
                callback.onLoginERROR();
            }
        });
    }

    public void deleteUser(String _id) {
        Call<Void> call = serv.deleteUser(_id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API_RESPONSE", "DELETE SUCCESSFUL");
                } else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void getAllItems(final ItemCallback callback) {
        Call<List<Item>> call = serv.getItems();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Item> items = response.body();
                    callback.onItemCallback(items);
                    for (Item it : items) {
                        Log.d("API_RESPONSE", "Item Name: " + it.getName() + " Item Price: " + it.getCost());
                    }
                } else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void getItem(String _id) {
        Call<Item> call = serv.getItem(_id);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Item i = response.body();
                    Log.d("API_RESPONSE", "Item Name: " + i.getName());
                } else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void getSession(final AuthCallback callback) {
        Call<Void> call = serv.getSession();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code()==201){
                    callback.onAuthorized();
                    Log.d("API_RESPONSE", "AUTHORIZED" );
                } else {
                    Log.d("API_RESPONSE", "Response not successful, code: ");
                    callback.onUnauthorized();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void quitSession(final AuthCallback callback) {
        Call<Void> call = serv.quitSession();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code()==201){
                    Log.d("API_RESPONSE", "201: COOKIE QUITED" );
                    callback.onUnauthorized();
                } else {
                    Log.d("API_RESPONSE", "Response not successful, code: "+response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void userBuys(String _username, String _idItem) {
        // FALTEN ELS CALLBACKS PER ESCRIURE A LA PANTALLA
        Call<Void> call = serv.userBuys(_username, _idItem);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201) {
                    Log.d("API_RESPONSE", "Item Comprado: " + _idItem);
                }
                else if (response.code() == 501){
                    Log.d("API_RESPONSE", "User NOT found ");
                }
                else if(response.code() == 502){
                    Log.d("API_RESPONSE", "Item NOT available");
                }
                else if (response.code() == 503) {
                    Log.d("API_RESPONSE", "Not enough money");
                } else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

}


