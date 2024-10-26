package com.example.biometriccheck;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    Button btn_biometric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btn_biometric = findViewById(R.id.btn_biometric);
        BiometricAvaliability();
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Ocorreu um erro "+errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Biometria valida!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Biometria invalida!", Toast.LENGTH_SHORT).show();
            }
        });
        btn_biometric.setOnClickListener(view -> {
            BiometricPrompt.PromptInfo.Builder promptinfo = dialoMetric();
            promptinfo.setNegativeButtonText("Cancelar");
            biometricPrompt.authenticate(promptinfo.build());
        });
    }
    BiometricPrompt.PromptInfo.Builder dialoMetric(){
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login Biometrico")
                .setSubtitle("faça login usando a biometria cadstrada no seu celular");
    }


    private void BiometricAvaliability() {
        String info = "";
        BiometricManager manager = BiometricManager.from(this);

        switch (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                info = "Seu dispositivo não possui leitor biometrico, não é possivel utilizar o app";
                ActiveButton(false);
                break;

            case BiometricManager.BIOMETRIC_SUCCESS:
                info = "Seu dispositivo esta apto a usar o app";
                ActiveButton(true);
                break;

            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                info = "Seu leitor biometrico não é suportado";
                ActiveButton(false);
                break;

            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                info = "Seu dispositivo precisa de uma atualização de segurança";
                ActiveButton(false);
                break;

            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                info = "Erro desconhecido";
                ActiveButton(false);
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                info = "Cadastre ao menos uma biometria";
                ActiveButton(false, true);
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                info = "o leitor esta indisponivel";
                ActiveButton(false);
                break;

        }
        TextView txtinfo = findViewById(R.id.txt_info);
        txtinfo.setText(info);
    }

    void ActiveButton(boolean active) {
        btn_biometric.setEnabled(active);

    }

    void ActiveButton(boolean active, boolean enroll){
       ActiveButton(active);
       if (!enroll) return;{
           Intent enrollintent = new Intent (Settings.ACTION_BIOMETRIC_ENROLL);
           enrollintent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,BiometricManager.Authenticators.BIOMETRIC_STRONG|BiometricManager.Authenticators.BIOMETRIC_WEAK);
           startActivity(enrollintent);

       }
    }


}