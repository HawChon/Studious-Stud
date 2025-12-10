package o1.adventure
import scala.util.Random

/** The class `Adventure` represents text adventure games. An adventure consists of a player and
  * a number of areas that make up the game world. It provides methods for playing the game one
  * turn at a time and for checking the state of the game.
  *
  * N.B. This version of the class has a lot of “hard-coded” information that pertains to a very
  * specific adventure game that involves a small trip through a twisted forest. All newly created
  * instances of class `Adventure` are identical to each other. To create other kinds of adventure
  * games, you will need to modify or replace the source code of this class. */
class Adventure:

  /** the name of the game */
  val title = "Studious Stud"

  private val corridor = Area("corridor",
    "The Central Corridor. The main artery of the school connecting all rooms. \nIt smells of floor wax and teenage angst.")
  private val classroom = Area("classroom",
    "The Classroom. The battlefield where you burn your youth for grades.\n(Hint: Too stressed? Try to 'sneak' into the lab to use your phone. Just don't get caught!)")
  private val dormitory = Area("dormitory",
    "The Dormitory. Your sanctuary. The only place to recover AP.\nIf you don't have a phone, you can just hide under the duvet and cry.")
  private val diningHall = Area("diningHall",
    "The Dining Hall. The air smells of mystery meat.\nYou wonder what culinary experiments await you today...")
  private val teacherOffice = Area("teacherOffice",
    "The Teacher's Office. The Dragon's Lair.\nYour confiscated phone—along with your lost soul and dignity—is likely imprisoned here.")

  private val flavorTexts = Map(

  "corridor" -> Vector(

    "The corridor air is thick with the scent of teenage hormones and unwashed uniforms.",
    "The class crush walks by. You try to act cool, but realize 5 seconds later you were staring with your mouth open.",
    "The Dean is patrolling the end of the hall like a prison warden. Your hand instinctively guards the phone in your pocket.",
    "A giant red banner hangs outside: 'SUFFER TODAY, CONQUER TOMORROW!' It looks like it was written in blood.",
    "You see a couple holding hands. Disgusting. You sincerely pray they fail the exams and end up flipping burgers together.",
    "You pass the 'Wall of Fame.' You sneer at the top student's photo. 'Sad, soulless try-hard,' you mutter, holding back tears of envy.",
    "You walk past a group of girls. You wait for a dialogue option window to pop up like in Galgame. It doesn't. Reality is broken.",
    "You find a crumpled love letter on the floor. You do the right thing: you slip it into the Principal's suggestion box. Let chaos reign."

  ),


  "dininghall" -> Vector(
  "The cafeteria lady has a mysterious hand tremor that *only* activates when serving meat. You watch two pieces of chicken fall back into the tray. Tragedy.",
  "There's a puddle of unidentified sticky liquid on the floor. Soup? Tears? Chemical waste? You carefully step around it.",
  "The school anthem is blasting again. It sounds like bad propaganda. You wonder which psychopath wrote lyrics praising this academic prison.",
  "Radio is playing 'Dango Daikazoku'! You look at the slope outside, hoping for a fateful anime encounter... but it's just a guy eating a cold sandwich.",
  "Disaster. The food line stretches out the door. You can practically feel your Action Points draining away just by looking at it.",
  "The new menu item is a culinary war crime: 'Pickled Bamboo with Pure Pork Fat.' Your taste buds scream in betrayal."
  ),

  "classroom" -> Vector(
  "On the blackboard: '30 DAYS UNTIL GAOKAO.' The countdown is written in red chalk. It looks disturbingly like a bloodstain.",
  "The students in the back row have built a fortress out of books. Are they gaming? Sleeping? It is Schrödinger's Student.",
  "The air composition in here is 40% Carbon Dioxide, 10% Oxygen, and 50% pure Anxiety.",
  "You put your head down to nap. Your delinquent friend 'Niu Er' calls you from the hallway for a 'patrol' (skipping class). You ignore him. Sleep is sacred.",
  "The greasy sweet bun from breakfast is sitting in your stomach like a brick. A wave of nausea hits you.",
  "You gaze longingly out the window at the Science Lab. The door is ajar. If you could just sneak over there... it's the Promised Land.",
  "A propaganda poster screams: 'The Exam happens only once! Life is renewable! Study or Die!' logic is not their strong suit.",
  "The Class Monitor walks in, heading straight for you... \n...\n...and confiscates the phone from the guy BEHIND you. Your heart resumes beating after a 10-second pause."
  ),

  "dormitory" -> Vector(

  "The Dorm Matron is screaming orders. 'Lights out! Talking = Point deduction! Showering late = Eviction!' It feels like minimum-security prison.",
  "You spent two hours last night grinding that Visual Novel. Finally unlocked the 'True Love' route. Worth the sleep deprivation. Heh heh.",
  "Someone started a 'shouting match' between dorm buildings to vent stress. You took advantage of the chaos to scream your crush's name into the void.",
  "You walk in. Your roommates are gathered around your bed like it's a funeral... Oh. A giant cockroach just breached your mosquito net.",
  "Lying in bed, you contemplate your future. 'How am I this handsome and still single?' You feel genuinely sorry for the girls who are missing out on you."

  ),
  "teacheroffice" -> Vector(

  "One second ago, you were cursing the entire faculty. The moment you open the door: 'Good afternoon, Sir! So nice to see you!' \nYou are a master of hypocrisy.",
  "Entering this office requires a saving throw against Fear. You stare at the doorknob for two full minutes before building up the nerve to turn it.",
  "You see a student standing next to the Grade Head's desk, head hanging low. He is getting absolutely roasted. Poor soul.",
  "While the Head Teacher yells, the Chemistry teacher at the next desk is just giggling and watching TikTok dancers. The duality of man."
   )
  )

  def getRandomFlavorText(locationName: String): String =

    if Random.nextDouble() < 0.3 then //30% can random a piece of flavour text.
      flavorTexts.get(locationName) match
        case Some(texts) => "\n>>> " + texts(Random.nextInt(texts.length))
        case None => ""
    else
      ""

  
  val phone = Item("phone", "--My most precious treasure--")
  dormitory.addItem(phone)
  
  corridor.setNeighbors(Vector("classroom" -> classroom, "dormitory" -> dormitory, "dininghall" -> diningHall, "office" -> teacherOffice))
