package dev.leonlatsch.olivia.boot;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.regex.Pattern;

import dev.leonlatsch.olivia.R;
import dev.leonlatsch.olivia.constants.Regex;
import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.service.CommonService;
import dev.leonlatsch.olivia.rest.service.RestServiceFactory;
import dev.leonlatsch.olivia.util.AndroidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackendDialog extends AlertDialog {

    private TextView connectButton;
    private EditText hostnameEditText;
    private ProgressBar progressBar;

    private CommonService commonService;

    public BackendDialog(Context context) {
        super(context);
        View view = getLayoutInflater().inflate(R.layout.dialog_backend, null);
        connectButton = view.findViewById(R.id.dialog_backend_button);
        hostnameEditText = view.findViewById(R.id.dialog_backend_edit_text);
        progressBar = view.findViewById(R.id.dialog_backend_progressbar);

        connectButton.setOnClickListener(view1 -> connect());

        setCancelable(false);
        setView(view);
    }

    private void connect() {
        isLoading(true);
        String input = hostnameEditText.getText().toString();

        if (!Pattern.matches(Regex.URL, input)) {
            input = "https://" + input;
        }

        if (!input.endsWith("/")) {
            input += "/";
        }

        // Try healthcheck
        RestServiceFactory.initialize(input);
        commonService = RestServiceFactory.getCommonService();
        commonService.healthcheck().enqueue(new Callback<Container<Void>>() {
            @Override
            public void onResponse(Call<Container<Void>> call, Response<Container<Void>> response) {
                System.out.println("Success");
                isLoading(false);
            }

            @Override
            public void onFailure(Call<Container<Void>> call, Throwable t) {
                System.out.println("Failue");
                isLoading(false);
            }
        });
    }

    private void isLoading(boolean isLoading) {
        if (isLoading) {
            AndroidUtils.animateView(progressBar, View.VISIBLE, 1);
        } else {
            AndroidUtils.animateView(progressBar, View.GONE, 1);
        }
    }
}
