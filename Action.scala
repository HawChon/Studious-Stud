package o1.adventure

/** The class `Action` represents actions that a player may take in a text adventure game.
  * `Action` objects are constructed on the basis of textual commands and are, in effect,
  * parsers for such commands. An action object is immutable after creation.
  * @param input  a textual in-game command such as “go east” or “rest” */
class Action(input: String):

  private val commandText = input.trim.toLowerCase
  private val verb        = commandText.takeWhile( _ != ' ' )
  private val modifiers   = commandText.drop(verb.length).trim

  /** Causes the given player to take the action represented by this object, assuming
    * that the command was understood. Returns a description of what happened as a result
    * of the action (such as “You go west.”). The description is returned in an `Option`
    * wrapper; if the command was not recognized, `None` is returned. */
  //这里逻辑是这样的：先接受用户的指令，在这里匹配动作然后执行
  def execute(actor: Player): Option[String] =
    this.verb match
      
      case "study"   => Some(actor.study())
      case "eat"     => Some(actor.eat())
      case "sleep"   => Some(actor.sleep())
      case "relax"   => Some(actor.relax())
      case "use"   => Some(actor.use(this.modifiers))
      //sneak用的专属方法
      case "sneak"   => Some(actor.sneak())
      case "exit"    => Some(actor.exitSneak())
      // 潜行状态下的专属动词
      case "playtiktok"  => Some(actor.playTiktok())
      case "playgalgame" => Some(actor.playGalgame())
      case "chatqq"      => Some(actor.chatQQ())
      case "listenmusic" => Some(actor.listenMusic())
      case "fantasytalk" => Some(actor.fantasyTalk())
      case "reflecting" => Some(actor.reflecting())
      //给steal用的
      case "steal" => Some(actor.steal()) 
      case "wait"  => Some(actor.waitInOffice())
      case "look"  => Some(actor.examine(this.modifiers))
      //之前的指令
      case "go"        => Some(actor.go(this.modifiers))
      case "quit"      => Some(actor.quit())
      case "get"       => Some(actor.get(this.modifiers))
      case "drop"      => Some(actor.drop(this.modifiers))
      case "examine"   => Some(actor.examine(this.modifiers))
      case "inventory" => Some(actor.inventory)
      case other       => None

  /** Returns a textual description of the action object, for debugging purposes. */
  override def toString = s"$verb (modifiers: $modifiers)"

end Action

