package com.example.lottonumberdraw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    //  각 버튼 연결
    private val clearButton : Button by lazy { // by lazy : 변수를 선언하고 나중에 사용하겠다는 의미
        findViewById<Button>(R.id.clearButton)
    }
    private val addButton : Button by lazy {
        findViewById(R.id.addButton)
    }
    private val runButton : Button by lazy {
        findViewById(R.id.runButton)
    }
    private val numberPickr : NumberPicker by lazy {
        findViewById(R.id.numberPicker)
    }

    private val numberTextViewList : List<TextView> by lazy {
        listOf( // 6개의 textView가 차례로 쌓이면서 초기화가 가능함
            findViewById(R.id.TextView1),
            findViewById(R.id.TextView2),
            findViewById(R.id.TextView3),
            findViewById(R.id.TextView4),
            findViewById(R.id.TextView5),
            findViewById(R.id.TextView6)
        )
    }

    private var didRun = false // false일 때 번호 선택 및 추가가 가능함
    private val pickNumberSet = hashSetOf<Int>() // 중복으로 숫자가 들어갈 수 없도록 || 중복을 막으려면 set을 사용하기
                                                 // mutableSet도 사용 가능함

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 번호 최소, 최대 범위를 정해주기
        numberPickr.minValue = 1
        numberPickr.maxValue = 45

        initRunButton()
        intitAddButton()
        initClearButton()
    }
    // 자동생성 번호
    private fun initRunButton() {
        runButton.setOnClickListener {
            val list = getRandomNumber()

            didRun = true
            // 자동생성 나타내기
            list.forEachIndexed { // 자동생성 나타내기 forEach만 사용하면 몇 번째 인덱스인지 모름
                                  // index가 필요한 이유는 textView를 초기화 해주기 위해서
                index, number ->
                val textView = numberTextViewList[index] // text값 빼내오고
                textView.text = number.toString() // string으로 변환하여
                textView.isVisible = true // 화면에 보이게 한다.
                // 자동생성 번호에 컬러입히기
                drawbleBackgroud(number, textView)

            }
            Log.d("Mainactivity", list.toString()) // 랜덤 숫자가 잘 들어가 있는지 확인하기
        }
    }
    // 번호추가하기 버튼 함수
    private fun intitAddButton() {
        addButton.setOnClickListener {
            if(didRun) { // 랜던 숫자를 더 생성할 수 없는 경우
                Toast.makeText(this, "초기화 후 다시 숫자를 돌려주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Toast이후에 반드시 return 값 주기
            }
            if(pickNumberSet.size >= 6) {
                Toast.makeText(this, "번호는 6개까지 선택이 가능합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(pickNumberSet.contains(numberPickr.value)) { // 숫자를 선택할 때 같은 숫자를 선택하지 않도록
                Toast.makeText(this, "이미 선택한 번호입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val textView = numberTextViewList[pickNumberSet.size] // 하나도 안 뽑았다면 set의 size는 0, 뽑았다면 뽑은 만큼
                                                                  // size가 늘어나게 되어서 결국에는 pickNumberSet이 숫자의 위지가 됨
            textView.isVisible = true
            textView.text = numberPickr.value.toString()
            // numberPicker를 선택한 다음 컬러 입히기
            drawbleBackgroud(numberPickr.value, textView)
            pickNumberSet.add(numberPickr.value)
        }
    }
    //  when문을 통해서 숫자마다 drawble color 설정해주기
    private fun drawbleBackgroud(number : Int, textView: TextView) {
        when(number) {
            in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.one_yellow) // drawble 컬러 값을 넣어주는 방법
            in 11..20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.one_blue)
            in 21..30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.one_red)
            in 31..40 -> textView.background = ContextCompat.getDrawable(this, R.drawable.one_gray)
            else -> textView.background = ContextCompat.getDrawable(this, R.drawable.oen_green)

        }

    }

    // 숫자를 랜덤으로 돌리는 함수
    // apply는 주로 초기화 및 데이터를 미리 지정할 때 주로 사용함
    private fun getRandomNumber() : List<Int> {
        val numberList = mutableListOf<Int>()
            .apply { // apply를 사용했던 객체 자체를 this로 사용할 수 있음
                for (i in 1..45) {
                    if(pickNumberSet.contains(i)) {
                        continue // 이미 있는 번호라면 제외하고 다시 반복을 돌려 추가하기
                    }
                    this.add(i)
                }
            }
        // 셔플을 통해서 무작위로 섞은 다음
        numberList.shuffle()
        val newList = pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size) // 0부터 6개의 숫자를 sublist로 추출하기
                                                                                             // pickNumberSet.size 만큼 빼주는 이유 = 기존에 선택한 숫자는 그대로 있으며, 뒤는 자동생성을 할 경우
                                                                                             // 이미 set에 들어가 있는 숫자는 빼주도록 함
        return newList.sorted() // 6개의 수를 오름차순으로 정렬
    }
    // 초기화 버튼
    private fun initClearButton() {
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            numberTextViewList.forEach{
                it.isVisible = false // 아래의 text를 숨기기
            }
            didRun = false // 초기화 후에는 번호를 누를 수 있음
        }
    }

}