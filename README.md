# Studious-Stud
This is my first small project, a text adventure! Even though it is just an assignment of Aalto's Scala course, but I assume it's interesting😀

Studious Stud is a text-based adventure game developed in Scala. The game simulates the intense life of a student over a 30-day period. The player must balance academic pressure (StudyPoints) with mental health (Mood) while navigating a hub-and-spoke map structure (Classroom, Dormitory, Dining Hall, etc.)

================================================================================
                       WALKTHROUGH: STUDIOUS STUD
================================================================================

--- GAME OBJECTIVE ---
Survive for 30 days in a high-pressure Chinese high school.
Victory: Reach Day 31 with Mood > 0.
Goal: Maximize your "StudyPoints" by managing your Mood and AP.

--- CORE MECHANICS ---
1. MOOD IS KEY: Keep Mood > 80 to get a 1.2x efficiency bonus on studying.
2. LOCATION MATTERS:
   - Classroom: Best for Studying (but allows Sneaking).
   - Dining Hall: Best for Eating (Restores Mood).
   - Dormitory: Best for Sleeping (Resets AP).
   - Teacher's Office: High risk, high reward (Study bonus or Steal phone).

--------------------------------------------------------------------------------
PART 1: THE WINNING STRATEGY (How to get High Score)
--------------------------------------------------------------------------------
To win easily, follow this daily routine:

[Morning]
1. You wake up in the Dormitory.
2. 'go dininghall' -> 'eat' 
   (Do this first to max out Mood for the 1.2x bonus).

[Daytime - The Grind]
3. 'go classroom'
4. 'study' (Repeat this 3-4 times).
   *Watch your Mood!* If Mood drops below 60, efficiency falls.

[Evening - Mood Recovery]
5. If Mood is low:
   - SAFE OPTION: 'go dininghall' -> 'eat' (again).
   - RISKY OPTION: 'sneak' (in Classroom). See Part 2 below.
   - LAZY OPTION: 'go dormitory' -> 'use phone' (Safe zone, Mood +5).

[Night]
6. 'go dormitory' -> 'sleep'
   (This resets your AP to 12 for the next day).

Repeat this cycle for 30 days.

--------------------------------------------------------------------------------
PART 2: FEATURE SHOWCASE (Must-Try Commands)
--------------------------------------------------------------------------------
Please try these specific scenarios to see the game's unique mechanics:

[A] THE SNEAK SYSTEM (In Classroom)
1. Type 'sneak'.
2. If successful, you enter "Sneak Mode".
3. Try specific sneak commands: 'playtiktok', 'chatqq', 'listenmusic'.
4. Type 'exit' to leave sneak mode.
   (Note: There is a chance to be caught and have your phone confiscated!)
5. SNEAKING WITHOUT A PHONE (Phantom Mode):
   Even if your phone is confiscated, you can still use the 'sneak' command!
   - Try 'playtiktok' or 'chatqq' to see the hilarious "Phantom Pain" flavor texts.
   - BONUS: If you are caught sneaking while empty-handed, you won't be punished. 
     In fact, seeing the teacher's awkward reaction might actually cheer you up!
     (Note: Entering sneak mode still costs 1 AP).

[B] THE "USE" COMMAND & PHANTOM PAIN
1. In the Dormitory: Type 'use phone' (Safe, Mood +5).
2. In the Corridor/Classroom: Type 'use phone' (Risky! 15% chance to get caught).
3. IF YOUR PHONE IS CONFISCATED:
   Type 'use phone' anyway. You will see a special "Phantom Pain" flavor text.

[C] THE HEIST (Stealing the Phone Back)
If your phone is confiscated, you must steal it back from the Office.
1. 'go teacherOffice'
2. 'look' -> Check the Teacher's Status.
   - "Staring at screen" = 10% Success (Do not steal!)
   - "Napping" = 50% Success (Risky)
   - "Gone to restroom" = 90% Success (Safer to steal!)
3. If the status is bad, type 'wait' (Costs 1 AP) to shuffle the status.
4. When status is "Clear/Restroom", type 'steal'.

--------------------------------------------------------------------------------
CHEAT SHEET (COMMANDS)
--------------------------------------------------------------------------------
- move:      go [classroom/dormitory/dininghall/corridor/office]
- action:    study, eat, sleep, relax
- items:     use phone, get [item], drop [item], inventory
- special:   sneak, steal, wait
- meta:      help, quit



[NOTE FOR GRADING]
The game balance is tuned for a 30-day survival challenge. 
If you wish to expedite the verification process, you can focus on testing the mechanics 
in the Classroom (Sneak) and Office (Steal) described in Part 2, as these contain 
the most complex logic.
