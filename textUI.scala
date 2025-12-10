package o1.adventure.ui

import o1.adventure.*
import scala.io.StdIn.*

/** The singleton object `AdventureTextUI` represents a fully text-based version of the
  * Adventure game application. The object serves as an entry point for the game, and
  * it can be run to start up a user interface that operates in the text console.
  * @see [[AdventureGUI]] */
object AdventureTextUI extends App:

  private val game = Adventure()
  private val player = game.player
  this.run()

  /** Runs the game. First, a welcome message is printed, then the player gets the chance to
    * play any number of turns until the game is over, and finally a goodbye message is printed. */
  private def run() =
    println(this.game.welcomeMessage)
    while !this.game.isOver do
      this.printTUI_HUD()
      this.playTurn()
    //println("\n" + this.game.goodbyeMessage)

  private def printTUI_HUD() =
    val area = this.player.location
    val efficiency = this.player.getStudyEfficiency
    val phoneStatus = if this.player.has("phone") then "in your pocket" else "GONE (SAD)"

    //Core Status Panel
    println("\n" + "="*50)
    println(s"Day: ${this.game.currentDay} / ${this.game.maxDays}  |  AP: ${this.player.getAP} / 12  |  Score: ${this.player.getStudyPoints}")
    println(s"Mood: ${this.player.getMood} (${this.player.getMoodDescription}) | Study efficiency: $efficiency | Phone: $phoneStatus")
    println("="*30)

    //Environment Status Panel
    if area.name == "teacherOffice" then
      println("\n[Battlefield Status]")
      // 调用 examine("")能直接返回老师的状态
      println(this.player.examine("") + "\n")

    //位置信息
    println(area.name)
    println("-" * area.name.length)

    // 不在office的话examine("")会返回完整的区域描述
    // 如果在办公室 上面已经打印过状态了, 可打印完整描述了
    if area.name != "teacherOffice" then
      println(area.fullDescription + "\n")
    else
      //用 fullDescription 来显示出口
      println(area.fullDescription + "\n")


    // Dynamic Command Panel
    val commands = this.game.getAvailableCommands()
    println("--- Available commands ---")
    println(commands.mkString(", "))
  end printTUI_HUD


  /** Prints out a description of the player character’s current location, as seen by the character. */
  /*private def printAreaInfo() =
    val area = this.player.location
    println("\n\n" + area.name)
    println("-" * area.name.length)
    println(area.fullDescription + "\n")

    //动态指令菜单实现
    val commands = this.game.getAvailableCommands()
    println("--- 当前可用指令 ---")
    println(commands.mkString(", "))

  /** Requests a command from the player, plays a game turn accordingly, and prints out a
    * report of what happened.  */
  */
  private def playTurn() =
    println()
    val command = readLine("Command: ")
    val turnReport = this.game.playTurn(command)
    if turnReport.nonEmpty then
      println(turnReport)


end AdventureTextUI

