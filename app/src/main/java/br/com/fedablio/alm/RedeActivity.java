package br.com.fedablio.alm;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import br.com.fedablio.dao.ConexaoDAO;
import br.com.fedablio.model.Conexao;

public class RedeActivity extends Activity{

    private EditText etRede;
    private EditText etPorta;
    private EditText etPalavra;
    private ArrayList<Conexao> listaConexaoBanco;
    private String selecao_codigo = "";
    private String selecao_rede = "";
    private String selecao_porta = "";
    private String selecao_palavra = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rede);
        etRede = (EditText) findViewById(R.id.editTextRede);
        etPorta = (EditText) findViewById(R.id.editTextPorta);
        etPalavra = (EditText) findViewById(R.id.editTextPalavra);
        seleciona_conexao();
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
    }

    private void seleciona_conexao(){
        AsyncTask<Void, Void, Void> executaTrem = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                new ConexaoDAO(RedeActivity.this).abre();
                listaConexaoBanco = new ConexaoDAO(RedeActivity.this).listaConexao();
                for(Conexao conexao : listaConexaoBanco){
                    selecao_codigo = String.valueOf(conexao.get_id());
                    selecao_rede = conexao.getEndereco();
                    selecao_porta = conexao.getPorta();
                    selecao_palavra = conexao.getPalavra();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                etRede.setText(selecao_rede);
                etPorta.setText(selecao_porta);
                etPalavra.setText(selecao_palavra);
                super.onPostExecute(aVoid);
            }
        };
        executaTrem.execute((Void[]) null);
    }

    public void alterar(View view){
        long codigo = Long.parseLong(selecao_codigo);
        String ip = etRede.getText().toString();
        String porta = etPorta.getText().toString();
        String palavra = etPalavra.getText().toString();
        if(ip.length() != 0 && porta.length() != 0 && palavra.length() != 0){
            new ConexaoDAO(this).alterar(codigo, ip, porta, palavra);
            Intent intent = new Intent(RedeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this,"There are blank fields.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_rede, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.mnPrincipal){
            Intent intent = new Intent(RedeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}