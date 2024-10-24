package com.example.investeasy

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.investeasy.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnReset.setOnClickListener {
            reset()
        }

        binding.btnCalculate.setOnClickListener {
            val contributionTemp = binding.tieContribution.text
            val monthsTemp = binding.tieMonths.text
            val fessTemp = binding.tieFees.text

            if (emptyOrNullFields(contributionTemp, monthsTemp, fessTemp)) {
                val messageEmptyField = getString(R.string.message_empty_field)
                Snackbar.make(
                    binding.tieContribution, messageEmptyField, Snackbar.LENGTH_LONG
                ).show()
            } else {
                val contribution: Double = contributionTemp.toString().toDouble()
                val months: Int = monthsTemp.toString().toInt()
                val fess: Double = fessTemp.toString().toDouble()

                if (!isValidValue(contribution) || !isValidValue(months.toDouble()) || (!isValidValue(
                        fess
                    ))
                ) {
                    val messageZeroField = getString(R.string.message_zero_field)
                    Snackbar.make(
                        binding.tieContribution, messageZeroField, Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    val (valueReceived, incomeValue) = calculateInvestment(
                        contribution,
                        months,
                        fess
                    )
                    val formattedValueReceived = formatCurrency(valueReceived)
                    val formattedIncomeValue = formatCurrency(incomeValue)
                    binding.tvValueReceived.text = "R$ $formattedValueReceived"
                    binding.tvIncomeValue.text = "R$ $formattedIncomeValue"
                }
            }
        }
    }

    private fun calculateInvestment(
        contribution: Double,
        months: Int,
        fess: Double
    ): Pair<Double, Double> {
        var valueReceived = 0.0
        var incomeValue = 0.0
        for (month in 1..months) {
            val monthlyIncome = valueReceived * (fess / 100)
            valueReceived += monthlyIncome
            valueReceived += contribution
            incomeValue += monthlyIncome
        }
        return Pair(valueReceived, incomeValue)
    }

    private fun emptyOrNullFields(vararg fields: CharSequence?): Boolean {
        return fields.any { it.isNullOrEmpty() }
    }

    private fun isValidValue(value: Double): Boolean {
        return value > 0
    }

    private fun formatCurrency(value: Double): String {
        val numberFormat = NumberFormat.getInstance(Locale("pt", "BR"))
        numberFormat.maximumFractionDigits = 2
        return numberFormat.format(value)
    }

    private fun TextInputEditText.clearText() {
        this.setText("")
    }

    private fun reset() {
        binding.tvValueReceived.text = "0.0"
        binding.tvIncomeValue.text = "0.0"
        binding.tieContribution.clearText()
        binding.tieMonths.clearText()
        binding.tieFees.clearText()

        binding.tieContribution.requestFocus()
    }
}