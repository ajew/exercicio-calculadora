package br.ufpe.cin.if710.calculadora

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val mathExpression = findViewById<EditText>(R.id.text_calc)
        /*val button = findViewById<Button>(R.id.btn_0)
        button.setOnClickListener {
            // your code to perform when the user clicks on the button
            mathExpression.append("0")
            Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        }*/

        btn_0.setOnClickListener{ text_calc.text = text_calc.text.append('0') }
        btn_1.setOnClickListener{ text_calc.text = text_calc.text.append('1') }
        btn_2.setOnClickListener{ text_calc.text = text_calc.text.append('2') }
        btn_3.setOnClickListener{ text_calc.text = text_calc.text.append('3') }
        btn_4.setOnClickListener{ text_calc.text = text_calc.text.append('4') }
        btn_5.setOnClickListener{ text_calc.text = text_calc.text.append('5') }
        btn_6.setOnClickListener{ text_calc.text = text_calc.text.append('6') }
        btn_7.setOnClickListener{ text_calc.text = text_calc.text.append('7') }
        btn_8.setOnClickListener{ text_calc.text = text_calc.text.append('8') }
        btn_9.setOnClickListener{ text_calc.text = text_calc.text.append('9') }
        btn_Clear.setOnClickListener{ text_calc.text = text_calc.text.dropLast(text_calc.text.length) as Editable? }
        btn_Add.setOnClickListener{ text_calc.text = text_calc.text.append('+')}
        btn_Subtract.setOnClickListener{ text_calc.text = text_calc.text.append('-')}
        btn_Multiply.setOnClickListener{ text_calc.text = text_calc.text.append('*')}
        btn_Divide.setOnClickListener{ text_calc.text = text_calc.text.append('/')}
        btn_Dot.setOnClickListener{ text_calc.text = text_calc.text.append('.')}
        btn_Power.setOnClickListener{ text_calc.text = text_calc.text.append('^')}
        btn_RParen.setOnClickListener{ text_calc.text = text_calc.text.append(')')}
        btn_LParen.setOnClickListener{ text_calc.text = text_calc.text.append('(')}
        btn_Equal.setOnClickListener{
            try {
                val valor:String=""+eval(text_calc.text.toString())
                text_info.text = valor
            } catch(e: RuntimeException){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putString("calc", text_calc.text.toString())
            outState.putString("info", text_info.text as String?)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState!=null){
            val tex_info:String=savedInstanceState.getString("info")
            val tex_calc:String=savedInstanceState.getString("calc")
            text_info.setText(tex_info)
            text_calc.setText(tex_calc)
        }

    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }
}
