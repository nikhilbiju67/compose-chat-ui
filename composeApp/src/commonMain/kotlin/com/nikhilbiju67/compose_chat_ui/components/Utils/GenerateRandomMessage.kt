package com.nikhilbiju67.compose_chat_ui.components.Utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.TextMessage
import models.User
import kotlin.random.Random

fun generateRandomStringMessage(receiver: User): TextMessage {
    val messages = listOf(
        "Hello! How are you doing today?",
        "Let's catch up sometime soon.",
        "Don't forget to stay hydrated!",
        "Hope you have an amazing day!",
        "I'm here if you need anything.",
        "Looking forward to our meeting tomorrow.",
        "Thank you for your help earlier!",
        "Congratulations on your achievement!",
        "Wishing you all the best for your journey!",
        "Take care and stay safe!",
        "It's been a while, let's talk soon!",
        "How's everything going on your end?",
        "Sending good vibes your way!",
        "Hope you're having a productive day!",
        "Remember to take breaks and relax!",
        "Wishing you success in your work!",
        "Let's plan something fun this weekend.",
        "I appreciate all your support.",
        "You did an excellent job on that task!",
        "Keep shining, you're doing great!",
        "Take a deep breath, you’ve got this!",
        "Is there anything I can help you with?",
        "Let me know if you're free for a quick chat.",
        "Always here to lend a hand if needed.",
        "Your hard work doesn’t go unnoticed.",
        "Hope your day is filled with positivity!",
        "Keep pushing, success is near!",
        "You're stronger than you think!",
        "Never stop believing in yourself.",
        "Excited to hear from you soon!",
        "You're making great progress!",
        "Small steps lead to big achievements.",
        "Your dedication is truly inspiring!",
        "Let's celebrate your accomplishments soon!",
        "Stay motivated and keep going strong.",
        "Remember, every day is a fresh start.",
        "Your energy is contagious—keep spreading positivity!",
        "I'm so proud of everything you're achieving!",
        "Thank you for being such an amazing friend.",
        "You're capable of incredible things!",
        "Believe in your potential—you're unstoppable!",
        "Take time for yourself today.",
        "Let me know if you need someone to talk to.",
        "Every little effort counts—you're doing great!",
        "Let's aim for a better tomorrow, together.",
        "You're not alone—I'm here for you.",
        "Wishing you peace and happiness today!",
        "Your smile brightens everyone's day!",
        "Keep chasing your dreams—they’re within reach.",
        "Success is just around the corner!",
        "Enjoy every moment—you deserve it.",
        "Take things one step at a time.",
        "Be kind to yourself—you've earned it.",
        "Your resilience is admirable!",
        "Thanks for always being so reliable.",
        "You're a true inspiration!",
        "The world needs more people like you.",
        "Let's make today memorable!",
        "Sending lots of positivity your way!",
        "Hope your day is as amazing as you are!",
        "Stay awesome and keep rocking!",
        "Never underestimate your strength.",
        "You bring so much value to everyone around you.",
        "Your kindness makes the world a better place.",
        "Always believe something wonderful is about to happen.",
        "Take care, and don't hesitate to reach out!"
    )

    val randomMessage = messages.random()

    return TextMessage(
        messageContent = randomMessage,
        id = Random.nextInt(1000000).toString(),
        sentTo = emptyList(),
        sentBy = receiver,
        messageReactions = emptyList(),
        replyingToMessageId = null,
        sendAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
    )
}
