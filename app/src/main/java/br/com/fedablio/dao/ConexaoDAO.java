package br.com.fedablio.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import br.com.fedablio.model.Conexao;

public class ConexaoDAO {

    private SQLiteDatabase sqLiteDatabase;
    private ConnectionFactory connectionFactory;

    public ConexaoDAO(Context context) {
        connectionFactory = new ConnectionFactory(context);
    }

    public void abre() {
        sqLiteDatabase = connectionFactory.getWritableDatabase();
    }

    public void fecha() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
    }

    public void alterar(long _id, String endereco, String porta, String palavra) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("endereco", endereco);
        contentValues.put("porta", porta);
        contentValues.put("palavra", palavra);
        abre();
        sqLiteDatabase.update("conexao", contentValues, "_id = " + _id, null);
        fecha();
    }

    public ArrayList<Conexao> listaConexao() {
        ArrayList<Conexao> lista = new ArrayList<Conexao>();
        abre();
        Cursor c = sqLiteDatabase.query("conexao", new String[]{"_id", "endereco", "porta", "palavra"}, null, null, null, null, null, null);
        while (c.moveToNext()) {
            Conexao conexao = new Conexao();
            conexao.set_id(c.getLong(0));
            conexao.setEndereco(c.getString(1));
            conexao.setPorta(c.getString(2));
            conexao.setPalavra(c.getString(3));
            lista.add(conexao);
        }
        return lista;
    }

}