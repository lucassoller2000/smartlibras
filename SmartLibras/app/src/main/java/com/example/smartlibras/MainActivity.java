package com.example.smartlibras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private ListView lvMainChat;
    private EditText etMain;
    private TextView btnSend;
    private ChatArrayAdapter chatArrayAdapter;
    private VolleyClass volleyClass;
    private CheckBox checkBox;
    private Timer timer;
    private NotificationManager notificationManager;
    private static String MESSAGE_ID = "35715900message";
    private static String GROUP_KEY = "159753message";
    private int idAtual = 1;
    private List<Notification> notifications;
    private TextView btnGravar;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private LinearLayout linearLayout;
    private TextToSpeech textToSpeech;
    private boolean inApp;
    private ArrayList<String> textosNaoLidos;
//    private boolean pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.inicializaComponentes();

//        if(!pause) {
//            Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
//                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//            showAlertWindow(this, settingsIntent);
//        }

        this.textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int ttsLang = textToSpeech.setLanguage(new Locale("pt", "BR"));

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }

                    Voice v;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        v = new Voice("pt-br-x-afs#male_3-local",new Locale("pt","BR"),400,200,true, null);
                        textToSpeech.setVoice(v);

                    }
                    Log.i("TTS", "Initialization success.");
                }
            }
        });

        this.lvMainChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        this.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String texto = etMain.getText().toString();
                String aux = texto.replace(" ", "");
                if (aux.length() > 0) {
                    volleyClass.salvarPergunta(texto);
                    chatArrayAdapter.add(new ChatMessage(false, false, texto));
                    etMain.setText("");
                }
            }
        });

        this.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    etMain.setEnabled(false);
                    etMain.setVisibility(View.INVISIBLE);
                    linearLayout.removeView(btnSend);
                    btnGravar.setVisibility(View.INVISIBLE);
                } else {
                    etMain.setEnabled(true);
                    etMain.setVisibility(View.VISIBLE);
                    linearLayout.addView(btnSend);
                    btnGravar.setVisibility(View.VISIBLE);
                }
            }
        });

        this.btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceInput();
            }
        });

        this.lvMainChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String texto  = chatArrayAdapter.getChatMessageList().get(i).getMessage();
                textosNaoLidos.add("");
                textosNaoLidos.add(texto);
                converterTexto();
            }
        });

        this.iniciarTimer();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.createNotificationChannel();
        }
        inApp = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        notifications = new ArrayList<>();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.createNotificationChannel();
        }
        inApp = false;
//        pause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
            this.createNotificationChannel();
            idAtual = 1;
        }
        if(textosNaoLidos.size() > 0){
            converterTexto();
        }
        inApp = true;
    }