// 其他地方都只能返回走廊
  classroom.setNeighbor("corridor", corridor)
  dormitory.setNeighbor("corridor", corridor)
  diningHall.setNeighbor("corridor", corridor)
  teacherOffice.setNeighbor("corridor", corridor)


  /** The character that the player controls in the game. */
  val player = Player(dormitory, teacherOffice)

  var currentDay = 1
  val maxDays = 30  //


  /** Determines if the adventure is complete, that is, if the player has won. */
  def isComplete = this.currentDay > this.maxDays && this.player.isAlive  //捱到天数而且角色没死

  /** Determines whether the player has won, lost, or quit, thereby ending the game. */
  def isOver = !this.player.isAlive || this.player.hasQuit || this.currentDay > this.maxDays

  /** Returns a message that is to be displayed to the player at the beginning of the game. */
  def welcomeMessage: String = """
|================================================================================
|                         S T U D I O U S   S T U D
|                     (The Hardcore Academic Survival Sim)
|================================================================================
|
| Welcome, you magnificent creature.
|
| You are a "Stud", an incredibly handsome (At least you believe) Chinese student.
| But in this high-pressure boarding Chinese high school, your face won't save you.
| Only your SCORES can!
|
| --- YOUR MISSION ---
| [1] SURVIVE: You have exactly 30 Days until the "Gaokao" (The Doomsday exam.)
| [2] GRIND:   Maximize your StudyPoints. No target score.
| [3] MANAGE:  You have 12 Action Points (AP) per day. Spend them wisely.
|   * The Gaokao is China's national college entrance examination.
|     It determines your university admission.
|
| --- CRITICAL WARNINGS ---
| > MOOD IS LIFE: If your Mood drops to 0, you suffer a Mental Breakdown. GAME OVER.
| > EFFICIENCY:   High Mood = High Efficiency. Low Mood = Brain Fog.
| > THE PHONE:    Your only source of joy. Do NOT let the teachers catch you with it.
|
| "The world doesn't care how hot you are. Go study."
|
| Type 'help' for commands. Good luck, Stud.
|================================================================================
""".stripMargin


  private def calculateMoodBonus(): Int =
    val mood = this.player.getMood
    if mood > 90 then 20 + Random.nextInt(31)       // 状态绝佳，加分 20~49
    else if mood > 80 then 10 + Random.nextInt(21)  // 良好，加分 10~29
    else if mood > 50 then Random.nextInt(6)   // 普通，加分 0~5
    else if mood > 30 then  -5 - Random.nextInt(16) // 低落，减分 5~20
    else if mood > 10 then -20 - Random.nextInt(31) // 糟糕，减分 20~5    0
    else -50 - Random.nextInt(50)                  // 崩溃边缘，大扣分！

  // 负责根据最终分数发放“录取通知书”
  private def getAdmissionResult(score: Int): String =

    if score >= 680 then
      "【ADMISSION LETTER】\nACCEPTED to 'Tsinghua University' (The MIT of China)!\nMajor: Civil Engineering.\nIt is widely considered a career trap, but hey, the diploma looks nice on the wall!"
    else if score >= 650 then
      "【ADMISSION LETTER】\nACCEPTED to 'Tongji University' (Tier 1)!\nYour parents promised you an RTX 5090 for this. WORTH. EVERY. SECOND."
    else if score >= 600 then
      "【ADMISSION LETTER】\nACCEPTED to 'Shenzhen University'.\nYou were ready for the '996' grind at Tencent HQ nearby...\nBut you got auto-transferred to the 'Preschool Education' major. RIP stock options."
    else if score >= 520 then
      "【ADMISSION LETTER】\nACCEPTED to a generic Public University.\nWelcome to a perfectly average life. No glory, but no stress. You are now a background NPC."
    else if score >= 400 then
      "【ADMISSION LETTER】\nACCEPTED to a Private College (Tier 3).\nThe tuition is astronomical. Better start flipping burgers this summer to pay off the debt."
    else if score >= 200 then
      "【ADMISSION LETTER】\nACCEPTED to 'Lanxiang Vocational School'.\nMajor: Advanced Excavator Manipulation.\nYou will be the coolest guy at the construction site."
    else
      "【REJECTION】\nSadly, your score is too low even for vocational school.\nYou look up the recruitment number for the Foxconn electronics factory...\nOr maybe try again next year?"


  /** Returns a message that is to be displayed to the player at the end of the game. The message
    * will be different depending on whether the player has completed their quest. */
  def goodbyeMessage: String =
  if this.isComplete then
    // 基础分与Bonus计算逻辑
    val baseScore = this.player.getStudyPoints
    val moodBonus = this.calculateMoodBonus()
    val finalScore = math.max(0, baseScore + moodBonus)

    s"""
    |===================================
    |         E X A M   O V E R
    |===================================
    |VICTORY! You survived the 30-day grinder.
    |The nightmare is finally over.
    |
    |Raw Knowledge Score : $baseScore
    |Mental State Bonus  : $moodBonus  (RNG plays a part, just like real life)
    |-----------------------------------
    |FINAL GAOKAO SCORE  : $finalScore
    |
    |${this.getAdmissionResult(finalScore)}
    |===================================
    """.stripMargin

  else if !this.player.isAlive then
    // 对应 Mood <= 0 的失败条件
    """
    |CRITICAL FAILURE: Mood reached 0.
    |
    |You stood up in the middle of class and screamed:
    |"DOES P=NP REALLY MATTER IF I AM DEAD INSIDE?!"
    |
    |You have been escorted out by security for a mandatory 'Mental Health Break'.
    |Status: DROPPED OUT (Burnout).
    |GAME OVER.
    """.stripMargin
  else
    // 对应中途退出
    // 嘲讽玩家
    """
    |YOU GAVE UP.
    |
    |The pressure was too much. You walked out of the school gate and never looked back.
    |You are now a 'Street Loafer' (or a NEET).
    |Maybe the electronic factory is hiring?
    """.stripMargin


  private def startNewDay():String = //在执行sleep时需要调用此方法。
    this.currentDay += 1
    this.player.resetForNewDay()
    var dayReport = s"\n--- Day: ${this.currentDay}  ---"

    //判断有无手机
    if !this.player.has("phone") then
      this.player.adjustMood(-5)
      dayReport += "Your pocket feels too light. You miss the touch of your screen.\n" + "Digital Withdrawal Symptoms set in. (Mood -5)"

    dayReport




  def getAvailableCommands(): List[String] =
    //游戏的“最高优先级”状态：潜行
    if this.player.sneaking then
      // 潜行时，只允许特定的潜行指令
      return List("playtiktok", "playgalgame", "chatqq", "listenmusic", "fantasytalk", "reflecting", "exit")

    //如果没在潜行，把通用+地点+移动指令拼起来输出
    var commands = List("study", "eat", "sleep", "relax", "inventory", "look", "quit", "get", "help")

    //添加地点的专属指令
    val location = this.player.location.name
    if location == "classroom" then
      commands = commands :+ "sneak"
    else if location == "teacherOffice" then
      commands = commands :+ "wait"
      commands = commands :+ "steal"
    if this.player.has("phone") then
      commands = commands :+ "use phone"

    //添加移动指令
    val neighbors = this.player.location.neighborNames // 在Area里头加的辅助方法
    val moveCommands = neighbors.map(name => s"go $name") //

    return commands ++ moveCommands
  end getAvailableCommands


  def help: String =
    //获取当前所有可用指令
    val available = this.getAvailableCommands()

    //帮助文本
    var helpText = "--- AVAILABLE COMMANDS ---\n"

    for command <- available do
      // 处理动态指令，给动词go服务
      val verb = command.split(" ")(0)

      // 查找说明，找不到就给个默认值
      val description = commandDescriptions.getOrElse(verb, "Do something.")

      if verb == "go" then
         helpText += s"- $command : Move to that area.\n"
      else
         helpText += s"- $command : $description\n"

    helpText += "--------------------------"
    helpText
  end help


  private val commandDescriptions = Map(
    //基础指令
    "study"     -> "Spend AP to gain StudyPoints (Efficiency depends on Mood/Location).",
    "eat"       -> "Recover Mood (Best used in Dining Hall).",
    "sleep"     -> "End the day, recover AP, and update Mood.",
    "relax"     -> "Recover a small amount of Mood anywhere.",
    "inventory" -> "Check what you are carrying.",
    "look"      -> "Examine your surroundings or check Teacher's status.",
    "get"       -> "Pick up an item.",
    "drop"      -> "Drop an item.",
    "quit"      -> "Give up and drop out of school.",
    "use phone" -> "Quickly check your phone. (Risk varies by location!)",

    // 移动指令
    "go"        -> "Move to a neighboring area.",

    //地点专属
    "sneak"     -> "Attempt to hide in the physical lab to play with your phone.",
    "wait"      -> "Wait in the Office to shuffle the Teacher's status.",
    "steal"     -> "Attempt to steal your phone back in the Office.",

    //潜行专属
    "playtiktok"  -> "Watch short videos.",
    "playgalgame" -> "Immerse in 2D love.",
    "chatqq"      -> "Chat with friends.(QQ is a very popular social app in China among youths, similar to TG)",
    "listenmusic" -> "Listen to sad songs.",
    "fantasytalk" -> "Delusional daydreaming.",
    "reflecting"  -> "Think about life.",
    "exit"        -> "Stop sneaking and return to classroom."
  )

  /** Plays a turn by executing the given in-game command, such as “go west”. Returns a textual
    * report of what happened, or an error message if the command was unknown. In the latter
    * case, no turns elapse. */
  def playTurn(command: String): String =
    val action = Action(command)  // 负责传入命令
    val verb = command.trim.toLowerCase.takeWhile(_ != ' ') // 获取动词
    if verb == "help" then
      return this.help

    var outcomeReport: Option[String] = None // 初始化

    if this.player.sneaking then
      // 如果玩家在潜行...
      val validSneakCommands = Set("exit", "playtiktok", "playgalgame", "chatqq", "listenmusic", "fantasytalk", "reflecting") //

      if validSneakCommands.contains(verb) then
        // 是合法的潜行指令,可以用！
        outcomeReport = action.execute(this.player)
      else
        // 不是合法的潜行指令
        outcomeReport = Some("Hey! You are strictly in 'Sneak Mode' right now.\n" +
        "If you're going to break the rules, do it properly!\n" +
        "Don't waste this crime on boring stuff. Play with your phone!")

    else
      //如果玩家不在潜行...就用之前的
      outcomeReport = action.execute(this.player)  //执行完动作之后，应该都会有String的返回的。


    // 统一处理执行结果
    var turnReport = outcomeReport.getOrElse(s"""Invalid Command: "$command".""")

    if verb == "go" && !turnReport.contains("can't") then
       turnReport += this.getRandomFlavorText(this.player.location.name)

    //原有的day end逻辑
    if this.player.isAsleep && !this.isOver then
    turnReport += this.startNewDay()

    if this.isOver then
    turnReport += "\n\n" + this.goodbyeMessage

    turnReport

  end playTurn

  
  
    
    
end Adventure

