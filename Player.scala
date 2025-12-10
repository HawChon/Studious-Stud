package o1.adventure

import scala.collection.mutable.Map
import scala.util.Random

/** A `Player` object represents a player character controlled by the real-life user
  * of the program.
  *
  * A player object’s state is mutable: the player’s location and possessions can change,
  * for instance.
  *
  * @param startingArea  the player’s initial location */
class Player(startingArea: Area,private val office: Area):

  private var currentLocation = startingArea        // gatherer: changes in relation to the previous location
  private var quitCommandGiven = false              // one-way flag
  private val bag = Map[String, Item]()   //用来存东西


  //下边是帅比的状态变量

  private var ap: Int = 12
  private var mood: Int = 80
  private var studyPoints: Int = 0
  private var consecutiveMeals: Int = 0
  private var Asleep = false
  private var isSneaking = false

  //下边是帅比的各个状态管理方法：

  //返回当前 AP
  def getAP: Int = this.ap

  //返回当前 Mood
  def getMood: Int = this.mood

  // 返回 Mood 的文字描述
  def getMoodDescription: String =
    if this.mood >= 80 then
      "PEAK PERFORMANCE (In the Zone)" // 状态绝佳，巅峰状态
    else if this.mood >= 60 then
      "Stable，good"                  // 心情不错，稳定
    else if this.mood >= 30 then
      "Mildly Depressed"               // 有点低落，轻度抑郁
    else if this.mood >= 10 then
      "High Stress Warning"            // 非常糟糕，高压警报！
    else
      "MENTAL BREAKDOWN (Brain.exe has stopped working)"

  def getStudyEfficiency: Double =   //mood影响学习状态

    if this.mood >= 80 then 1.2
    else if this.mood >= 60 then 1.0
    else if this.mood >= 30 then 0.8
    else if this.mood >= 10 then 0.6
    else 0.2

  def adjustMood(amount: Int) =  //方便外部访问修改这里的private mood
    //this.mood = this.mood + amount
    this.mood = math.max(0, math.min(100, this.mood + amount)) //安全检查，免得超出100 or 低于零整崩了

  def isAlive:Boolean =
    this.mood > 0

  def resetForNewDay()=  //重置每天时可以调用这个方法，刷新状态和AP资源
    this.ap = 12
    this.consecutiveMeals = 0
    this.wakeUp
    this.stopSneaking()
  end resetForNewDay


  def confiscatePhone(): String =    // Dedicated method for tragedy
    // Attempt to remove phone from player inventory
    val phone = this.removeItem("phone")

    phone match
      case Some(item) =>  // Player owns the phone
        this.office.addItem(item) // Send phone to Office inventory
        this.adjustMood(-20)      // Major Mood Penalty
        "BUSTED!\n" +
        "The teacher's hand moves faster than light. Your phone is gone.\n" +
        "It has been sealed in the 'TeacherOffice' dungeon.\n" +
        "You feel a part of your soul dying. (Mood -20)"

      case None => // Player DOES NOT have the phone (The Bluff)
        this.adjustMood(2)
        "The teacher grabs your wrist! ...But you are empty-handed.\n" +
        "You innocently pull out a 'Essential Vocabulary' book (which is actually a hollow cover hiding a Light Novel).\n" +
        "Teacher coughs awkwardly: 'Good... keep studying.'\n" +
        "You smirk internally. Outplayed. (Mood +2)"
  end confiscatePhone


  def sneaking: Boolean = this.isSneaking
  def startSneaking(): Unit = { this.isSneaking = true }
  def stopSneaking(): Unit = { this.isSneaking = false }

  def getStudyPoints = this.studyPoints

  def isAsleep = this.Asleep

  def wakeUp = { Asleep = false }
  def asleep = { Asleep = true }
  def apClear = { this.ap = 0 }
  def apCost = { this.ap -= 1 }


  //下边是供action调用的方法
  def study(): String =
  //检查 AP
    if this.ap < 1 then
      return "AP Insufficient. Go sleep, you zombie."
    else
      this.apCost

  //GDD逻辑
      val (efficiency, locationBonus) = this.location.name match
        case "classroom"     => (1.0, 0)   // 效率 100%
        case "teacherOffice" => (1.2, -10) // 效率 120%
        case "dormitory"     => (0.4, 0)   // 效率 40%
        case "corridor" => (0.6, 0)   // 默认在走廊 60%
        case "diningHall" => (0.5, 0)  //效率减半

      val moodEfficiency = this.getStudyEfficiency //之前添加的

      val pointsGained = 5 * efficiency * moodEfficiency // 基础 5 分
      this.studyPoints += pointsGained.round.toInt

      val lostMood = -9 + locationBonus
      this.adjustMood(lostMood) // 基础 Mood-8
      s"You studied hard in the ${this.location.name}. \n" +
      s">> Knowledge Gained : +${pointsGained.round} Points.\n" +
      s">> Mental Toll      : ${lostMood} Mood."

  end study


  def sleep(): String =

    asleep

    apClear // Sleep action resets AP to 0 immediately, Day ends

  // Calculate Mood change and generate the result report
    val (moodChange, report) = this.location.name match
      case "dormitory" => (15, "Sleep: 100/100. You slept like a baby. Recovery maxed out. (Mood +15)")
      case "classroom" => (-10, "You passed out at your desk from sheer exhaustion. A bit TOO diligent, aren't we? (Mood -10)")
      case "diningHall" => (-10, "You were woken up at dawn by the cafeteria lady banging pots. Terrible sleep quality. (Mood -10)")
      case "corridor" => (-10, "You fell asleep in the hallway... 'Does the school have homeless people now?' Students whisper as they pass. Humiliating. (Mood -10)")
      case "teacherOffice" => (-10, "You tried to sleep in the office?! You got detention for 'Disrespecting Faculty'. The threat of expulsion is real. (Mood -10)")

    adjustMood(moodChange)
    report
  end sleep


  def eat(): String =

  // Logic for diminishing returns on consecutive meals
    val (bigMeal, yourFeeling) = this.consecutiveMeals match {
      case 0 => (15, "Starving! You devoured the meal. It tastes like survival. (Mood +15)")
      case 1 => (5, "You ordered seconds. Still tastes pretty good. (Mood +5)")
      case 2 => (1, "...Do you really need this? You feel a weird sense of accomplishment, like a Mukbang streamer. (Mood +1)")
      case _ => (0, "You are going to explode. Eating another bite would be torture. No Mood gained.")
    }

    if this.ap < 1 then
      return "AP Insufficient. Go sleep, you zombie."
    else
      this.apCost
      val (moodChange, report) = this.location.name match
        case "classroom" => (2, "You sneaked some snacks between classes. The thrill makes it tastier! (Mood +2)")
        case "dormitory" => (5, "You made a cup of Instant Noodles. The smell of MSG is divine. (Mood +5)")
        case "diningHall" => (bigMeal, yourFeeling)
        case "corridor" => (2, "You grabbed a sandwich and ate while walking. Efficient, but sad. (Mood +2)")
      // Note: Eating in office is dangerous
        case "teacherOffice" => (-10, "The Grade Head caught you chewing before you could swallow. Detention for 'Eating in a Restricted Area'. (Mood -10)")

    // Only increment the meal counter if eating in the Dining Hall
      if this.location.name == "diningHall" then consecutiveMeals += 1
      adjustMood(moodChange)
      report
  end eat


  def relax(): String =

    if this.ap < 1 then
      return "AP Insufficient. Go sleep, you zombie."
    else

      this.apCost
      val (moodChange, report) = this.location.name match
        case "classroom" => (3, "You took a power nap during the 10-minute break. (Mood +3)")
        case "dormitory" => (5, "You vegetate on your bed for a while. Pure bliss. (Mood +5)")
        case "diningHall" => (3, "You stare blankly, smelling the food... wondering what's for dinner. Hope keeps you alive! (Mood +3)")
        case "corridor" => (3, "You look out the window. The weather is actually nice. (Mood +3)")
        case "teacherOffice" => (-5, "You tried to make small talk with the teacher. It backfired. Now you have extra homework. (Mood -5)")

      adjustMood(moodChange)
      report

  def sneak(): String =
    if this.ap < 1 then
      "AP Insufficient. Go sleep, you zombie."
    else

      if this.location.name != "classroom" then
        "Sneak Action Failed: You can only attempt to Sneak in 'classroom'. (Try 'go classroom' first)."
      else
        this.apCost // Deduct 1 AP

       // Entrance Roll 20%,Failure Rate
        val entranceRoll = scala.util.Random.nextDouble() // 0.0 to 1.0

        if entranceRoll < 0.20 then // Failed Entrance
          //Excuse Roll, 50% Chance to save
          val excuseRoll = scala.util.Random.nextDouble()
          if excuseRoll < 0.50 then
            // Excuse Success, Speech Check Passed
            "Sneak attempt detected! Teacher: 'What are you doing?'\nYou lie: 'Just looking for a quiet corner to recite vocabulary, Sir.'\nTeacher: '...Fine. Carry on.' (Speech Check PASSED. Phew.)"
          else
          // Excuse Failed, Confiscation
            this.confiscatePhone() // Calls the confiscation method
        else
          // 5. Sneak Success
          this.startSneaking()
          "Stealth Mode Engaged. You successfully hid in the physical lab!\n(You can now use 'playTiktok', 'chatQQ', etc.)"
  end sneak


  def exitSneak(): String =
  // Check if player is actually sneaking
    if !this.sneaking then
      return "Command Error: You are not currently sneaking. \n(If you want to leave the room, use the 'go' command. Walking out now = Detention.)"
    val exitRoll = scala.util.Random.nextDouble()

    if exitRoll < 0.15 then
    // EXIT FAILED
      this.stopSneaking() // Force exit state

      // Logic: Check if player has phone
      if this.has("phone") then
        this.confiscatePhone() // Failed + Has Phone = Game Over for the phone
      else
      this.adjustMood(2)
      "ALERT! The teacher catches you hiding in the corner!\nTeacher: 'Hand it over! I know you have it!'\nYou raise your empty hands innocently. 'I have nothing, Sir.'\nThe teacher looks extremely awkward. You feel a twisted sense of victory. (Mood +2)"

    else
    //EXIT SUCCESS
      this.stopSneaking()

      if this.has("phone") then
       // Success + Has Phone: Big Reward
       this.adjustMood(20)
        "Extraction Successful! You slipped back to your seat before anyone noticed.\nThe thrill of the heist makes you feel alive. (Mood +20)"
      else
        // Success + No Phone: Rebel without a cause
        this.adjustMood(2)
        "You successfully finished your 'imaginary' sneak session.\nYou didn't study, and you didn't play (because you have no phone).\nBut you wasted time in a cool way. You are a Rebel without a Cause. (Mood +2)"
  end exitSneak


  def playTiktok(): String =
  //
    if !this.sneaking then
      return "ACTION FAILED: You are not in Sneak Mode! Doing this openly? 'You are at the age of struggle! Why are you always on your phone?!' The teacher's voice echoes in your head."

    if this.has("phone") then
      "You doomscroll TikTok. You stumble upon a video posted by your roommate titled 'She said YES!'... \nGreat. He got a girlfriend. You are seething with jealousy. Your day is ruined. (Mood -5)"
    else
      "Item 'Phone' not found. You stare at your empty palm and swipe the air, hallucinating a viral dance video. \nYou giggle at the invisible dancers. You look absolutely insane."

  def playGalgame(): String =
    if !this.sneaking then
      return "ACTION FAILED: You are not in Sneak Mode! 'Stop wasting your youth on games!' The guilt prevents you from starting."

    if this.has("phone") then
      "You boot up 'ATRI -My Dear Moments-'. \n'Brushing teeth with Atri... Buying shoes with Atri... Creating memories...'\n(30 mins later) You almost triggered the Bad Ending. Why is dating a high-performance robot girl harder than Calculus?"
    else
      "You simulate the Visual Novel in your brain. 'I am in love with my own desire...'\nYou sniff the dust of the laboratory, overwhelmed by nostalgia for a 2D girl who doesn't exist. Tears flow."

  def chatQQ(): String =
    if !this.sneaking then
      return "ACTION FAILED: You are not in Sneak Mode! The teacher is watching. Keep your head down!"

    if this.has("phone") then
      "You open QQ and use the 'Shake' feature (Random Match). \nYou matched with a middle-aged man named 'Lonely Wolf'. He asks for a selfie. \nYou mischievously decide to send him a zoomed-in photo of your ugliest male friend."
    else
      "You pull out a piece of scratch paper and draw a Chat App interface.\nYou sketch avatars of 5 different anime waifus in a group chat. \n'Who should I message first?' You are agonizing over this paper delusion."

  def listenMusic(): String =
    if !this.sneaking then
      return "ACTION FAILED: Not in Sneak Mode! 'You are in the prime of your youth! Why waste it on a phone?!' The teacher's lecture haunts you."

    if this.has("phone") then
      "You hit play on your 'Sad Boi Hours' playlist. The music swells. \n'What is the meaning of life? Why are we here? Just to suffer?' \nYou are moved to tears by your own edginess."
    else
      "Recalling your glorious past, you start humming a melancholic tune... \nWait, is that real music? \nOH CRAP. It's the 'Nap Time Over' bell! RUN!"

  def fantasyTalk(): String =
    if !this.sneaking then
      return "ACTION FAILED: Not in Sneak Mode! 'Stop daydreaming! The exam won't take itself!'"

    if this.has("phone") then
      "You pull off a God-tier dialogue choice in the Galgame. Route secured!\nYou imagine the class crush watching over your shoulder. \n'Wow,' she'd say, 'Your virtual dating skills are so hot. You have infinite Rizz.' \n(You grin at your screen like an idiot.)"
    else
      "Atri... My most precious treasure... When will we meet again in Eden? \n(You stare at the ceiling, waiting for her hands to save you from this school.)"

  def reflecting(): String =
    if !this.sneaking then
      return "ACTION FAILED: Not in Sneak Mode! The teacher is patrolling. Look busy!"

    if this.has("phone") then
      "You see a lovey-dovey couple on the playground. Disgusting. \nYou immediately post about them on Jodel: 'Public Display of Affection should be illegal.' \nThen you look at your reflection: 'I'm so handsome. Why am I single?' The universe makes no sense."
    else
      "You tenderly stroke the chair near the air conditioner—your usual gaming throne. \nJust days ago, you were playing your 'Ranked Promos' right here. \nNow, the chair is empty. The gamer is gone. Only the student remains."
    //这是steal的方法

  def waitInOffice(): String =

    if this.ap < 1 then
      return "AP Insufficient. Go sleep, you zombie."


    if this.location.name != "teacherOffice" then
      return "You can only 'wait' inside the Teacher's Office."
    else
      this.apCost
      this.location.randomizeTeacherStatus() // Shuffle teacher state

      // Return description
      "You pretend to ask a difficult Calculus question to kill time.\n" +
      "The situation in the office shifts...\n" +
      "\n" + this.examine("") // Call examine to show new state
  end waitInOffice


  def steal(): String =
    // Check AP
    if this.ap < 1 then
      return "AP Insufficient. Go sleep, you zombie."

    // Check Location
    if this.location.name != "teacherOffice" then
      return "You are not in the Office. What are you trying to steal? Air?"

    //Stress penalty
    this.apCost
    this.adjustMood(-5) // Basic anxiety penalty

    val status = this.location.teacherStatus
    val roll = scala.util.Random.nextDouble()

    val (successChance, resultReport) = status match
      case 0 => // Teacher Staring (10% chance)
        (0.1, this.stealAttempt(roll, 0.1))
      case 1 => // Teacher Napping (50% chance)
        (0.5, this.stealAttempt(roll, 0.5))
      case 2 => // Teacher Gone (80% chance)
        (0.80, this.stealAttempt(roll, 0.80))
      case _ =>
        (0.0, "Something glitchy happened. You backed off.")
    resultReport

  end steal


  private def stealAttempt(roll: Double, successThreshold: Double): String =
    if roll < successThreshold then
      // SUCCESS
      // Remove "Phone" from office inventory
      val phoneOption = this.office.removeItem("phone")

      phoneOption match
        case Some(phone) =>
          this.addItem(phone) // Add to player inventory
          // Indiana Jones style swap
          "MISSION ACCOMPLISHED.\n" +
          "You picked the lock of the Grade Head's drawer, grabbed your phone, " +
          "and swapped it with the dummy model you prepared.\nThe perfect crime."
        case None =>
          // Phone already taken or not there
          adjustMood(5)
          "You successfully stole... a Mock Exam Paper?\n" +
          "There was no phone here. But stealing *something* makes you feel productive.\n" +
          "You are truly a sick workaholic. (Mood +5)"
    else
      // FAILURE
      this.adjustMood(-15)
      "BUSTED!\n" +
      "Your hand was deep in the drawer when the teacher turned around.\n" +
      "Teacher smiles politely: 'Looking for something, Mr. Thief?'\n" +
      "(Mood -15, and pure terror.)"
  end stealAttempt

  def use(itemName: String): String =
    itemName match
      case "phone" =>
        if this.has("phone") then
          this.usePhoneQuickly() // 有手机，正常使用
        else
          this.adjustMood(-1) //因为太悲伤了，稍微扣点 Mood
          "Muscle memory takes over. Your hand automatically reaches for your pocket\n" +
          "...but grabs nothing but lint.\n" +
          "For a second, you swear you felt a vibration. It is 'Phantom Vibration Syndrome'.\n" +
          "You stare at your empty hand, thumb twitching, trying to scroll through invisible memes.\n" +
          "The digital withdrawal hits hard. (Mood -1)"

      case "book" | "vocabulary" =>
        if this.has(itemName) then
          "You flip through the vocabulary book. 'Abandon', 'Abnormal', 'Absent'... \nIt works better as a cover for your phone than for actual studying."
        else
          s"You don't have a $itemName."

      case _ =>
        // 对于其他物品，保持原有的通用检查
        if !this.has(itemName) then
          s"You don't have any '$itemName'!"
        else
          s"You try to use the $itemName, but nothing happens."

  private def usePhoneQuickly(): String =
    if this.ap < 1 then
      return "AP Insufficient. Go sleep, you zombie."
    // 如果在安全区域（宿舍），直接加分
    this.apCost
    if this.location.name == "dormitory" then
      this.adjustMood(5)
      "You lay on your bed and check your feed. Peaceful. (Mood +5)"

    // 如果在潜行状态，提示玩家用专属指令
    else if this.sneaking then
      "You are already sneaking! Use the specific app commands (playtiktok, chatqq, etc.) to get maximum joy!"
    //如果在危险区域（教室/办公室/走廊/食堂）直接用
    else if this.location.name == "teacherOffice" then
      val riskRoll = scala.util.Random.nextDouble()
      // 90% 概率被抓
      if riskRoll < 0.90 then
        // 没收!
        val bustMessage = this.confiscatePhone()
        bustMessage + "\n\n>>> You openly watched e-girl videos right in front of the teacher.\n" +
                      "The teacher froze, stunned by your sheer audacity, before slowly walking over to take your phone.\n" +
                      "They are definitely considering a Major Demerit."
      else
        // 10% 概率幸存,King of Phones!
        this.adjustMood(20)
        "The Grade Head is busy screaming at some other unlucky soul.\n" +
        "You seized the moment: checked all group chats AND took a risky selfie for your 'Moments'.\n" +
        "You are the King of Smartphones. Mood +20!"

    else
      val riskRoll = scala.util.Random.nextDouble()
      // 15% 概率被抓
      if riskRoll < 0.15 then
         this.confiscatePhone() // 没收！
      else
        val happyUse = 8 + Random.nextInt(5)  //
         this.adjustMood(happyUse)
         s"Risky move! You quickly checked WeChat while the teacher turned around.\nNo new messages. But the thrill gave you a rush. (Mood +${happyUse})"
      //丢掉的前置检查
    //if this.has("phone") then
      //return "你手机就在身上，没必要“偷”了。安心学习吧"
  
  //这些是以前的方法
  private def addItem(item: Item) =
    this.bag += item.name -> item

  private def removeItem(itemName: String) =
    this.bag.remove(itemName)

  def has(itemName: String) =
    this.bag.contains(itemName)
  /** Determines if the player has indicated a desire to quit the game. */
  def hasQuit = this.quitCommandGiven

  /** Returns the player’s current location. */
  def location = this.currentLocation

  def inventory: String =
    if !this.bag.isEmpty then
      val itemInBag = this.bag.keys.mkString("\n")
      "You are carrying:\n" + itemInBag
    else "You are empty-handed."

  def examine(itemName: String): String =
    //the player typed just look
    if itemName.isEmpty then
      // Office Surveillance
      if this.location.name == "teacherOffice" then
        // If in Office, reveal Teacher's Status instead of room description
        this.location.teacherStatus match
          case 0 => "STATUS: ALERT.\nThe teacher is briefing the Class Monitor and staring right at you.\n(Steal Chance: 10%. SUICIDE MISSION.)"
          case 1 => "STATUS: ASLEEP.\nThe teacher is face-down on the desk, drooling slightly.\n(Steal Chance: 50%. Proceed with caution.)"
          case 2 => "STATUS: CLEAR.\nThe seat is empty. The enemy has retreated to the restroom.\n(Steal Chance: 80%. GO! GO! GO!)"
          case _ => "CRITICAL BUG: The teacher exists in a quantum superposition.\n(If you see this, the game code is broken. Sumimasen!)"
      else
        // Not in office, Default look description
        this.location.fullDescription

    else
      //保留的旧逻辑
      if has(itemName) then
        val item = this.bag(itemName)
        s"You look closely at the ${item.name}.\n${item.description}"
      else
        "If you want to examine something, you need to pick it up first."
  end examine


  /** Attempts to move the player in the given direction. This is successful if there
    * is an exit from the player’s current location towards the direction name. Returns
    * a description of the result: "You go DIRECTION." or "You can't go DIRECTION." */
  def go(direction: String) =
    if this.ap < 1 then
      "AP Insufficient. Go sleep, you zombie."
    else
      val destination = this.location.neighbor(direction)
      this.currentLocation = destination.getOrElse(this.currentLocation)
      if destination.isDefined then
        this.apCost
        this.consecutiveMeals = 0
        s"You go $direction."

      else s"You can't go $direction."

  def get(itemName: String): String =

    // 偷窃系统补丁
    if itemName == "phone" && this.currentLocation == this.office then
      return "The teacher is right here! If you makes an attempt to 'get' it...She'll immediately kill u.\nTry STEAL it! To get your phone back, to fix your broken heart, save that lost soul!"
    // ---------------------

    if currentLocation.contains(itemName) then
      val itemInPlace = currentLocation.removeItem(itemName)
      itemInPlace match {
        case Some(newItem: Item) =>
          this.addItem(newItem)
          f"You pick up the $itemName."
      }
    else f"There is no $itemName here to pick up."

  def drop(itemName: String): String =
    val removedItem = this.removeItem(itemName)
    removedItem match
      case Some(item) =>  // 成功,物品在背包里
        this.currentLocation.addItem(item) // 添加到当前区域
        s"You drop the ${item.name}."
      case None =>      // 玩家背包里没有这个物品,失败的
        "You don't have that!"



  /** Causes the player to rest for a short while (this has no substantial effect in game terms).
    * Returns a description of what happened. */
  def rest() = "You rest for a while. Better get a move on, though."


  /** Signals that the player wants to quit the game. Returns a description of what happened
    * within the game as a result (which is the empty string, in this case). */
  def quit() =
    this.quitCommandGiven = true
    ""


  /** Returns a brief description of the player’s state, for debugging purposes. */
  override def toString = "Now at: " + this.location.name



end Player

