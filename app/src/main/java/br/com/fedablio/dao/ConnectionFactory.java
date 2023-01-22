package br.com.fedablio.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConnectionFactory extends SQLiteOpenHelper{

    private static final String NOME_DATABASE = "banco_alm";
    private static final int VERSAO_DATABASE = 1;

    public ConnectionFactory(Context context) {
        super(context, NOME_DATABASE, null, VERSAO_DATABASE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "CREATE TABLE conexao (_id INTEGER PRIMARY KEY AUTOINCREMENT, endereco TEXT, porta TEXT, palavra TEXT);";
        String sql2 = "INSERT INTO conexao (endereco, porta, palavra) VALUES ('192.168.0.199', '8090', 'fedablio');";
        db.execSQL(sql1);
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}