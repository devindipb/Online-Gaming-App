package com.example.tictactoeonline

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.tictactoeonline.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityGameBinding

    private var gameModel : GameModel? = null

    var xScore:Int = 0
    var oScore:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)

        binding.startGameBtn.setOnClickListener {
            startGame()
        }

        GameData.gameModel.observe(this){
            gameModel = it
            setUI()
        }
    }



    private fun saveScore( xScore: Int,oScore:Int) {
        val sharedPref = this.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.putInt("xScore", xScore)
        editor?.putInt("oScore", oScore)
        editor?.apply()
    }

    // This function retire the saved appScore and userScore
    private fun getScore() {
        val sharedPref = this.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
        xScore = sharedPref.getInt("xScore", 0)
        findViewById<TextView>(R.id.textView).text = xScore.toString()
        oScore = sharedPref.getInt("oScore", 0)
        findViewById<TextView>(R.id.textView2).text = oScore.toString()

    }

    // on app launching the this function assign all retired data to app
    override fun onResume() {
        super.onResume()
        getScore()
    }

    // on app closing the this function save all playerScore and appScore
    override fun onPause() {
        super.onPause()
        saveScore(xScore,oScore)
    }




    fun setUI(){
        gameModel?.apply{
            binding.btn0.text = filledPos[0]
            binding.btn1.text = filledPos[1]
            binding.btn2.text = filledPos[2]
            binding.btn3.text = filledPos[3]
            binding.btn4.text = filledPos[4]
            binding.btn5.text = filledPos[5]
            binding.btn6.text = filledPos[6]
            binding.btn7.text = filledPos[7]
            binding.btn8.text = filledPos[8]

            binding.startGameBtn.visibility = View.VISIBLE

            binding.gameStatusText.text =
                when(gameStatus){
                    GameStatus.CREATED -> {
                        binding.startGameBtn.visibility = View.INVISIBLE
                        "Game ID :"+ gameId
                    }
                    GameStatus.JOINED -> {
                        "Click on start game"
                    }
                    GameStatus.INPROGRESS -> {
                        binding.startGameBtn.visibility = View.INVISIBLE
                        currentPlayer + " turn "
                    }
                    GameStatus.FINISHED -> {
                        if (winner.isNotEmpty()){
                            if(winner == "X"){
                                xScore++
                                val xscore = findViewById<TextView>(R.id.textView2)
                                xscore.text = xScore.toString()
                            }
                            if(winner == "O"){
                                oScore++
                                val oscore = findViewById<TextView>(R.id.textView)
                                oscore.text = oScore.toString()
                            }
                        }
                        if(winner.isNotEmpty()) {
                            winner + "Won"

                        }
                        else "DRAW"
                    }
                }
        }
    }

    fun startGame(){
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS
                )
            )
        }
    }
    fun updateGameData(model : GameModel){
        GameData.saveGameModel(model)
    }

    fun checkForWinner(){
        val winningPos = arrayOf(
            intArrayOf(0,1,2),
            intArrayOf(3,4,5),
            intArrayOf(6,7,8),
            intArrayOf(0,3,6),
            intArrayOf(1,4,7),
            intArrayOf(2,5,8),
            intArrayOf(0,4,8),
            intArrayOf(2,4,6),
        )
        gameModel?.apply {
            for (i in winningPos){

                if(
                    filledPos[i[0]] == filledPos[i[1]] &&
                    filledPos[i[1]] == filledPos[i[2]] &&
                    filledPos [i[0]].isNotEmpty()
                ) {
                    gameStatus = GameStatus.FINISHED
                    winner = filledPos[i[0]]
                }
            }

            if( filledPos.none(){ it.isEmpty() }){
                gameStatus = GameStatus.FINISHED
            }

            updateGameData(this)
        }

    }


    override fun onClick(v: View?) {
        gameModel?.apply {
            if(gameStatus != GameStatus.INPROGRESS){
                Toast.makeText(applicationContext, "Game not started", Toast.LENGTH_SHORT).show()
                return
            }

            //game is in progress
            val clickedPos = (v?.tag as String).toInt()
            if(filledPos[clickedPos].isEmpty()){
                filledPos[clickedPos] = currentPlayer
                currentPlayer = if(currentPlayer == "X") "O" else "X"
                checkForWinner()
                updateGameData(this)
            }
        }
    }

}