package com.example.simplecalculator; // sesuaikan dengan package project kamu

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class mainActivity extends AppCompatActivity {

    private TextView tvExpression; // menampilkan rumus lengkap, misal "78-65"
    private TextView tvResult;     // menampilkan hasil sementara, misal "13"

    private final Calculator calculator = new Calculator();

    // SharedPreferences (lewat PrefsManager) + dua layar yang dibutuhkan onResume()
    private PrefsManager prefsManager;
    private LinearLayout layoutStart;
    private LinearLayout layoutCalculator;

    // Rumus yang sedang diketik, disimpan sebagai teks (Lesson 2: Variabel)
    private String expression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ===== Bagian 1: Tombol "Buka Kalkulator" di layar pembuka =====
        prefsManager = new PrefsManager(this);
        layoutStart = findViewById(R.id.layoutStart);
        layoutCalculator = findViewById(R.id.layoutCalculator);
        Button btnOpenCalculator = findViewById(R.id.btnOpenCalculator);

        btnOpenCalculator.setOnClickListener(v -> {
            layoutStart.setVisibility(View.GONE);
            layoutCalculator.setVisibility(View.VISIBLE);
            prefsManager.saveCalculatorOpened(true);
        });

        // ===== Bagian 2: Kalkulator =====
        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        setupNumberButton(R.id.btn0, "0");
        setupNumberButton(R.id.btn1, "1");
        setupNumberButton(R.id.btn2, "2");
        setupNumberButton(R.id.btn3, "3");
        setupNumberButton(R.id.btn4, "4");
        setupNumberButton(R.id.btn5, "5");
        setupNumberButton(R.id.btn6, "6");
        setupNumberButton(R.id.btn7, "7");
        setupNumberButton(R.id.btn8, "8");
        setupNumberButton(R.id.btn9, "9");

        Button btnDecimal = findViewById(R.id.btnDecimal);
        btnDecimal.setOnClickListener(v -> onDecimalClick());

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnSubtract = findViewById(R.id.btnSubtract);
        Button btnMultiply = findViewById(R.id.btnMultiply);
        Button btnDivide = findViewById(R.id.btnDivide);
        btnAdd.setOnClickListener(v -> onOperatorClick("+"));
        btnSubtract.setOnClickListener(v -> onOperatorClick("-"));
        btnMultiply.setOnClickListener(v -> onOperatorClick("×"));
        btnDivide.setOnClickListener(v -> onOperatorClick("÷"));

        Button btnClear = findViewById(R.id.btnClear);
        Button btnBackspace = findViewById(R.id.btnBackspace);
        Button btnPlusMinus = findViewById(R.id.btnPlusMinus);
        Button btnEquals = findViewById(R.id.btnEquals);
        Button btnPercent = findViewById(R.id.btnPercent);

        btnClear.setOnClickListener(v -> onClearClick());
        btnBackspace.setOnClickListener(v -> onBackspaceClick());
        btnPlusMinus.setOnClickListener(v -> onPlusMinusClick());
        btnEquals.setOnClickListener(v -> onEqualsClick());
        btnPercent.setOnClickListener(v -> onPercentClick());

        // updateDisplay() sengaja tidak dipanggil di sini: onResume() yang membaca
        // data tersimpan lalu menampilkannya (kalau dipanggil, data lama tertimpa "").
    }

    /** Dibaca di onResume(): pulihkan layar & rumus terakhir dari SharedPreferences */
    @Override
    protected void onResume() {
        super.onResume();
        expression = prefsManager.getExpression();
        if (prefsManager.isCalculatorOpened()) {
            layoutStart.setVisibility(View.GONE);
            layoutCalculator.setVisibility(View.VISIBLE);
        }
        updateDisplay();
    }

    private void setupNumberButton(int buttonId, String digit) {
        Button btn = findViewById(buttonId);
        btn.setOnClickListener(v -> onDigitClick(digit));
    }

    // ===== Tombol angka: tinggal ditempel ke akhir rumus =====
    private void onDigitClick(String digit) {
        if (expression.length() < 20) {
            expression += digit;
        }
        updateDisplay();
    }

    // ===== Tombol koma (,) =====
    private void onDecimalClick() {
        String lastNumber = getLastNumberSegment();
        if (lastNumber.contains(",")) return; // sudah ada koma, jangan tambah lagi
        expression += lastNumber.isEmpty() ? "0," : ",";
        updateDisplay();
    }

    // ===== Tombol operator (+ - × ÷) =====
    private void onOperatorClick(String operator) {
        if (expression.isEmpty()) {
            // Boleh mulai dengan "-" untuk angka negatif
            if (operator.equals("-")) expression = "-";
            updateDisplay();
            return;
        }
        char lastChar = expression.charAt(expression.length() - 1);
        if (isOperator(lastChar)) {
            // Ganti operator terakhir kalau user tekan operator 2x berturut-turut
            expression = expression.substring(0, expression.length() - 1) + operator;
        } else {
            expression += operator;
        }
        updateDisplay();
    }

    // ===== Tombol "=" =====
    private void onEqualsClick() {
        Double result = evaluateExpression(expression);
        if (result != null) {
            expression = formatResult(result);
        }
        updateDisplay();
    }

    // ===== Tombol AC =====
    private void onClearClick() {
        expression = "";
        updateDisplay();
    }

    // ===== Tombol backspace =====
    private void onBackspaceClick() {
        if (!expression.isEmpty()) {
            expression = expression.substring(0, expression.length() - 1);
        }
        updateDisplay();
    }

    // ===== Tombol +/- : hanya berlaku untuk angka PERTAMA (sebelum ada operator) =====
    private void onPlusMinusClick() {
        if (expression.isEmpty() || hasOperatorAfterStart(expression)) return;
        if (expression.startsWith("-")) {
            expression = expression.substring(1);
        } else {
            expression = "-" + expression;
        }
        updateDisplay();
    }

    // ===== Tombol % : ubah angka terakhir jadi bentuk persen =====
    private void onPercentClick() {
        String lastNumber = getLastNumberSegment();
        if (lastNumber.isEmpty()) return;
        String beforePart = expression.substring(0, expression.length() - lastNumber.length());
        double value = Double.parseDouble(lastNumber.replace(",", "."));
        expression = beforePart + formatResult(value / 100);
        updateDisplay();
    }

    /** Memperbarui tampilan rumus (besar) dan hasil sementara (kecil) */
    private void updateDisplay() {
        prefsManager.saveExpression(expression); // apply(): simpan setiap rumus berubah
        tvExpression.setText(expression.isEmpty() ? "0" : expression);

        boolean endsWithOperator = !expression.isEmpty() && isOperator(expression.charAt(expression.length() - 1));
        Double liveResult = evaluateExpression(expression);

        if (liveResult != null && hasOperatorAfterStart(expression) && !endsWithOperator) {
            tvResult.setText(formatResult(liveResult));
        } else {
            tvResult.setText("");
        }
    }

    /** Ambil segmen angka terakhir (setelah operator terakhir, kalau ada) */
    private String getLastNumberSegment() {
        int i = expression.length() - 1;
        while (i >= 0 && !isOperator(expression.charAt(i))) {
            i--;
        }
        return expression.substring(i + 1);
    }

    /** Cek apakah ada operator SELAIN tanda minus di depan (index 0) */
    private boolean hasOperatorAfterStart(String expr) {
        for (int i = 1; i < expr.length(); i++) {
            if (isOperator(expr.charAt(i))) return true;
        }
        return false;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '×' || c == '÷';
    }

    /**
     * Memecah rumus jadi angka-angka dan operator (Lesson 4: Method & Array/List),
     * lalu menghitungnya berurutan dari kiri ke kanan memakai Calculator.calculate()
     */
    private Double evaluateExpression(String expr) {
        if (expr.isEmpty()) return null;

        List<String> tokens = tokenize(expr);
        if (tokens.isEmpty() || tokens.size() % 2 == 0) return null; // harus diakhiri angka

        try {
            double result = Double.parseDouble(tokens.get(0).replace(",", "."));
            for (int i = 1; i < tokens.size(); i += 2) {
                String operator = tokens.get(i);
                double next = Double.parseDouble(tokens.get(i + 1).replace(",", "."));
                result = calculator.calculate(result, next, operator);
            }
            return result;
        } catch (Exception e) {
            // Contoh: pembagian nol, atau angka belum lengkap saat diketik
            return null;
        }
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (isOperator(c) && i != 0) {
                tokens.add(currentNumber.toString());
                tokens.add(String.valueOf(c));
                currentNumber.setLength(0);
            } else {
                currentNumber.append(c);
            }
        }
        if (currentNumber.length() > 0) {
            tokens.add(currentNumber.toString());
        }
        return tokens;
    }

    private String formatResult(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);
        } else {
            return String.valueOf(value).replace(".", ",");
        }
    }
}