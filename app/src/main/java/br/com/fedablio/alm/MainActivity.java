package br.com.fedablio.alm;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import br.com.fedablio.dao.ConexaoDAO;
import br.com.fedablio.model.Conexao;

public class MainActivity extends Activity {

    private ArrayList<Conexao> listaConexaoBanco;
    private boolean excecao = false;
    private Exception mensagem_excecao = null;
    private String selecao_rede = "";
    private String selecao_porta = "";
    private String selecao_palavra;
    private TextView tvRede;
    private TextView tvPorta;
    private TextView tvPalavra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvRede = (TextView) findViewById(R.id.textViewRedeActivityMain);
        tvPorta = (TextView) findViewById(R.id.textViewPortaActivityMain);
        tvPalavra = (TextView) findViewById(R.id.textViewPalavraActivityMain);
        seleciona_conexao();
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
    }

    private void seleciona_conexao(){
        AsyncTask<Void, Void, Void> executaTrem = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                new ConexaoDAO(MainActivity.this).abre();
                listaConexaoBanco = new ConexaoDAO(MainActivity.this).listaConexao();
                for(Conexao conexao : listaConexaoBanco){
                    selecao_rede = conexao.getEndereco();
                    selecao_porta = conexao.getPorta();
                    selecao_palavra = conexao.getPalavra();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                tvRede.setText(selecao_rede);
                tvPorta.setText(selecao_porta);
                tvPalavra.setText(selecao_palavra);
                super.onPostExecute(aVoid);
            }
        };
        executaTrem.execute((Void[]) null);
    }

    public static boolean rede(Context contexto){
        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if ( (netInfo != null) && (netInfo.isConnectedOrConnecting()) && (netInfo.isAvailable()) ){
            return true;
        }
        return false;
    }

    private void url() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient cliente = null;
        HttpGet requiscao = null;
        BufferedReader in = null;
        StringBuffer sb = null;
        cliente = new DefaultHttpClient();
        requiscao = new HttpGet();
        try {
            requiscao.setURI(new URI("http://" + selecao_rede+":"+selecao_porta + "/"+selecao_palavra+"/"));
            cliente.execute(requiscao);
            HttpResponse response = cliente.execute(requiscao);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            String page = sb.toString();
        } catch(Exception erro){
            excecao = true;
            mensagem_excecao = erro;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.mnTemperatura){
            if(rede(this)){
                url();
                if(excecao != true){
                    Intent intent = new Intent(MainActivity.this, TemperaturaActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this,"Invalid address or Arduino not found.", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"No network connection.", Toast.LENGTH_LONG).show();
            }
        }
        if(id == R.id.mnEndereco){
            Intent intent = new Intent(MainActivity.this, RedeActivity.class);
            startActivity(intent);
            finish();
        }
        if(id == R.id.mnSobre){
            Intent intent = new Intent(MainActivity.this, SobreActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}