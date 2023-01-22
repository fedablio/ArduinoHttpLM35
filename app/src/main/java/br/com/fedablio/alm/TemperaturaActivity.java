package br.com.fedablio.alm;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import br.com.fedablio.dao.ConexaoDAO;
import br.com.fedablio.model.Conexao;

public class TemperaturaActivity extends Activity {

    private TextView tvCelsius, tvKelvin, tvFarenheit;
    private HttpClient cliente = null;
    private HttpGet requiscao = null;
    private BufferedReader in = null;
    private StringBuffer sb = null;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private ArrayList<Conexao> listaConexaoBanco;
    private String selecao_rede = "";
    private String selecao_porta = "";
    private String selecao_palavra = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperatura);
        tvCelsius = (TextView) findViewById(R.id.textViewCelsius);
        tvKelvin = (TextView) findViewById(R.id.textViewKelvin);
        tvFarenheit = (TextView) findViewById(R.id.textViewFarenheit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        seleciona_conexao();
        mostra_situacao();
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
    }

    private void seleciona_conexao(){
        AsyncTask<Void, Void, Void> executaTrem = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                new ConexaoDAO(TemperaturaActivity.this).abre();
                listaConexaoBanco = new ConexaoDAO(TemperaturaActivity.this).listaConexao();
                for(Conexao conexao : listaConexaoBanco){
                    selecao_rede = conexao.getEndereco();
                    selecao_porta = conexao.getPorta();
                    selecao_palavra = conexao.getPalavra();
                }
                return null;
            }
        };
        executaTrem.execute((Void[]) null);
    }

    private void mostra_situacao() {
        Timer timer = null;
        if (timer == null) {
            timer = new Timer();
            TimerTask tarefa = new TimerTask() {
                @Override
                public void run() {
                    AsyncTask<Void, Void, Void> executaIsso = new AsyncTask<Void, Void, Void>() {
                        String arduino = "";
                        double celsius;
                        double kelvin;
                        double farenheit;
                        DecimalFormat arredonda = new DecimalFormat("0.00");
                        @Override
                        protected Void doInBackground(Void... params) {
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
                                arduino = page;
                                celsius = Double.parseDouble(arduino);
                                kelvin = celsius + 273;
                                farenheit = celsius * 1.8 + 32;
                            } catch(Exception erro){
                                throw new RuntimeException(erro);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            progressStatus = 0;
                            new Thread(new Runnable() {
                                public void run() {
                                    while (progressStatus < 100) {
                                        progressStatus += 1;
                                        handler.post(new Runnable() {
                                            public void run() {
                                                progressBar.setProgress(progressStatus);
                                            }
                                        });
                                        try {
                                            Thread.sleep(50);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                            tvCelsius.setText(String.valueOf(arredonda.format(celsius).replace(",",".")));
                            tvKelvin.setText(String.valueOf(arredonda.format(kelvin).replace(",",".")));
                            tvFarenheit.setText(String.valueOf(arredonda.format(farenheit).replace(",",".")));
                            super.onPostExecute(result);
                        }
                    };
                    executaIsso.execute((Void[]) null);
                }
            };
            timer.scheduleAtFixedRate(tarefa, 0, 5000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tempereatura, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.mnPrincipal){
            Intent intent = new Intent(TemperaturaActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}