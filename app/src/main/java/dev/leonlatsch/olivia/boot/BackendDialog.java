package dev.leonlatsch.olivia.boot;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

    private static final String DEFAULT_SUFFIX = "backend/";
    private static final String SLASH = "/";
    private static final String HTTPS = "https:///";

    private TextView connectButton;
    private EditText hostnameEditText;
    private ProgressBar progressBar;
    private ImageView successImageView;

    private CommonService commonService;

    public BackendDialog(Context context) {
        super(context);
        View view = getLayoutInflater().inflate(R.layout.dialog_backend, null);
        connectButton = view.findViewById(R.id.dialog_backend_button);
        hostnameEditText = view.findViewById(R.id.dialog_backend_edit_text);
        progressBar = view.findViewById(R.id.dialog_backend_progressbar);
        successImageView = view.findViewById(R.id.dialog_backend_success_indicator);

        connectButton.setOnClickListener(view1 -> connect());

        setCancelable(false);
        setView(view);
    }

    private void connect() {
        isLoading(true);
        String url = buildUrl(hostnameEditText.getText().toString());
        if (url == null) {
            isLoading(false);
            success(false);
            return;
        }

        // Try healthcheck
        tryHealthcheck(url);
    }

    private void tryHealthcheck(String url) {
        RestServiceFactory.initialize(url);
        commonService = RestServiceFactory.getCommonService();
        commonService.healthcheck().enqueue(new Callback<Container<Void>>() {
            @Override
            public void onResponse(Call<Container<Void>> call, Response<Container<Void>> response) {
                isLoading(false);
                success(true);
            }

            @Override
            public void onFailure(Call<Container<Void>> call, Throwable t) {
                if (url.endsWith(DEFAULT_SUFFIX)) {
                    isLoading(false);
                    success(false);
                } else {
                    tryHealthcheck(url + DEFAULT_SUFFIX);
                }
            }
        });
    }

    private String buildUrl(String input) {
        if (!Pattern.matches(Regex.URL, input)) {
            input = HTTPS + input;
            if (!Pattern.matches(Regex.URL, input)) {
                return null;
            }
        }

        if (!input.endsWith(SLASH)) {
            input += SLASH;
        }

        return input;
    }

    private void success(boolean success) {
        successImageView.setVisibility(View.VISIBLE);
        if (success) {
            successImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icons8_checked_48, getContext().getTheme()));
        } else {
            successImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icons8_cancel_48, getContext().getTheme()));
        }
    }

    private void isLoading(boolean isLoading) {
        if (isLoading) {
            hostnameEditText.setEnabled(false);
            connectButton.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
            successImageView.setVisibility(View.GONE);
        } else {
            hostnameEditText.setEnabled(true);
            connectButton.setClickable(true);
            progressBar.setVisibility(View.GONE);
        }
    }
}
