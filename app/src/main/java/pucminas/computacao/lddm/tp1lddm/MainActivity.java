package pucminas.computacao.lddm.tp1lddm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String nomeContato, numeroContato, enderecoContato, emailContato;
    EditText nameContact, numberContact, adressContact, emailContact;
    Pessoa pessoa, contato;
    boolean clicked = false;
    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions mGoogleSignInOptions;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton loginGoogle, loginFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);
        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();*/
        loginGoogle = findViewById(R.id.googleLogin);
        loginGoogle.setSize(SignInButton.SIZE_WIDE);
        findViewById(R.id.googleLogin).setOnClickListener(this);
    }

    //Método que chama o login da conta do google
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleLogin:
                signIn();
                loginGoogle = findViewById(R.id.googleLogin);
                loginGoogle.setSize(SignInButton.SIZE_ICON_ONLY);
                break;
            // ...
        }
    }


    //API do google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /* Método que cria o contato e abre o app de contatos e cria mensagem de erro caso haja algum campo vazio.
     * @param View view
     * @return void
     */
    public void createContact(View view){
        Button button = (Button)findViewById(R.id.saveButton);

        // Show toast message when button is clicked
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    //armazena o que foi digitado nas edit texts
                    nameContact = (EditText) findViewById(R.id.contactName);
                    numberContact = (EditText) findViewById(R.id.contactNumber);
                    adressContact = (EditText) findViewById(R.id.contactAdress);
                    emailContact = (EditText) findViewById(R.id.contactEmail);
                    //transforma o que foi digitado nas edit text em string e armazena
                    nomeContato = nameContact.getText().toString();
                    numeroContato = numberContact.getText().toString();
                    enderecoContato = adressContact.getText().toString();
                    emailContato = emailContact.getText().toString();
                    //verifica se os campos estão vazios, se estiverem, gera mensagem de erro
                    if (nomeContato.compareTo("") == 0 || numeroContato.compareTo("") == 0 || enderecoContato.compareTo("") == 0 || emailContato.compareTo("") == 0) {
                        Toast.makeText(getApplicationContext(), "Um dos campos está vazio!", Toast.LENGTH_LONG).show();// Set your own toast  message
                    //se não estiverem vazios,cria o contato e chama a intent do app contato
                    } else {
                        clicked = true;
                        contato = createPessoa(nomeContato, numeroContato, enderecoContato, emailContato);
                        Intent intentContact = new Intent(Intent.ACTION_INSERT);
                        intentContact.setType(ContactsContract.Contacts.CONTENT_TYPE);
                        intentContact.putExtra(ContactsContract.Intents.Insert.NAME, contato.getNomePessoa());
                        intentContact.putExtra(ContactsContract.Intents.Insert.EMAIL, contato.getEmailPessoa());
                        intentContact.putExtra(ContactsContract.Intents.Insert.PHONE, contato.getNumeroTel());
                        intentContact.putExtra(ContactsContract.Intents.Insert.POSTAL, contato.getEndereco());
                        if (intentContact.resolveActivity(getPackageManager()) != null) {
                            startActivity(intentContact);
                        }
                    }
                }
        });
    }

    //método que cria objeto do tipo pessoa
    public Pessoa createPessoa(String nomeContato, String numeroContato, String enderecoContato, String emailContato){
        pessoa = new Pessoa(nomeContato, numeroContato, enderecoContato, emailContato);
        return pessoa;
    }

    /* Método que chama a intent do email e envia email
    * @param View view
    * @return void
    */
    public void sendEmailContact(View view){
        //se boolean que representa se contato foi salvo for false, aviso de erro
        if(!clicked){
            Toast.makeText(getApplicationContext(),"Contato não foi salvo ainda!",Toast.LENGTH_LONG).show();
        //chama a intent do email
        }else {
            Intent intentEmail = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + contato.getEmailPessoa()));
            intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Contato salvo");
            intentEmail.putExtra(Intent.EXTRA_TEXT, "Seu contato foi salvo com sucesso!");
            if (intentEmail.resolveActivity(getPackageManager()) != null) {
                startActivity(intentEmail);
            }
        }
    }

    /* Método que chama a intent whatsapp e envia mensagem para o contato salvo
     * @param View view
     * @return void
     */
    public void sendMessageWhatsapp(View view){
        try {
            //se boolean que representa se contato foi salvo for false, aviso de erro
            if(!clicked){
                Toast.makeText(getApplicationContext(),"Contato não foi salvo ainda!",Toast.LENGTH_LONG).show();
            //chama a intent do whatsapp
            }else{
                String text = "Seu número foi salvo com sucesso!";
                Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                intentWhatsapp.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+55" + contato.getNumeroTel() + "&text=" + text));
                startActivity(intentWhatsapp);
            }
        }catch(Exception E){
            E.printStackTrace();
        }
    }

    //Método que abre a intent do facebook com API
    public void loginFacebook(View view){
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }
                    @Override
                    public void onCancel() {
                        // App code
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 0: super.onActivityResult(requestCode, resultCode, data);
                    callbackManager.onActivityResult(requestCode, resultCode, data);
                        break;
            case 1: super.onActivityResult(requestCode, resultCode, data);
                    LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
                        break;
            case 2: super.onActivityResult(requestCode, resultCode, data);
                    // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
                    if (requestCode == RC_SIGN_IN) {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        GoogleSignInResult task = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                        handleSignInResult(task);
                    }
                    break;
        }
    }

    //Método que chama intent de login do LinkedIn com API
    public void loginLinkedin(View view){
        final Activity thisActivity = this;

        LISessionManager.getInstance(getApplicationContext()).init(thisActivity, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                apiHelperLinkedin();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
            }
        }, true);
    }

    public void apiHelperLinkedin() {
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                // Success!
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                // Error making GET request!
            }
        });
    }

    /*
    private void getPackageHash() {
        try {

            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "pucminas.computacao.lddm.tp1lddm",//give your package name here
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.d(TAG, "Hash  : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));//Key hash is printing in Log
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }*/

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    private void handleSignInResult(GoogleSignInResult gsi) {
        if(gsi.isSuccess()){
            loginGoogle = findViewById(R.id.googleLogin);

        // Signed in successfully, show authenticated UI.
        }else{
            Toast.makeText(this, "Não foi possível realizar o login.", Toast.LENGTH_SHORT).show();
        }
    }
}
