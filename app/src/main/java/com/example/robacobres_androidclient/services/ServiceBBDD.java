package com.example.robacobres_androidclient.services;

import android.content.Context;
import android.util.Log;

import com.example.robacobres_androidclient.callbacks.AuthCallback;
import com.example.robacobres_androidclient.callbacks.CharacterCallback;
import com.example.robacobres_androidclient.callbacks.ChargeDataCallback;
import com.example.robacobres_androidclient.callbacks.ForumCallback;
import com.example.robacobres_androidclient.callbacks.InsigniasCallback;
import com.example.robacobres_androidclient.callbacks.ItemCallback;
import com.example.robacobres_androidclient.callbacks.PartidasCallback;
import com.example.robacobres_androidclient.callbacks.PrivateCallback;
import com.example.robacobres_androidclient.callbacks.UnityCallback;
import com.example.robacobres_androidclient.callbacks.UserCallback;
import com.example.robacobres_androidclient.callbacks.VideoCallback;
import com.example.robacobres_androidclient.interceptors.AddCookiesInterceptor;
import com.example.robacobres_androidclient.interceptors.ReceivedCookiesInterceptor;
import com.example.robacobres_androidclient.models.ChangePassword;
import com.example.robacobres_androidclient.models.ChatIndividual;
import com.example.robacobres_androidclient.models.Forum;
import com.example.robacobres_androidclient.models.GameCharacter;
import com.example.robacobres_androidclient.models.Insignia;
import com.example.robacobres_androidclient.models.Item;
import com.example.robacobres_androidclient.models.Ranking;
import com.example.robacobres_androidclient.models.User;
import com.example.robacobres_androidclient.models.Video;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBBDD {
    private static ServiceBBDD instance;
    private Retrofit retrofit;
    private ServidorBBDD serv;

    // Private constructor to prevent instantiation from other classes
    private ServiceBBDD(Context context) {

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
                .baseUrl("http://10.0.2.2:8080/RobaCobres/")
                //.baseUrl("http://147.83.7.204/RobaCobres/") // LOCAL Base URL
                .addConverterFactory(GsonConverterFactory.create())       // Conversor JSON
                .client(client)                                           // Cliente OkHttp
                .build();

        serv = retrofit.create(ServidorBBDD.class);
    }

    // Método público para obtener la única instancia del Singleton Service
    public static ServiceBBDD getInstance(Context context) {
        if (instance == null) {
            synchronized (ServiceBBDD.class) {
                if (instance == null) {
                    instance = new ServiceBBDD(context); // Crear la instancia si es nula
                }
            }
        }
        return instance;
    }

    public void registerUser(String _username, String _password, String _mail, final UserCallback callback) {
        User body = new User(_username, _password, _mail);
        Call<User> call = serv.registerUser(body);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u = response.body();
                if (response.code() == 201) {
                    callback.onCorrectProcess();
                    callback.onMessage("CONGRATULATIONS, " + u.getName() + " YOU ARE REGISTERED");
                } else if (response.code() == 501) {
                    callback.onMessage("USERNAME OR EMAIL ALREADY USED");
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

    public void getUser(final UserCallback callback){
        Call<User> call = serv.GetStatsUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 201) {
                    callback.onUserLoaded(response.body()); // Retornem l'usuari via el callback
                } else if (response.code() == 506) {
                    callback.onMessage("User Not Yet Logged");
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

    public void deleteUser(final UserCallback callback, final AuthCallback callback1) {
        Call<Void> call = serv.deleteUser();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API_RESPONSE", "DELETE SUCCESSFUL");
                    callback.onDeleteUser();
                    callback1.onUnauthorized();
                } else {
                    callback.onMessage("No se ha podido borrar la cuenta");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void RecoverPassword(String user, final UserCallback userCallback){
        Call<Void> call = serv.RecoverPassword(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201) {
                    userCallback.onMessage("CHECK YOUR MAIL");
                    userCallback.onCorrectProcess();
                    Log.d("API_RESPONSE", "POST SUCCESSFUL");
                } else if (response.code() == 500) {
                    userCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }else if (response.code() == 501) {
                        userCallback.onMessage("USER DOES NOT EXIST");
                        Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                } else {
                    userCallback.onMessage("ERROR SENDING MAIL");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void UserChangePassword(ChangePassword passwords, final UserCallback userCallback){
        Call<Void> call = serv.UserChangePassword(passwords);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201) {
                    userCallback.onMessage("PASSWORD CHANGED");
                    userCallback.onCorrectProcess();
                    Log.d("API_RESPONSE", "PUT SUCCESSFUL");
                } else if (response.code() == 500) {
                    userCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                } else {
                    userCallback.onMessage("ACTUAL PASSWORD INCORRECT");
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

    public void getItemssUserCanBuy(final ItemCallback callback) {
        Call<List<Item>> call = serv.getItemssUserCanBuy();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.code() == 201) {
                    List<Item> items = response.body();
                    callback.onItemCallback(items);
                    for (Item it : items) {
                        Log.d("API_RESPONSE", "Item Name: " + it.getName() + " Item Price: " + it.getCost());
                    }
                }
                else if (response.code() == 500){
                    Log.d("API_RESPONSE", "ERROR ");
                    callback.onError("Error");
                }

                else if (response.code() == 505) {
                    Log.d("API_RESPONSE", "Not more items to buy");
                    callback.onError("Has comprado todos los items de la tienda!");

                }
                else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                    callback.onError("Error "+response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
                callback.onError("ERROR WITH CONNECTION");
            }
        });
    }


    public void getCharactersUserCanBuy(final CharacterCallback callback) {
        //@Path("NameUser") String NameUser
        Call<List<GameCharacter>> call = serv.getCharactersUserCanBuy();
        call.enqueue(new Callback<List<GameCharacter>>() {
            @Override
            public void onResponse(Call<List<GameCharacter>> call, Response<List<GameCharacter>> response) {
                if (response.code() == 201) {
                    List<GameCharacter> Charactes = response.body();
                    callback.onCharacterCallback(Charactes);
                    for (GameCharacter it : Charactes) {
                        Log.d("API_RESPONSE", "Character Name: " + it.getName() + " Character Price: " + it.getCost());
                    }
                }
                else if (response.code() == 500){
                    Log.d("API_RESPONSE", "ERROR ");
                    callback.onError("Error");
                }

                else if (response.code() == 501) {
                    Log.d("API_RESPONSE", "user not found");
                    callback.onError("ATTENTION USER NOT FOUND!");
                }
                else if (response.code() == 502) {
                    Log.d("API_RESPONSE", "Not enough money");
                    callback.onError("ERES POBRE!");
                }
                else if (response.code() == 506) {
                    Log.d("API_RESPONSE", "User not logged in yet");
                    callback.onError("User not logged in yet!");
                }
                else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                    callback.onError("Error "+response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GameCharacter>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
                callback.onError("ERROR WITH CONNECTION");
            }
        });
    }

    public void getMyItems(final ItemCallback callback) {
        Call<List<Item>> call = serv.getMyItems();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.code() == 201) {
                    List<Item> items = response.body();
                    callback.onItemCallback(items);
                    for (Item it : items) {
                        Log.d("API_RESPONSE", "Item Name: " + it.getName());
                    }
                }
                else if (response.code() == 501){
                    Log.d("API_RESPONSE", "ERROR USER NOT FOUND ");
                    callback.onError("Error User Not Found");
                }
                else if (response.code() == 502) {
                    Log.d("API_RESPONSE", "No Items");
                    callback.onError("No items");
                }
                else if (response.code() == 506) {
                    Log.d("API_RESPONSE", "User not logged in yet");
                    callback.onError("User not logged in yet");
                }
                else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                    callback.onError("Error "+response.code());
                }

            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
                callback.onError("ERROR WITH CONNECTION");
            }
        });
    }

    public void getMyCharacters(final CharacterCallback callback) {
        Call<List<GameCharacter>> call = serv.getMyCharacters();
        call.enqueue(new Callback<List<GameCharacter>>() {
            @Override
            public void onResponse(Call<List<GameCharacter>> call, Response<List<GameCharacter>> response) {
                if (response.code() == 201) {
                    List<GameCharacter> characters = response.body();
                    callback.onCharacterCallback(characters);
                    for (GameCharacter g : characters) {
                        Log.d("API_RESPONSE", "Character Name: " + g.getName());
                    }
                }
                else if (response.code() == 501){
                    Log.d("API_RESPONSE", "ERROR USER NOT FOUND ");
                    callback.onError("Error User Not Found");
                }
                else if (response.code() == 502) {
                    Log.d("API_RESPONSE", "No Items");
                    callback.onError("No characters");
                }
                else if (response.code() == 506) {
                    Log.d("API_RESPONSE", "User not logged in yet");
                    callback.onError("User not logged in yet");
                }
                else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                    callback.onError("Error "+response.code());
                }

            }

            @Override
            public void onFailure(Call<List<GameCharacter>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
                callback.onError("ERROR WITH CONNECTION");
            }
        });
    }

    public void getItem(String itemName) {
        Call<Item> call = serv.getItem(itemName);
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

    public void userBuysItem(String itemName, final ItemCallback callback) {
        Call<List<Item>> call = serv.userBuysItem(itemName);
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.code() == 201) {
                    Log.d("API_RESPONSE", "Item Comprado: " + itemName);
                    callback.onPurchaseOk(itemName);
                    //Actualitza la llista amb els objectes que no te comprats
                    List<Item> itemsnotbought = response.body();
                    callback.onItemCallback(itemsnotbought);
                }
                else if (response.code() == 501){
                    Log.d("API_RESPONSE", "User NOT found ");
                }
                else if(response.code() == 502){
                    Log.d("API_RESPONSE", "Item NOT available");
                }
                else if (response.code() == 503) {
                    Log.d("API_RESPONSE", "Not enough money");
                    callback.onError("Ahorra un poco fuckin pobre!");
                }
                else if (response.code() == 505) {
                    Log.d("API_RESPONSE", "Not more items to buy");
                    callback.onError("Has comprado todos los items de la tienda!");

                }
                else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void userBuysCharacter(String characterName, final CharacterCallback callback) {
        Call<List<GameCharacter>> call = serv.userBuysCharacter(characterName);
        call.enqueue(new Callback<List<GameCharacter>>() {
            @Override
            public void onResponse(Call<List<GameCharacter>> call, Response<List<GameCharacter>> response) {
                if (response.code() == 201) {
                    Log.d("API_RESPONSE", "Item Comprado: " + characterName);
                    callback.onPurchaseOk(characterName);
                    //Actualitza la llista amb els objectes que no te comprats
                    List<GameCharacter> itemsnotbought = response.body();
                    callback.onCharacterCallback(itemsnotbought);
                }
                else if (response.code() == 501){
                    Log.d("API_RESPONSE", "User NOT found ");
                }
                else if(response.code() == 502){
                    Log.d("API_RESPONSE", "Character NOT available");
                }
                else if (response.code() == 503) {
                    Log.d("API_RESPONSE", "Not enough money");
                    callback.onError("Ahorra un poco, pobre!");
                }
                else if (response.code() == 506) {
                    Log.d("API_RESPONSE", "User not logged in yet");
                    callback.onError("LOGUEATE!");
                }
                else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<GameCharacter>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void UserGetsMultiplicador(final ChargeDataCallback callback) {
        Call<String> call = serv.UserGetsMultiplicador();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String factormulti = response.body();
                    callback.onChargeFactorM(factormulti);
                    Log.d("API_RESPONSE", "Factor multiplicador: " + factormulti);
                } else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }
    public void GetStatsUser(final ChargeDataCallback callback) {
        Call<User> call = serv.GetStatsUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    callback.onChargeUser(user);
                    Log.d("API_RESPONSE", "El usuario es: " + user.toString());
                } else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }
    public void UserSellsCobre(double kiloscobre, final ChargeDataCallback callback){
        Call<User> call = serv.UserSellsCobre(kiloscobre);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 201) {
                    callback.onMessage("Purchase successful!");
                    User user = response.body();
                    callback.onChargeUser(user);
                    Log.d("API_RESPONSE", "PURCHASE SUCCESSFUL");
                } else if (response.code() == 500) {
                    callback.onMessage("You want to sell more cobre than you have!");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }else if (response.code() == 503){
                    Log.d("API_RESPONSE", "Multiplicador Not Found");
                }
                else if(response.code() == 502){
                    callback.onMessage("Please enter a number greater of 0Kg!");
                    Log.d("API_RESPONSE", "User wants to sell 0 cobre");

                } else {
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void getCode(final UserCallback userCallback){
        Call<Void> call = serv.getCode();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201) {
                    userCallback.onMessage("CHECK YOUR MAIL");
                    Log.d("API_RESPONSE", "POST SUCCESSFUL");
                } else if (response.code() == 502) {
                    userCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }else if (response.code() == 506) {
                    userCallback.onMessage("USER NOT LOGGED IN");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void changeCorreo(String correo, String code,final UserCallback userCallback){
        Call<Void> call = serv.UserChangeCorreo(correo,code);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201) {
                    userCallback.onMessage("EMAIL CHANGED");
                    userCallback.onCorrectProcess();
                    Log.d("API_RESPONSE", "POST SUCCESSFUL");
                } else if (response.code() == 503) {
                    userCallback.onMessage("WRONG CODE");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }else if (response.code() == 506) {
                    userCallback.onMessage("USER NOT LOGGED IN");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
                else {
                    userCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void getForum(final ForumCallback forumCallback) {
        Call<List<Forum>> call = serv.getForum();
        call.enqueue(new Callback<List<Forum>>() {
            @Override
            public void onResponse(Call<List<Forum>> call, Response<List<Forum>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Forum> forums = response.body();
                    forumCallback.onForumCallback(forums);
                    Log.d("API_RESPONSE", "GET SUCCESSFUL");
                } else if (response.code() == 502) {
                    forumCallback.onMessage("No Forum Messages");
                    List<Forum> list = new ArrayList<>();
                    forumCallback.onForumCallback(list);
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }else if (response.code() == 506) {
                    forumCallback.onMessage("USER NOT LOGGED IN");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
                else {
                    forumCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Forum>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }


    public void PostInForum(Forum forum,final ForumCallback forumCallback) {
        Call<List<Forum>> call = serv.PostInForum(forum);
        call.enqueue(new Callback<List<Forum>>() {
            @Override
            public void onResponse(Call<List<Forum>> call, Response<List<Forum>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Forum> forums = response.body();
                    forumCallback.onForumCallback(forums);
                    Log.d("API_RESPONSE", "GET SUCCESSFUL");
                } else if (response.code() == 502) {
                    forumCallback.onMessage("No Forum Messages");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }else if (response.code() == 506) {
                    forumCallback.onMessage("USER NOT LOGGED IN");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
                else {
                    forumCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Forum>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void getPrivateNames(final PrivateCallback privateCallback) {
        Call<List<User>> call = serv.getPrivateNames();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> names = response.body();
                    privateCallback.onPrivateCallbackNames(names);
                    Log.d("API_RESPONSE", "GET SUCCESSFUL");
                } else if (response.code() == 502) {
                    privateCallback.onMessage("No Private Messages");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }else if (response.code() == 506) {
                    privateCallback.onMessage("USER NOT LOGGED IN");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
                else {
                    privateCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void getPrivateMessagesWith(final PrivateCallback privateCallback, String name) {
        Call<List<ChatIndividual>> call = serv.getPrivateMessagesWith(name);
        call.enqueue(new Callback<List<ChatIndividual>>() {
            @Override
            public void onResponse(Call<List<ChatIndividual>> call, Response<List<ChatIndividual>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatIndividual> names = response.body();
                    privateCallback.onPrivateCallbackMessages(names);
                    Log.d("API_RESPONSE", "GET SUCCESSFUL");
                } else if (response.code() == 502) {
                    privateCallback.onMessage("No Private Messages");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }else if (response.code() == 506) {
                    privateCallback.onMessage("USER NOT LOGGED IN");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
                else {
                    privateCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<ChatIndividual>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void postPrivateMessage(final PrivateCallback privateCallback, ChatIndividual chat) {
        Call<List<ChatIndividual>> call = serv.postPrivateMessage(chat);
        call.enqueue(new Callback<List<ChatIndividual>>() {
            @Override
            public void onResponse(Call<List<ChatIndividual>> call, Response<List<ChatIndividual>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatIndividual> names = response.body();
                    privateCallback.onPrivateCallbackMessages(names);
                    Log.d("API_RESPONSE", "GET SUCCESSFUL");
                } else if (response.code() == 502) {
                    privateCallback.onMessage("There has been an error, are you sure of the name?");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }else if (response.code() == 506) {
                    privateCallback.onMessage("USER NOT LOGGED IN");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
                else {
                    privateCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<ChatIndividual>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void getInsignias(final InsigniasCallback insigniasCallback, String name) {
        Call<List<Insignia>> call = serv.getInsignias(name);
        call.enqueue(new Callback<List<Insignia>>() {
            @Override
            public void onResponse(Call<List<Insignia>> call, Response<List<Insignia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Insignia> insignias = response.body();
                    insigniasCallback.onInsigniaCallback(insignias);
                    Log.d("API_RESPONSE", "GET SUCCESSFUL");
                }else if (response.code() == 506) {
                    insigniasCallback.onMessage("USER NOT LOGGED IN");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
                else {
                    insigniasCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Insignia>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void putInsignia(final InsigniasCallback insigniasCallback, String name, Insignia insignia) {
        Call<List<Insignia>> call = serv.putInsignia(name, insignia);
        call.enqueue(new Callback<List<Insignia>>() {
            @Override
            public void onResponse(Call<List<Insignia>> call, Response<List<Insignia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Insignia> insignias = response.body();
                    insigniasCallback.onInsigniaCallback(insignias);
                    Log.d("API_RESPONSE", "Post SUCCESSFUL");
                } else if (response.code() == 506) {
                    insigniasCallback.onMessage("USER NOT LOGGED IN");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                } else {
                    insigniasCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Insignia>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });

    }
    public void getMaxPuntuacion(final PartidasCallback callback) {
        Call<String> call = serv.getMaxPuntuacion();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 201) {
                    String maxpunc = response.body();
                    callback.onChargePuntuacionMax(maxpunc);
                    Log.d("API_RESPONSE", "Maxima puntuacion usuario: " + maxpunc);
                } else if (response.code() == 502) {
                    callback.onMessage("NO DATA AVAILABLE");
                    Log.d("API_RESPONSE", "NO DATA AVAILABLE, code: " + response.code());
                }else if (response.code() == 508) {
                    callback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "QUERY ERROR, code: " + response.code());
                }else{
                    callback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void getRanking(final PartidasCallback callback) {
        Call<List<Ranking>> call = serv.getRanking();
        call.enqueue(new Callback<List<Ranking>>() {
            @Override
            public void onResponse(Call<List<Ranking>> call, Response<List<Ranking>> response) {
                if (response.code() == 201) {
                    List<Ranking> ranking = response.body();
                    callback.onChargeRanking(ranking);
                    for (Ranking r : ranking) {
                        Log.d("API_RESPONSE", "Ranking Name: " + r.getUsername() + " Ranking points: " + r.getMaxpuntuacion());
                    }
                } else if (response.code() == 502) {
                    callback.onMessage("NO DATA AVAILABLE");
                    Log.d("API_RESPONSE", "NO DATA AVAILABLE, code: " + response.code());
                }else if (response.code() == 508) {
                    callback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "QUERY ERROR, code: " + response.code());
                }else{
                    callback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Ranking>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }
    public void getMailRanking(final PartidasCallback partidasCallback){
        Call<Void> call = serv.getMailRanking();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201) {
                    partidasCallback.onMessage("CHECK YOUR MAIL");
                    Log.d("API_RESPONSE", "POST SUCCESSFUL");
                } else if (response.code() == 500) {
                    partidasCallback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }else if (response.code() == 506) {
                    partidasCallback.onMessage("USER NOT LOGGED IN");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    public void getMedia(final VideoCallback callback) {
        Call<List<Video>> call = serv.getMedia();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.code() == 201) {
                    List<Video> videos = response.body();
                    callback.onVideoCallback(videos);
                    for (Video v : videos) {
                        Log.d("API_RESPONSE", "Video descripcion: " + v.getDescripcion() + " Video url: " + v.getUrl());
                    }
                } else if (response.code() == 502) {
                    callback.onMessage("NO DATA AVAILABLE");
                    Log.d("API_RESPONSE", "NO DATA AVAILABLE, code: " + response.code());
                }else if (response.code() == 508) {
                    callback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "QUERY ERROR, code: " + response.code());
                }else{
                    callback.onMessage("ERROR");
                    Log.d("API_RESPONSE", "Response not successful, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed", t);
            }
        });
    }

    //PART UNITY
    //AL ENVIAR STRING, RETROFIT HO ENVIA COM UN txt/plain
    // This method will handle the .txt content received from Unity
    public void sendStateToServer(String itemsStateText,final UnityCallback callback) {
        // Here you can handle the .txt or string content and make a Retrofit API call
        Log.d("ServiceBBDD", "Received state from Unity: " + itemsStateText);

        // Use Retrofit to send this data to your server
        // For example, create an API method that accepts this text and sends it to the server
        Call<Void> call = serv.sendState(itemsStateText);  // Replace 'sendState' with your actual method
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ServiceBBDD", "HOLA ENVIADO CORRECTAMENTE");
                    callback.onReturnHola();

                } else {
                    Log.e("ServiceBBDD", "Failed to send state.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ServiceBBDD", "Error sending state to server", t);
            }
        });
    }

    public void sendSave(String levelstring) {
        Call<Void> call = serv.sendSaveToServer(levelstring);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ServiceBBDD", "Enviat");
                } else {
                    Log.e("ServiceBBDD", "Failed to send state.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ServiceBBDD", "Error sending state to server", t);
            }
        });
    }

    public void sendAddCobre(int cobreAmount) {
        Call<Void> call = serv.addCobre(Integer.toString(cobreAmount));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ServiceBBDD", "Enviat");
                } else {
                    Log.e("ServiceBBDD", "Failed to send state.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ServiceBBDD", "Error sending state to server", t);
            }
        });
    }

}