//    private static void showAlertWindow(final Context context, final Intent intent) {
//        new AlertDialog.Builder(context, R.style.App)
//                .setTitle("Notificações flutuantes")
//                .setMessage("Ative  as notificações flutuantes nas configurações para recebê-las no topo da tela.")
//                .setPositiveButton("Ir para notificações", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        context.startActivity(intent);
//                    }
//                })
//                .setNeutralButton(android.R.string.cancel, null)
//                .show();
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lvMainChat) {
            menu.add("Limpar conversa");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.message);
                lvMainChat.setAdapter(chatArrayAdapter);
        }
        return true;
    }


    private void inicializaComponentes() {
        lvMainChat = findViewById(R.id.lvMainChat);
        etMain = findViewById(R.id.etMain);
        btnSend = findViewById(R.id.btnSend);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.message);
        lvMainChat.setAdapter(chatArrayAdapter);
        registerForContextMenu(lvMainChat);
        volleyClass = new VolleyClass(getApplicationContext());
        checkBox = findViewById(R.id.checkBox);
        timer = new Timer();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        }

        btnGravar = findViewById(R.id.btnGravar);
        linearLayout = findViewById(R.id.linear);
        textosNaoLidos = new ArrayList<>();
    }

    private void startVoiceInput() throws ActivityNotFoundException {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("pt", "BR"));
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Olá, basta apenas falar e escreveremos para você");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String textoOuvido = result.get(0).toLowerCase();
                volleyClass.salvarPergunta(textoOuvido);
                chatArrayAdapter.add(new ChatMessage(false, false, textoOuvido));
            }
        }
    }

    private void iniciarTimer() {
        int tempo = 5000;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (checkBox.isChecked()) {
                    MainActivity.this.lerPergunta();
                    MainActivity.this.lerResposta(true, false, true);
                } else {
                    MainActivity.this.lerResposta(false, true, false);
                }
            }
        }, tempo, tempo);
    }

    private void lerPergunta() {
        volleyClass.lerPergunta(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.length() > 0) {
                    chatArrayAdapter.add(new ChatMessage(true, true, response));
                    if(inApp){
                        textosNaoLidos.add("");
                        textosNaoLidos.add(response);
                        converterTexto();
                    }else {
                        notificar(response, true);
                        if(textosNaoLidos.size() == 0){
                            textosNaoLidos.add("Você possui novas mensagens");
                        }
                        textosNaoLidos.add(response);
                    }
                }
            }
        });
    }

    private void lerResposta(final boolean s, final boolean lerResposta, final boolean tela2) {
        volleyClass.lerResposta(s, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.length() > 0) {
                    chatArrayAdapter.add(new ChatMessage(lerResposta, tela2, response));
                    if (lerResposta) {
                        if(inApp){
                            textosNaoLidos.add("");
                            textosNaoLidos.add(response);
                            converterTexto();

                        }else {
                            notificar(response, false);
                            if(textosNaoLidos.size() == 0){
                                textosNaoLidos.add("Você possui novas mensagens");
                            }
                            textosNaoLidos.add(response);
                        }
                    }
                }
            }
        });
    }

    private void converterTexto(){
        Log.i("TTS", "button clicked: ");

        int speechStatus = 0;

        for(String s : textosNaoLidos){
            speechStatus = textToSpeech.speak(s, TextToSpeech.QUEUE_ADD, null);
        }

        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
        textosNaoLidos = new ArrayList<>();
    }

    private void notificar(String texto, boolean hasImage) {
        Bitmap bit = null;

        if(hasImage){
            String url = "http://www.innovative.inf.br/"+texto+".gif";
            texto = getString(R.string.message_image);

            try {
                bit = new UrlTask().execute(url).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int SUMMARY_ID = 0;

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification;
        Notification summaryNotification;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (hasImage) {
                notification =
                        new NotificationCompat.Builder(MainActivity.this, MESSAGE_ID)
                                .setSmallIcon(R.drawable.sig)
                                .setContentTitle(getString(R.string.message_title))
                                .setContentText(texto)
                                .setLargeIcon(bit)
                                .setGroup(GROUP_KEY)
                                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setDefaults(Notification.DEFAULT_VIBRATE)
                                .setContentIntent(pendingIntent)
                                .setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(bit)
                                        .bigLargeIcon(null))
                                .build();
            } else {
                notification =
                        new NotificationCompat.Builder(MainActivity.this, MESSAGE_ID)
                                .setSmallIcon(R.drawable.sig)
                                .setContentTitle(getString(R.string.message_title))
                                .setContentText(texto)
                                .setGroup(GROUP_KEY)
                                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setDefaults(Notification.DEFAULT_VIBRATE)
                                .build();
            }

            summaryNotification =
                    new NotificationCompat.Builder(MainActivity.this, MESSAGE_ID)
                            .setContentText(idAtual + getString(R.string.new_message))
                            .setSmallIcon(R.drawable.sig)
                            .setGroup(GROUP_KEY)
                            .setGroupSummary(true)
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .build();

            notifications.add(notification);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            for (int i = 0; i < notifications.size(); i++) {
                notificationManager.notify(i + 1, notifications.get(i));
            }
            notificationManager.notify(SUMMARY_ID, summaryNotification);
            idAtual++;

        }else{
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.message_title))
                    .setContentText(texto)
                    .setSmallIcon(R.drawable.sig)
                    .setGroup(GROUP_KEY)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setNumber(idAtual)
                    .build();

            if(hasImage){
                summaryNotification = new NotificationCompat.Builder(this)
                        .setContentTitle(getString(R.string.message_title))
                        .setContentText(texto)
                        .setSmallIcon(R.drawable.sig)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setGroup(GROUP_KEY)
                        .setLargeIcon(bit)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(bit)
                                .bigLargeIcon(null))
                        .setGroupSummary(true)
                        .setNumber(idAtual)
                        .build();
            }else{
                summaryNotification = new NotificationCompat.Builder(this)
                        .setContentTitle(getString(R.string.message_title))
                        .setContentText(texto)
                        .setSmallIcon(R.drawable.sig)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setGroup(GROUP_KEY)
                        .setGroupSummary(true)
                        .setNumber(idAtual)
                        .build();
            }

            notifications.add(notification);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            for (int i = 0; i < notifications.size(); i++) {
                notificationManager.notify(i + 1, notifications.get(i));
            }

            notificationManager.notify(SUMMARY_ID, summaryNotification);
            idAtual++;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(MESSAGE_ID, name, importance);
            channel.setDescription(description);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}