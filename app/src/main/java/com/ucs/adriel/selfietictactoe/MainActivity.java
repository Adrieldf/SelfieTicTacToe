package com.ucs.adriel.selfietictactoe;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Serializable {

    private final int REQUEST_PERMISSION = 1;
    private final int CAMERA = 2;
    private File photoFile = null;

    private Bitmap playerOneImage;
    private Bitmap playerTwoImage;
    private boolean setPlayerOne = false;
    private boolean setPlayerTwo = false;
    private boolean fromPlayerOne = false;
    private boolean fromPlayerTwo = false;
    private int whosFirst = 0;
    private int[][] game = {{0,0,0},
                            {0,0,0},
                            {0,0,0}};
    private int turn = 0;
    private int rounds = 0;
    private TextView txtInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Permissao storage read
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        }
        // Permissao storage write
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        }
        playerOneImage = null;
        playerTwoImage = null;
        txtInfo =  findViewById(R.id.txtInfo);
        setInfo("Selecione o jogador 1 e tire uma selfie");
        findViewById(R.id.txt1).setEnabled(true);
        findViewById(R.id.txt2).setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CAMERA) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(photoFile)));
            savePhoto(photoFile.getAbsolutePath());
        }
    }

    private void savePhoto(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if(fromPlayerOne){
            playerOneImage = bitmap;
           ((ImageButton)findViewById(R.id.btnPlayerOne)).setImageBitmap(bitmap);
           setPlayerOne = true;
           fromPlayerOne = false;
            findViewById(R.id.txt1).setEnabled(false);
        }
        else if(fromPlayerTwo){
            playerTwoImage = bitmap;
            ((ImageButton)findViewById(R.id.btnPlayerTwo)).setImageBitmap(bitmap);
            setPlayerTwo = true;
            fromPlayerTwo = false;
            findViewById(R.id.txt2).setEnabled(false);
        }

    }

    public void playerOneClick(View view){
        fromPlayerOne = true;
        if(!setPlayerOne){
            snap();
            if(playerTwoImage != null)
                setInfo("Selecione qual jogador irá começar o jogo");
            else
                setInfo("Selecione o jogador 2 e tire uma selfie");
        }
        else
        {
            if(playerOneImage != null && playerTwoImage != null)
            {
                if(whosFirst == 0)
                {
                    whosFirst = 1;
                    setInfo("Vez do jogador 1");
                }
            }
        }

    }
    public void playerTwoClick(View view){
        fromPlayerTwo = true;
        if(!setPlayerTwo){
            snap();
            if(playerOneImage == null)
                setInfo("Selecione o jogador 1 e tire uma selfie");
            else
                setInfo("Selecione qual jogador irá começar o jogo");
        }
        else
        {
            if(playerOneImage != null && playerTwoImage != null)
            {
                if(whosFirst == 0)
                {
                    whosFirst = 5;
                    setInfo("Vez do jogador 2");
                }
            }
        }

    }
    public void playerClear(View view){
       clearGame(true);
    }
    public void clearGame(boolean all) {
        if(all)
        {
            playerOneImage = null;
            ((ImageButton)findViewById(R.id.btnPlayerOne)).setImageBitmap(null);
            setPlayerOne = false;
            fromPlayerOne = false;
            playerTwoImage = null;
            ((ImageButton)findViewById(R.id.btnPlayerTwo)).setImageBitmap(null);
            setPlayerTwo = false;
            fromPlayerTwo = false;
            findViewById(R.id.txt1).setEnabled(true);
            findViewById(R.id.txt2).setEnabled(true);
            setInfo("Selecione o jogador 1 e tire uma selfie");
        }else{
            setInfo("Selecione qual jogador irá começar o jogo");
        }
        rounds = 0;
        whosFirst = 0;
        game = new int[][] {{0,0,0},{0,0,0},{0,0,0}};

        ((ImageButton)findViewById(R.id.btnGame0)).setImageBitmap(null);
        ((ImageButton)findViewById(R.id.btnGame1)).setImageBitmap(null);
        ((ImageButton)findViewById(R.id.btnGame2)).setImageBitmap(null);
        ((ImageButton)findViewById(R.id.btnGame3)).setImageBitmap(null);
        ((ImageButton)findViewById(R.id.btnGame4)).setImageBitmap(null);
        ((ImageButton)findViewById(R.id.btnGame5)).setImageBitmap(null);
        ((ImageButton)findViewById(R.id.btnGame6)).setImageBitmap(null);
        ((ImageButton)findViewById(R.id.btnGame7)).setImageBitmap(null);
        ((ImageButton)findViewById(R.id.btnGame8)).setImageBitmap(null);
    }
    public void winnerMessage(int winner){
        Intent intent = new Intent(getBaseContext(), winnerMessage.class);
        if(winner == 1)
        {
            Gson gson = new Gson();
            String json = gson.toJson(new WinnerData(playerOneImage, "Vitória do jogador 1!"));
            intent.putExtra("winnerData", json);
        }else
        {
            Gson gson = new Gson();
            String json = gson.toJson( new WinnerData(playerTwoImage, "Vitória do jogador 2!"));
            intent.putExtra("winnerData", json);
        }
        startActivity(intent);
    }
    public void snap() {
        Intent takePictureIntent = new
                Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createFile();
            } catch (IOException ex) {
                showAlert("Erro", "Erro ao tirar a foto");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getBaseContext(),getBaseContext().getApplicationContext().getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA);
            }
        }
    }
    private File createFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_Hhmmss").format(new Date());
        File folder = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return new File(folder.getPath() + File.separator + "JPG_" + timeStamp + ".jpg");
    }


    private void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    private void setInfo(String info){
        txtInfo.setText(info);
    }
    public void gameClick(View view){
        if(whosFirst == 0)
        {
            showAlert("Aviso", "Selecione qual jogador será o primeiro a jogar");
            return;
        }
        else if(turn == 0)
            turn = whosFirst;
        Bitmap playerImage;
        if(turn == 5){
            playerImage = playerTwoImage;
            turn = 1;
            setInfo("Vez do jogador 1");
        }
        else{
            playerImage = playerOneImage;
            turn = 5;
            setInfo("Vez do jogador 2");
        }


        switch(view.getId())
        {
            case R.id.btnGame0:
                ((ImageButton)findViewById(R.id.btnGame0)).setImageBitmap(playerImage);
                game[0][0] = turn;
                break;
            case R.id.btnGame1:
                ((ImageButton)findViewById(R.id.btnGame1)).setImageBitmap(playerImage);
                game[0][1] = turn;
                break;
            case R.id.btnGame2:
                ((ImageButton)findViewById(R.id.btnGame2)).setImageBitmap(playerImage);
                game[0][2] = turn;
                break;
            case R.id.btnGame3:
                ((ImageButton)findViewById(R.id.btnGame3)).setImageBitmap(playerImage);
                game[1][0] = turn;
                break;
            case R.id.btnGame4:
                ((ImageButton)findViewById(R.id.btnGame4)).setImageBitmap(playerImage);
                game[1][1] = turn;
                break;
            case R.id.btnGame5:
                ((ImageButton)findViewById(R.id.btnGame5)).setImageBitmap(playerImage);
                game[1][2] = turn;
                break;
            case R.id.btnGame6:
                ((ImageButton)findViewById(R.id.btnGame6)).setImageBitmap(playerImage);
                game[2][0] = turn;
                break;
            case R.id.btnGame7:
                ((ImageButton)findViewById(R.id.btnGame7)).setImageBitmap(playerImage);
                game[2][1] = turn;
                break;
            case R.id.btnGame8:
                ((ImageButton)findViewById(R.id.btnGame8)).setImageBitmap(playerImage);
                game[2][2] = turn;
                break;
        }
        rounds++;
        int value = checkWin();
       if(value > 0)
       {
           if(value == 1) {
              // showAlert("Vitória!", "Jogador 2 venceu a partida!");
               clearGame(false);
               winnerMessage(2);
           }else {
             // showAlert("Vitória!", "Jogador 1 venceu a partida!");
               clearGame(false);
               winnerMessage(1);
           }
       }else if(rounds >= 9){
           showAlert("Empate!", "O jogo empatou, boa sorte na próxima!");
           clearGame(false);
       }

    }
    private int checkWin(){
        int condition = 0;

        if((game[0][0] +  game[0][1] + game[0][2]) == 3){//Linha 1
            condition = 1;
        }else if((game[0][0] +  game[0][1] + game[0][2]) == 15){
            condition = 2;
        }else if((game[1][0] +  game[1][1] + game[1][2]) == 3){//Linha 2
            condition = 1;
        }else if((game[1][0] +  game[1][1] + game[1][2]) == 15){
            condition = 2;
        }else if((game[2][0] +  game[2][1] + game[2][2]) == 3){//Linha 3
            condition = 1;
        }else if((game[2][0] +  game[2][1] + game[2][2]) == 15){
            condition = 2;
        }else if((game[0][0] +  game[1][0] + game[2][0]) == 3){//Coluna 1
            condition = 1;
        }else if((game[0][0] +  game[1][0] + game[2][0]) == 15){
            condition = 2;
        }else if((game[0][1] +  game[1][1] + game[2][1]) == 3){//Coluna 2
            condition = 1;
        }else if((game[0][1] +  game[1][1] + game[2][1]) == 15){
            condition = 2;
        }else if((game[0][2] +  game[1][2] + game[2][2]) == 3){//Coluna 3
            condition = 1;
        }else if((game[0][2] +  game[1][2] + game[2][2]) == 15){
            condition = 2;
        }else if((game[0][0] +  game[1][1] + game[2][2]) == 3){//Diagonal
            condition = 1;
        }else if((game[0][0] +  game[1][1] + game[2][2]) == 15){
            condition = 2;
        }else if((game[0][2] +  game[1][1] + game[2][0]) == 3){//Diagonal Inversa
            condition = 1;
        }else if((game[0][2] +  game[1][1] + game[2][0]) == 15){
            condition = 2;
        }
        return condition;
    }

    public static class WinnerData implements Serializable{
        public String text;
        public Bitmap image;
        public WinnerData(Bitmap image, String text)
        {
            this.image = image;
            this.text = text;
        }
    }
}
